/**
 * Подготовительные действия перед началом рабочего цикла: выборка -> обработка -> ретрансляция
 *
 * 12.07.2018 by Alex Tues
 * 06.08.2018
 */
package com.tequila.common;

import com.tequila.config.SnailExpressConfig;
import com.tequila.data.domain.transfer.TransferLoads;
import com.tequila.data.repository.PlatformRepository;
import com.tequila.data.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

@Component
public class Prelude {
    private static final Logger LOGGER = LoggerFactory.getLogger(Prelude.class);

    @Resource
    private PlatformRepository platformRepository;
    @Resource
    private TransferRepository transferRepository;
    @Autowired
    private SnailExpressConfig snailExpressConfig;

    /**
     * Подготовительные операции которые должны быть выполнены до запуска
     * основного цикла обработки сообщений телематической платформы:
     *
     * - считывание и инициализация свойств;
     * - инициализация (если необходимо) таблицы <transfer>/delivery.t_loads
     *   (учет идентификаторов обработанных записей из <platform>/sakura.messages).
     *
     * Пустая таблица <transfer>/delivery.t_loads означает, что это - самый первый запуск
     * и никакие сообщения телематической платформы ранее не обрабатывались.
     * Для того, чтобы избежать полного сканирования таблицы <platform>/sakura.messages,
     * необходимо указать значение идентификатора, начиная с которого будут выбираться
     * сообщения для последующей ретрансляции.
     */
    public void prelude() {
        LOGGER.info("[Prelude] started at {}", Constants.FULL_DATE_FORMAT.format(new Date()));
        printCurrentProperties();
        LOGGER.info("[Prelude] check initial value for <transfer>/delivery.t_loads");
        long count = countTransferLoadsRecords();

        if(count == Constants.ZERO) {
            LOGGER.info("[Prelude] table <transfer>/delivery.t_loads is empty");
            LOGGER.info("[Prelude] prepare initial values for table <transfer>/delivery.t_loads is empty");
            prepareTransferLoads();
            LOGGER.info("[Prelude] table <transfer>/delivery.t_loads updated by initial values");
        }
    }

    // Распечатать текущие свойства
    private void printCurrentProperties() {
        LOGGER.info("[Prelude] current properties");
        LOGGER.info("         INIT_PLATFORM_ID: {}", snailExpressConfig.getINIT_PLATFORM_ID());
        LOGGER.info("      SCAN_MESSAGES_DELAY: {}", snailExpressConfig.getSCAN_MESSAGES_DELAY());
        LOGGER.info("    SCAN_MESSAGES_TIMEOUT: {}", snailExpressConfig.getSCAN_MESSAGES_TIMEOUT());
        LOGGER.info("        SCAN_NEW_MESSAGES: {}", snailExpressConfig.getSCAN_NEW_MESSAGES());
        LOGGER.info("      SCAN_LIMIT_MESSAGES: {}", snailExpressConfig.getSCAN_LIMIT_MESSAGES());
        LOGGER.info("        DELIVERY_MESSAGES: {}", snailExpressConfig.getDELIVERY_MESSAGES());
        LOGGER.info("       DELIVERY_DEAD_TIME: {}", snailExpressConfig.getDELIVERY_DEAD_TIME());
        LOGGER.info("     DELIVERY_MAX_ATTEMPT: {}", snailExpressConfig.getDELIVERY_MAX_ATTEMPT());
        LOGGER.info("      REDELIVERY_MESSAGES: {}", snailExpressConfig.getREDELIVERY_MESSAGES());
        LOGGER.info("         REDELIVERY_DELAY: {}", snailExpressConfig.getREDELIVERY_DELAY());
        LOGGER.info("       REDELIVERY_TIMEOUT: {}", snailExpressConfig.getREDELIVERY_TIMEOUT());
        LOGGER.info("         REDELIVERY_LIMIT: {}", snailExpressConfig.getREDELIVERY_LIMIT());
        LOGGER.info("     REFS_REFRESH_COUNTER: {}", snailExpressConfig.getREFS_REFRESH_COUNTER());
        LOGGER.info("                     MODE: {}", snailExpressConfig.getMODE());
        if(snailExpressConfig.getMODE().equalsIgnoreCase("DEBUG")) {
            LOGGER.info("            DEBUG_ADDRESS: {}:{}", snailExpressConfig.getDEBUG_HOST(), snailExpressConfig.getDEBUG_PORT());
        }
        LOGGER.info("           HIGH_LOG_LEVEL: {}", snailExpressConfig.getHIGHT_LOG_LEVEL());
        LOGGER.info("     AVAILABLE PROCESSORS: {}", Runtime.getRuntime().availableProcessors());
    }

    // Количество записей в таблице <transfer>/delivery.t_loads
    private long countTransferLoadsRecords() {
        return transferRepository.selectTransferLoadsCount().get();
    }

    // Вставить в таблицу <transfer>/delivery.t_loads инициализирующую запись
    private void prepareTransferLoads() {
        long launchId = snailExpressConfig.getINIT_PLATFORM_ID();

        if(launchId <= Constants.ZERO) {
            launchId = platformRepository.selectMessagesMaxId().get();
        }
        TransferLoads transferLoads = new TransferLoads();
        transferLoads.setFid(Constants.ZERO);
        transferLoads.setTid(launchId - Constants.ONE);
        transferLoads.setCount(Constants.ZERO);
        transferLoads.setLoaded(new Timestamp(new Date().getTime()));
        transferRepository.getRepository(TransferLoads.class).save(transferLoads);
    }
}
