/**
 * Обработчик сообщений, загруженных из таблицы <platform>/sakura.messages
 *
 * 13.07.2018 by Alex Tues
 * 18.07.2018
 */
package com.tequila.core;

import com.tequila.common.Constants;
import com.tequila.config.SnailExpressConfig;
import com.tequila.data.domain.platform.Messages;
import com.tequila.data.domain.platform.Retranslators;
import com.tequila.data.domain.transfer.TransferIps;
import com.tequila.sender.IPSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
public class MessagesHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesHandler.class);

    @Autowired
    private SnailExpressConfig snailExpressConfig;
    @Autowired
    private MessagesScanner messagesScanner;
    @Autowired
    private IPSSender ipsSender;

    // Справочники
    private Map<Long, Set<Long>> bindingCache;
    private List<Retranslators> retranslatorsList;

    // Текущее (обрабатываемое) сообщение
    private Messages message;

    // Каждое сообщение из списка должно быть подготовлено в соответствии с его протоколом ретрансляции.
    // Затем сообщение должно быть сохранено в таблице <transfer>/delivery.t_*
    // (где "*" обозначает протокол ретрансляции сообщений: ips, teltonika, galileo и т.д.) и либо
    // ретранслировано, либо оставлено без ретрансляции.
    public void handler(List<Messages> messagesList) {
        int delivery_dead_time   = snailExpressConfig.getDELIVERY_DEAD_TIME(),
            delivery_max_attempt = snailExpressConfig.getDELIVERY_MAX_ATTEMPT();

        retranslatorsList = messagesScanner.getRetranslatorsList();
        bindingCache = messagesScanner.getBindingCache();

        // Список подготовленных для ретрансляции сообщений по протоколу Wialon IPS
        // (аналогично и для других протоколов)
        List<TransferIps> transferIpsList = new ArrayList<>();
        for(int i = 0; i < messagesList.size(); i++) {
            // Текущее (очередное) сообщение
            message = messagesList.get(i);

            Long imeiCode = message.getImei();
            String protocol, filter;
            Boolean sendflags, sendgeodata, sendin, sendout;
            Integer port;
            String host;

            // Множество ретрансляторов для текущего сообщения
            Set<Long> retranslatorsForImei = bindingCache.get(imeiCode);
            // Сообщению может соответствовать несколько ретрансляторов и для
            // каждого из них необходимо сформировать отдельную запись.
            // Поэтому, как правило, количество сообщений для ретрансляции больше
            // числа сообщений, загруженных из таблицы <platform>/sakura.messages
            Iterator<Long> it = retranslatorsForImei.iterator();
            while (it.hasNext()) {
                long retranslatorId = it.next();
                // Данные текущего ретранслятора
                Retranslators retranslator = getRetranslator(retranslatorId);
                // В таблице соответствия связь между imei-кодом и ретранслятором есть,
                // но самого ретранслятора в таблице ретрансляторов может и не быть
                if(retranslator == null) {
                    LOGGER.info("[MessagesHandler] not found retranslator id={} for message {}", retranslatorId, message.toString());
                    continue;
                }
                protocol = retranslator.getProtocol();
                filter = retranslator.getFilter();
                sendflags = retranslator.getSendflags();
                sendgeodata = retranslator.getSendgeodata();
                sendin = retranslator.getSendin();
                sendout = retranslator.getSendout();
                port = retranslator.getPort();
                host = retranslator.getHost();
                if(protocol == null || protocol.isEmpty() || host == null || host.isEmpty() || port == null || port <= 0) {
                    LOGGER.info("[MessagesHandler] check <protocol>, <port> and <host> fields for retranslator id={}", retranslator.getId());
                    continue;
                }
                if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                    LOGGER.info("[MessagesHandler] process message id={} (imei={}), retranslator={}: name={}, host={}, port={}, protocol={}",
                            message.getId(),
                            imeiCode,
                            retranslator.getId(),
                            retranslator.getName(),
                            retranslator.getHost(),
                            retranslator.getPort(),
                            retranslator.getProtocol());
                }

                // Подготовить сообщение в соответствии с протоколом ретрансляции
                switch(protocol.toUpperCase()) {
                    case "IPS": case "WIALON":
                        TransferIps tips = new TransferIps();
                        tips.setId_message(message.getId());
                        tips.setId_retranslator(retranslatorId);
                        tips.setImei(message.getImei());
                        tips.setPort(port);
                        tips.setHost(host);
                        tips.setLpackage(createIPSLoginPackage(retranslator.getPassword()));
                        tips.setDpackage(createIPSDataPackage(filter));
                        long currentTime = new Date().getTime();
                        tips.setTimestamp_create(new Timestamp(currentTime));
                        tips.setTimestamp_send(null);
                        tips.setTimestamp_stop(new Timestamp(currentTime + delivery_dead_time));
                        tips.setAttempt(0);
                        tips.setAttempt_limit(delivery_max_attempt);
                        tips.setDelivered(false);
                        // Сохранить сообщение в списке для ретрансляции
                        transferIpsList.add(tips);
                        break;
                    case "GALILEO": case "TELTONIKA": case "EVENT":
                        LOGGER.info("[MessagesHandler] protocol {} has not yet been implemented", protocol);
                        break;
                    default:
                        LOGGER.info("[MessagesHandler] unknown protocol {}", protocol);
                        break;
                }
            }
        }

        // Список сообщений для ретрансляции по протоколу Wialon IPS сформирован
        if(!transferIpsList.isEmpty()) {
            // Отсортировать по коду ретранслятора и imei-коду
            // (для возможной экономии ресурсов на открытие сокетов при передаче)
            Collections.sort(transferIpsList, new Comparator<TransferIps>() {
                @Override
                public int compare(TransferIps o1, TransferIps o2) {
                    Long value1 = o1.getId_retranslator() + o1.getImei();
                    Long value2 = o2.getId_retranslator() + o2.getImei();
                    return value1.compareTo(value2);
                }
            });

            // Ретранслировать новые сообщения
            LOGGER.info("[MessagesSending] send {} new messages", transferIpsList.size());
            ipsSender.executeTask(transferIpsList, true);
        }
    }

    // Найти ретранслятор по идентификатору
    private Retranslators getRetranslator(long id) {
        for(int i = 0; i < retranslatorsList.size(); i++) {
            if(retranslatorsList.get(i).getId() == id) return retranslatorsList.get(i);
        }
        return null;
    }

    // Сформировать пакет логина по протоколу IPS
    private String createIPSLoginPackage(String password) {
        return new StringBuilder()
                .append(Constants.IPS_LOGIN_PREFIX)
                .append(message.getImei())
                .append(";")
                .append((password == null || password.isEmpty() ? "NA" : password))
                .toString();
    }

    // Сформировать пакет данных по протоколу IPS
    private String createIPSDataPackage(String filter) {
        return new StringBuilder()
                .append(Constants.IPS_DATA_PREFIX)
                .append(MessagesUtils.getDateAndTimeIps(message.getTimestamp_create())).append(";")
                .append(MessagesUtils.getNMEALatitudeIps(message.getLat())).append(";")
                .append(MessagesUtils.getNMEALongitudeIps(message.getLon())).append(";")
                .append(MessagesUtils.getSpeedIps(message.getSpeed())).append(";")
                .append(MessagesUtils.getCourseIps(message.getCourse())).append(";")
                .append(MessagesUtils.getHeightIps(message.getAltitude())).append(";")
                .append(MessagesUtils.getSatellitesIps(message.getSatellites_count())).append(";")
                .append(MessagesUtils.getHdopIps(message.getHdop())).append(";")
                .append(MessagesUtils.getInputsIps(message.getInput())).append(";")
                .append(MessagesUtils.getOutputsIps(message.getOutput())).append(";")
                .append(MessagesUtils.getAdcIps()).append(";")
                .append(MessagesUtils.getIbuttonIps()).append(";")
                .append(MessagesUtils.getParamsIps(filter, message.getParams()))
                .toString();
    }
}
