/**
 * Утилиты
 *
 * 16.07.2018 by Alex Tues
 */
package com.tequila.core;

import com.tequila.common.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public final class MessagesUtils {
    // Получить строку с датой и временем, приведенными к временной зоне UTC (протокол Wialon IPS)
    public static String getDateAndTimeIps(Date ts) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.IPS_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(ts);
    }

    // Получить географическую широту в формате протокола NMEA-0183 из десятичного представления (протокол Wialon IPS)
    // (алгоритм из исходных кодов TVCom)
    public static String getNMEALatitudeIps(Double lat) {
        String suffix = ";N";

        if(lat == null) return "NA;NA";
        if(lat < 0) {
            lat *= (-1);
            suffix = ";S";
        }

        Integer gradInt = lat.intValue();
        Double minDouble = (lat - gradInt) * 60;
        Double gradMinDouble = (gradInt * 100) + minDouble;
        String latString = (gradInt < 10 ? "0":"") + gradMinDouble;
        return latString + suffix;

        /*
        // Это - стандартный преобразователь, но возвращаемая строка не соответствует
        // формату, принятому в исходных кодах от TVCom
        Double absLat = Math.abs(lat);
        int latDegree = absLat.intValue();
        double latMinutes = (absLat - latDegree) * 60;
        double decimalLat = Double.parseDouble(String.valueOf(latDegree) + String.valueOf(latMinutes));
        decimalLat = Math.round(decimalLat * 10000.00) / 10000.00;

        // Учесть расположение относительно экватора
        return (lat >= 0.00 ? String.valueOf(decimalLat) + ";N" : String.valueOf(decimalLat) + ";S");
        */
    }

    // Получить географическую долготу в формате протокола NMEA-0183 из десятичного представления (протокол Wialon IPS)
    // (алгоритм из исходных кодов TVCom)
    public static String getNMEALongitudeIps(Double lon) {
        String suffix = ";E";

        if(lon == null) return "NA;NA";
        if(lon < 0) {
            lon *= (-1);
            suffix = ";W";
        }

        Integer gradInt = lon.intValue();
        Double minDouble = (lon - gradInt) * 60;
        Double gradMinDouble = (gradInt * 100) + minDouble;
        String lonString = (gradInt < 100 ? "0":"") + (gradInt < 10 ? "0":"") + gradMinDouble;
        return lonString + suffix;

        /*
        // Это - стандартный преобразователь, но возвращаемая строка не соответствует
        // формату, принятому в исходных кодах от TVCom
        Double absLon = Math.abs(lon);
        int lonDegree = absLon.intValue();
        double lonMinutes = (absLon - lonDegree) * 60;
        double decimalLon = Double.parseDouble(String.valueOf(lonDegree) + String.valueOf(lonMinutes));
        decimalLon = Math.round(decimalLon * 10000.00) / 10000.00;

        // Учесть расположение относительно Гринвича
        return (lon >= 0.00 ? longitude + ";E" : longitude + ";W");
        */
    }

    // Получить значение скорости (протокол Wialon IPS)
    public static String getSpeedIps(Double speed) {
        if(speed == null) return "NA";

        return String.valueOf(speed.intValue());
    }

    // Получить значение курса (протокол Wialon IPS)
    public static String getCourseIps(Integer course) {
        if(course == null) return "NA";

        return String.valueOf(course);
    }

    // Получить значение высоты (протокол Wialon IPS)
    public static String getHeightIps(Double height) {
        if(height == null) return "NA";

        return String.valueOf(height.intValue());
    }

    // Получить количество спутников (протокол Wialon IPS)
    public static String getSatellitesIps(Integer sats) {
        if(sats == null) return "NA";

        return String.valueOf(sats);
    }

    // Получить значение снижения точности (протокол Wialon IPS)
    public static String getHdopIps(Double hdop) {
        if(hdop == null) return "NA";

        return String.valueOf(hdop);
    }

    // Получить значение цифровых входов (протокол Wialon IPS)
    public static String getInputsIps(Integer input) {
        if(input == null) return "NA";

        return String.valueOf(input);
    }

    // Получить значение цифровых выходов (протокол Wialon IPS)
    public static String getOutputsIps(Integer output) {
        if(output == null) return "NA";

        return String.valueOf(output);
    }

    // Получить аналоговые входы (протокол Wialon IPS)
    public static String getAdcIps() {
        return "";
    }

    // Получить код ключа водителя (протокол Wialon IPS)
    public static String getIbuttonIps() {
        return "NA";
    }

    // Получить значения сообщений по заданному фильтру (протокол Wialon IPS)
    public static String getParamsIps(String filter, String params) {
        if(filter == null || filter.isEmpty() || params == null || params.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        // Множество параметров, которые необходимо учесть при выборке
        Set<String> filterElements = new HashSet<>();
        String[] filterItems = filter.split("\\|");
        for(int i = 0; i < filterItems.length; i++) {
            filterElements.add(filterItems[i].toUpperCase());
        }

        // Массив фактических значений сообщений
        String[] paramsItems = params.split(",");
        for(int i = 0; i < paramsItems.length; i++) {
            // Вычленить символьный код параметра (P11, B20 и т.д)
            String[] item = paramsItems[i].split(":");
            if(filterElements.contains(item[0].toUpperCase())) {
                sb.append(paramsItems[i]);
                sb.append(",");
            }
        }

        // В итоговой строке лишняя запятая; удалить
        int length = sb.length();
        if(length != 0) {
            return sb.deleteCharAt(length - 1).toString();
        }
        return sb.toString();
    }
}
