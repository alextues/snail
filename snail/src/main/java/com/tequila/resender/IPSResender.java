/**
 * Переретрансляция (переотправка) сообщений по протоколу Wialon IPS
 *
 * 10.07.2018 by Alex Tues
 * 17.07.2018
 */

package com.tequila.resender;

import com.tequila.common.Constants;
import com.tequila.config.SnailExpressConfig;
import com.tequila.data.domain.transfer.TransferIps;
import com.tequila.data.repository.TransferRepository;
import com.tequila.sender.IPSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Component
public class IPSResender {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPSResender.class);

    @Resource
    private TransferRepository transferRepository;
    @Autowired
    private SnailExpressConfig snailExpressConfig;
    @Autowired
    private IPSSender ipsSender;

    // Идентификатор последнего недоставленного сообщения, начиная с которого нужно производить очередную выборку
    private long scanFromId = Constants.ZERO;

    /**
     * Выборка и подготовка данных для ретрансляции недоставленных сообщений
     *
     * Таблица сообщений <transfer>/delivery.t_ips циклически сканируется, начиная с самого
     * первого недоставленного сообщения. После попытки ретрансляции последнего недоставленного
     * сообщения, цикл сканирования возобновляется с самого начала.
     */
    @Scheduled(initialDelayString = "#{T(java.lang.Long).valueOf(snailExpressConfig.getREDELIVERY_DELAY())}",
               fixedDelayString = "#{T(java.lang.Long).valueOf(snailExpressConfig.getREDELIVERY_TIMEOUT())}")
    public void rescanner() {
        // Переретрансляция отключена; выйти из метода
        if(!snailExpressConfig.getREDELIVERY_MESSAGES()) {
            return;
        }

        /**
         *
         * Получить список недоставленных сообщений по протоколу Wialon IPS
         *
         * Критерии отбора:
         * - сообщение не доставлено (<transfer>/delivery.t_ips.enabled != true);
         * - не закончился срок доставки (текущая отметка времени меньше <transfer>/delivery.t_ips.timestamp_stop);
         * - полностью не использован лимит попыток доставки (<transfer>/delivery.t_ips.attempt меньше <transfer>/delivery.t_ips.attempt_limit);
         * - объем выборки ограничивается параметром из файла свойств
         */
        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        // Выборка недоставленных сообщений из таблицы <transfer>/delivery.t_ips
        List<TransferIps> undeliveredIpsList = transferRepository.selectUndeliveredIPSMessages(
                scanFromId,
                false,
                currentTimestamp,
                snailExpressConfig.getREDELIVERY_LIMIT());
        // Нет недоставленных сообщений или нет недоставленных сообщений, удовлетворяющих критериям отбора
        if(undeliveredIpsList.isEmpty()) {
            LOGGER.info("[IPSResender] nothing to resend");
            scanFromId = Constants.ZERO;
            return;
        }
        // Есть недоставленные сообщения
        else {
            // Переретранслировать сообщения
            LOGGER.info("[IPSResender] resending {} messages", undeliveredIpsList.size());
            ipsSender.executeTask(undeliveredIpsList, false);
            // Скорректировать идентификатор, начиная с которого нужно производить следующую выборку
            scanFromId = undeliveredIpsList.get(undeliveredIpsList.size() - 1).getId();
        }
    }
}
