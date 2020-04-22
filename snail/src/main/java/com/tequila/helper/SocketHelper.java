/**
 * Работа с коллекцией доступных сокетов для ретрансляции сообщений
 *
 * 09.07.2018 by Alex Tues
 * 16.07.2018
 * 20.07.2018
 */
package com.tequila.helper;


import com.tequila.common.Constants;
import com.tequila.config.SnailExpressConfig;
import com.tequila.data.domain.platform.Retranslators;
import com.tequila.data.repository.PlatformRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SocketHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketHelper.class);

    @Resource
    private PlatformRepository platformRepository;
    @Autowired
    private SnailExpressConfig snailExpressConfig;

    /**
     * Сформировать коллекцию доступных сокетов.
     *
     * Структура коллекции:
     * ключ - хост:порт
     * значение - сокет
     * (значения null в коллекцию не включаются).
     */
    public synchronized Map<String, Socket> createSocketMap() {
        Map<String, Socket> socketMap = new HashMap<>();

        // Если включен режим отладки, то в коллекции содержится только один сокет
        if(snailExpressConfig.getMODE().equalsIgnoreCase("DEBUG")) {
            String debugHost = snailExpressConfig.getDEBUG_HOST();
            int debugPort = snailExpressConfig.getDEBUG_PORT();
            Socket debugSocket = openSocket(debugHost, debugPort);
            if(debugSocket != null) {
                socketMap.put(debugHost + ":" + debugPort, debugSocket);
            }
            return socketMap;
        }

        // Коллекция сокетов формируется по всему списку ретрансляторов
        // (для переретрансляции ранее не отправленных сообщений)
        List<Retranslators> allRetranslators = (List<Retranslators>)platformRepository.findAll(Retranslators.class);
        if(!allRetranslators.isEmpty()) {
            for(int i = 0; i < allRetranslators.size(); i++) {
                String host = allRetranslators.get(i).getHost();
                int port = allRetranslators.get(i).getPort();
                Socket socket = openSocket(host, port);
                if(socket != null) {
                    socketMap.put(host + ":" + port, socket);
                }
            }
        }
        return socketMap;
    }

    // Открыть сокет и добавить его в коллекцию
    private Socket openSocket(String host, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), Constants.SOCKET_OPEN_TIMEOUT);
            LOGGER.info("[SocketHelper] socket {}:{} opened", host, port);
            return socket;
        }
        catch(IOException ex) {
            LOGGER.info("[SocketHelper] socket {}:{} not opened", host, port);
            return null;
        }
    }

    // Закрыть все сокеты из коллекции
    public synchronized void closeSocketMap(Map<String, Socket> socketMap) {
        if(socketMap.size() != 0) {
            try {
                for (Map.Entry<String, Socket> entry : socketMap.entrySet()) {
                    Socket socket = entry.getValue();
                    socket.close();
                }
            }
            catch(IOException ex) {}
            finally {
                socketMap.clear();
            }
        }
    }
}
