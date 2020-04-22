/**
 * Текущая конфигурация
 *
 * Свойства загружаются из файла свойств sex.properties.
 *
 * 13.06.2018 by Alex Tues
 * 02.07.2018
 * 10.07.2018
 * 12.07.2018
 * 19.07.2018
 */
package com.tequila.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sex.properties")
public class SnailExpressConfig {
    @Value("${INIT_PLATFORM_ID}")
    private long INIT_PLATFORM_ID;
    public void setINIT_PLATFORM_ID(long INIT_PLATFORM_ID) {
        this.INIT_PLATFORM_ID = INIT_PLATFORM_ID;
    }
    public long getINIT_PLATFORM_ID() {
        return INIT_PLATFORM_ID;
    }

    @Value("${SCAN_MESSAGES_DELAY}")
    private long SCAN_MESSAGES_DELAY;
    public void setSCAN_MESSAGES_DELAY(long SCAN_MESSAGES_DELAY) {
        this.SCAN_MESSAGES_DELAY = SCAN_MESSAGES_DELAY;
    }
    public long getSCAN_MESSAGES_DELAY() {
        return SCAN_MESSAGES_DELAY;
    }

    @Value("${SCAN_MESSAGES_TIMEOUT}")
    private long SCAN_MESSAGES_TIMEOUT;
    public void setSCAN_MESSAGES_TIMEOUT(long SCAN_MESSAGES_TIMEOUT) {
        this.SCAN_MESSAGES_TIMEOUT = SCAN_MESSAGES_TIMEOUT;
    }
    public long getSCAN_MESSAGES_TIMEOUT() {
        return SCAN_MESSAGES_TIMEOUT;
    }

    @Value("${SCAN_NEW_MESSAGES}")
    private boolean SCAN_NEW_MESSAGES;
    public void setSCAN_NEW_MESSAGES(boolean SCAN_NEW_MESSAGES) {
        this.SCAN_NEW_MESSAGES = SCAN_NEW_MESSAGES;
    }
    public boolean getSCAN_NEW_MESSAGES() {
        return SCAN_NEW_MESSAGES;
    }

    @Value("${SCAN_LIMIT_MESSAGES}")
    private int SCAN_LIMIT_MESSAGES;
    public void setSCAN_LIMIT_MESSAGES(int SCAN_LIMIT_MESSAGES) {
        this.SCAN_LIMIT_MESSAGES = SCAN_LIMIT_MESSAGES;
    }
    public int getSCAN_LIMIT_MESSAGES() {
        return SCAN_LIMIT_MESSAGES;
    }

    @Value("${DELIVERY_MESSAGES}")
    private boolean DELIVERY_MESSAGES;
    public void setDELIVERY_MESSAGES(boolean DELIVERY_MESSAGES) {
        this.DELIVERY_MESSAGES = DELIVERY_MESSAGES;
    }
    public boolean getDELIVERY_MESSAGES() {
        return DELIVERY_MESSAGES;
    }

    @Value("${DELIVERY_DEAD_TIME}")
    private int DELIVERY_DEAD_TIME;
    public void setDELIVERY_DEAD_TIME(int DELIVERY_DEAD_TIME) {
        this.DELIVERY_DEAD_TIME = DELIVERY_DEAD_TIME;
    }
    public int getDELIVERY_DEAD_TIME() {
        return DELIVERY_DEAD_TIME;
    }

    @Value("${DELIVERY_MAX_ATTEMPT}")
    private int DELIVERY_MAX_ATTEMPT;
    public void setDELIVERY_MAX_ATTEMPT(int DELIVERY_MAX_ATTEMPT) {
        this.DELIVERY_MAX_ATTEMPT = DELIVERY_MAX_ATTEMPT;
    }
    public int getDELIVERY_MAX_ATTEMPT() {
        return DELIVERY_MAX_ATTEMPT;
    }

    @Value("${REDELIVERY_MESSAGES}")
    private boolean REDELIVERY_MESSAGES;
    public void setREDELIVERY_MESSAGES(boolean REDELIVERY_MESSAGES) {
        this.REDELIVERY_MESSAGES = REDELIVERY_MESSAGES;
    }
    public boolean getREDELIVERY_MESSAGES() {
        return REDELIVERY_MESSAGES;
    }

    @Value("${REDELIVERY_DELAY}")
    private long REDELIVERY_DELAY;
    public void setREDELIVERY_DELAY(long REDELIVERY_DELAY) {
        this.REDELIVERY_DELAY = REDELIVERY_DELAY;
    }
    public long getREDELIVERY_DELAY() {
        return REDELIVERY_DELAY;
    }

    @Value("${REDELIVERY_TIMEOUT}")
    private long REDELIVERY_TIMEOUT;
    public void setREDELIVERY_TIMEOUT(long REDELIVERY_TIMEOUT) {
        this.REDELIVERY_TIMEOUT = REDELIVERY_TIMEOUT;
    }
    public long getREDELIVERY_TIMEOUT() {
        return REDELIVERY_TIMEOUT;
    }

    @Value("${REDELIVERY_LIMIT}")
    private int REDELIVERY_LIMIT;
    public void setREDELIVERY_LIMIT(int REDELIVERY_LIMIT) {
        this.REDELIVERY_LIMIT = REDELIVERY_LIMIT;
    }
    public int getREDELIVERY_LIMIT() {
        return REDELIVERY_LIMIT;
    }

    @Value("${REFS_REFRESH_COUNTER}")
    private int REFS_REFRESH_COUNTER;
    public void setREFS_REFRESH_COUNTER(int REFS_REFRESH_COUNTER) {
        this.REFS_REFRESH_COUNTER = REFS_REFRESH_COUNTER;
    }
    public int getREFS_REFRESH_COUNTER() {
        return REFS_REFRESH_COUNTER;
    }

    @Value("${MODE}")
    private String MODE;
    public void setMODE(String MODE) {
        this.MODE = MODE;
    }
    public String getMODE() {
        return MODE;
    }

    @Value("${DEBUG_HOST}")
    private String DEBUG_HOST;
    public void setDEBUG_HOST(String DEBUG_HOST) {
        this.DEBUG_HOST = DEBUG_HOST;
    }
    public String getDEBUG_HOST() {
        return DEBUG_HOST;
    }

    @Value("${DEBUG_PORT}")
    private int DEBUG_PORT;
    public void setDEBUG_PORT(int DEBUG_PORT) {
        this.DEBUG_PORT = DEBUG_PORT;
    }
    public int getDEBUG_PORT() {
        return DEBUG_PORT;
    }

    @Value("${HIGH_LOG_LEVEL}")
    private boolean HIGHT_LOG_LEVEL;
    public void setHIGHT_LOG_LEVEL(boolean HIGHT_LOG_LEVEL) {
        this.HIGHT_LOG_LEVEL = HIGHT_LOG_LEVEL;
    }
    public boolean getHIGHT_LOG_LEVEL() {
        return HIGHT_LOG_LEVEL;
    }
}
