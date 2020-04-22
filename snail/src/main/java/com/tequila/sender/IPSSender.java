/**
 * Ретрансляция (отправка) сообщений по протоколу Wialon IPS
 *
 * 04.07.2018 by Alex Tues
 * 09.07.2018
 * 16.07.2018
 * 02.08.2018
 * 07.08.2018
 */
package com.tequila.sender;

import com.tequila.common.Constants;
import com.tequila.data.domain.transfer.TransferConfirm;
import com.tequila.helper.LoggingHelper;
import com.tequila.config.SnailExpressConfig;
import com.tequila.data.domain.transfer.TransferIps;
import com.tequila.data.repository.TransferRepository;
import com.tequila.helper.SocketHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
public class IPSSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPSSender.class);

    @Resource
    private TransferRepository transferRepository;
    @Autowired
    private SnailExpressConfig snailExpressConfig;
    @Autowired
    private Executor taskExecutor;
    @Autowired
    private SocketHelper socketHelper;
    @Autowired
    private LoggingHelper logging;

    /**
     * Ретранслировать сообщения из списка. Значение флага isNew указывает на
     * "происхождение" сообщения: если true - сообщение новое и ранее не ретранслировалось;
     * если false - сообщение уже было ранее неудачно ретранслировано и ретрансляцию
     * необходимо повторить
     */
    public void executeTask(List<TransferIps> transferIpsList, boolean isNew) {
        // Ретранслировать сообщения
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Если ретрансляция новых сообщений выключена, то только сохранить
                // сообщения в таблице <transfer>/delivery.t_ips и выйти из метода
                if(isNew && !snailExpressConfig.getDELIVERY_MESSAGES()) {
                    saveIPSMessages(transferIpsList, isNew);
                    return;
                }

                // Подготовить коллекцию открытых на момент запуска метода сокетов
                Map<String, Socket> socketMap = socketHelper.createSocketMap();
                // Если коллекция открытых сокетов пуста (т.е. не удалось создать соединений
                // с удаленными хостами-приемниками сообщений), то (пере)ретрансляция отменяется;
                // в этом случае остается только обновить необходимые поля и записать в лог
                if(socketMap.isEmpty()) {
                    LOGGER.info("[IPSSender] no available sockets");
                }

                // Подготовить карту соответствия ретрансляторов и признаков ожидания подтверждений при отправке данных
                Map<Long, Boolean> confirmMap = getConfirms();

                // Перебрать все сообщения из списка и попытаться их отправить/переотправить
                for (int i = 0; i < transferIpsList.size(); i++) {
                    String host;
                    int port;
                    Socket socket;
                    String comment;
                    // Для режима отладки все сообщения принудительно перенаправить на отладочный узел
                    if(snailExpressConfig.getMODE().equalsIgnoreCase("DEBUG")) {
                        host = snailExpressConfig.getDEBUG_HOST();
                        port = snailExpressConfig.getDEBUG_PORT();
                        socket = getSocket(host, port, socketMap);
                        comment = String.format("debug send message id=%s to %s:%d", transferIpsList.get(i).getId_message(), host, port);
                    }
                    else {
                        host = transferIpsList.get(i).getHost();
                        port = transferIpsList.get(i).getPort();
                        socket = getSocket(host, port, socketMap);
                        comment = String.format("send message id=%s to %s:%d", transferIpsList.get(i).getId_message(), host, port);
                    }
                    try {
                        // Подходящего открытого сокета нет
                        if (socket == null) {
                            logging.logIps(transferIpsList.get(i), isNew, false, "FAIL:" + comment + " [no open socket]");
                            continue;
                        }
                        // Подходящий открытый сокет найден
                        socket.setSoTimeout(Constants.SOCKET_WAIT_TIMEOUT);
                        if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                            LOGGER.info("[IPSSender] {}", comment);
                        }
                        // Логин принят
                        if (isLoginCorrect(socket, transferIpsList.get(i).getLpackage())) {
                            if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                                LOGGER.info("[IPSSender] login package {} -> OK", transferIpsList.get(i).getLpackage());
                            }
                            // Вернулось подтверждение получения пакета данных;
                            // изменить в сообщении значение поля delivered на true
                            if (isDataCorrect(socket, transferIpsList.get(i).getDpackage())) {
                                if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                                    LOGGER.info("[IPSSender] data package {} -> OK", transferIpsList.get(i).getDpackage());
                                }
                                transferIpsList.get(i).setDelivered(true);
                                logging.logIps(transferIpsList.get(i), isNew,true, "OK:" + comment);
                            }
                            // Нет подтверждения получения пакета данных; это еще не означает, что данные не приняты
                            else {
                                // Нужно ли ожидать подтверждение отправки данных?
                                boolean confirm = false;
                                if(confirmMap.containsKey(transferIpsList.get(i).getId_retranslator())) {
                                    confirm = confirmMap.get(transferIpsList.get(i).getId_retranslator());
                                }
                                if(confirm == true) {
                                    // Необходимо подтверждение отправки данных
                                    if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                                        LOGGER.info("[IPSSender] data package {} -> not OK", transferIpsList.get(i).getDpackage());
                                    }
                                    logging.logIps(transferIpsList.get(i), isNew,false, "FAIL:" + comment + " [data package not delivered]");
                                } else {
                                    // Подтверждение отправки данных не нужно, данные отправлены;
                                    // изменить в сообщении значение поля delivered на true
                                    if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                                        LOGGER.info("[IPSSender] data package {} -> OK", transferIpsList.get(i).getDpackage());
                                    }
                                    transferIpsList.get(i).setDelivered(true);
                                    logging.logIps(transferIpsList.get(i), isNew,true, "OK:" + comment);
                                }
                            }
                        }
                        // Логин не принят и все сообщение не доставлено
                        else {
                            if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                                LOGGER.info("[IPSSender] login package {} -> not OK", transferIpsList.get(i).getLpackage());
                            }
                            logging.logIps(transferIpsList.get(i), isNew, false, "FAIL:" + comment + " [login package not delivered]");
                        }
                    } catch (SocketException ex) {
                        logging.logIps(transferIpsList.get(i), isNew, false, "FAIL:" + " [socket exception]");
                        LOGGER.info("[IPSSender] {}", ex.toString());
                    }
                }

                // Коллекция обработана; сохранить
                if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
                    LOGGER.info("[IPSSender] save {} messages", transferIpsList.size());
                }
                saveIPSMessages(transferIpsList, isNew);

                // Закрыть все сокеты
                socketHelper.closeSocketMap(socketMap);
                // Подождать перед завершением (сброс и очистка буферов)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
        });
    }

    // Сохранить новые сообщения по протоколу Wialon IPS
    private void saveIPSMessages(List<TransferIps> transferIpsList, boolean isNew) {
        for(int i = 0; i < transferIpsList.size(); i++) {
            TransferIps tips = new TransferIps();
            // Для ранее отправленных сообщений не создавать новой записи
            if(!isNew) {
                tips.setId(transferIpsList.get(i).getId());
            }
            tips.setId_message(transferIpsList.get(i).getId_message());
            tips.setId_retranslator(transferIpsList.get(i).getId_retranslator());
            tips.setImei(transferIpsList.get(i).getImei());
            tips.setPort(transferIpsList.get(i).getPort());
            tips.setHost(transferIpsList.get(i).getHost());
            tips.setLpackage(transferIpsList.get(i).getLpackage());
            tips.setDpackage(transferIpsList.get(i).getDpackage());
            tips.setTimestamp_create(transferIpsList.get(i).getTimestamp_create());
            tips.setTimestamp_send(new Timestamp(new Date().getTime()));
            tips.setTimestamp_stop(transferIpsList.get(i).getTimestamp_stop());
            tips.setAttempt(transferIpsList.get(i).getAttempt() + 1);
            tips.setAttempt_limit(transferIpsList.get(i).getAttempt_limit());
            tips.setDelivered(transferIpsList.get(i).getDelivered());
            transferRepository.getRepository(TransferIps.class).save(tips);
        }
    }

    // Отослать пакет логина
    private boolean isLoginCorrect(Socket s, String lpackage) {
        // К моменту передачи данных сокет может "отвалиться"
        if(s == null || s.isClosed()) {
            LOGGER.info("[IPSender] socket suddenly closed");
            return false;
        }
        try {
            s.getOutputStream().write((lpackage + Constants.TERMINATOR).getBytes());
            byte[] bufLogin = new byte[1024];
            int rLogin = s.getInputStream().read(bufLogin);
            String responseLogin = new String(bufLogin, 0, rLogin);
            // Логин принят
            return responseLogin.contains(Constants.IPS_LOGIN_OK);
        }
        catch(IOException ex) {}
        return false;
    }

    // Отослать пакет данных
    private boolean isDataCorrect(Socket s, String dpackage) {
        // К моменту передачи данных сокет может "отвалиться"
        if(s == null || s.isClosed()) {
            LOGGER.info("[IPSender] socket suddenly closed");
            return false;
        }
        try {
            s.getOutputStream().write((dpackage + Constants.TERMINATOR).getBytes());
            byte[] bufData = new byte[1024];
            int rData = s.getInputStream().read(bufData);
            String responseData = new String(bufData, 0, rData);
            // Пакет данных принят
            return responseData.contains(Constants.IPS_DATA_OK);
        }
        catch(IOException ex) {}
        return false;
    }

    // Получить из коллекции открытых сокетов нужный сокет
    private synchronized Socket getSocket(String host, int port, Map<String, Socket> socketMap) {
        String key = host + ":" + port;
        if(socketMap.containsKey(key)) {
            return socketMap.get(key);
        }
        return null;
    }

    // Сформировать карту соответствия ретрансляторов и признаков ожидания подтверждений при отправке данных
    private synchronized Map<Long, Boolean> getConfirms() {
        List<TransferConfirm> allConfirms = (List<TransferConfirm>)transferRepository.findAll(TransferConfirm.class);

        Map<Long, Boolean> confirmMap = new HashMap<>();
        if(!allConfirms.isEmpty()) {
            for(int i = 0; i < allConfirms.size(); i++) {
                confirmMap.put(allConfirms.get(i).getId_retranslator(), allConfirms.get(i).getConfirm());
            }
        } else {
            LOGGER.info("[IPSSender] confirmation table is empty; add stub value");
            confirmMap.put(Constants.ZERO, false);
        }

        return confirmMap;
    }
}
