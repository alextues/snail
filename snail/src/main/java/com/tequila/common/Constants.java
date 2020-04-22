/**
 * Константы и шаблоны
 *
 * 12.07.2018 by ALex Tues
 * 06.08.2018
 */
package com.tequila.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class Constants {
    public static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final long ZERO = 0L;
    public static final long ONE  = 1L;

    public static final int REFRESH_COUNTER     = 256;
    public static final int SOCKET_OPEN_TIMEOUT = 1000;
    public static final int SOCKET_WAIT_TIMEOUT = 300;

    public static final String IPS_LOGIN_PREFIX   = "#L#";
    public static final String IPS_LOGIN_OK       = "#AL#1";
    public static final String IPS_DATA_PREFIX    = "#D#";
    public static final String IPS_DATA_OK        = "#AD#1";
    public static final String TERMINATOR         = "\r\n";
    public static final String IPS_DATE_FORMAT    = "ddMMyy;HHmmss";

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_BASE          = "1970-01-01";
}
