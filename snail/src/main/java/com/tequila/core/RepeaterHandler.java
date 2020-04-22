/**
 * Переотправка данных по заданному ретранслятору и списку imei-кодов за период
 *
 * Данные для переотправки подготавливаются в plain text файле состоящем вт очности из трех строк в формате:
 *  - идентификатор ретранслятора;
 *  - список imei-кодов через запятую в строку (если пусто, то все imei-коды для данного ретранслятора);
 *  - даты начала периода (может быть пустой)
 *  - дата окончания периода (может быть пустой) (обе даты в формате YYYY-MM-DD)
 * Пример файла:
 *
 *    14
 *    imei0, imei1, imei2, ...
 *    2018-07-01
 *    2018-07-20
 *
 * (именно в таком порядке следования). Если в строке imei-кодов нет данных (пустая строка),
 * это означает, что выбираются все imei-коды, назначенные ретранслятору.
 * По шедулеру проверяется наличие этого файла. Файл считывается, а затем удаляется.
 * По данным из файла формируется пакет данных на отправку и записывается в таблицу
 * <transfer>/delivery.t_* (где "*" обозначает протокол ретрансляции сообщений: ips,
 * teltonika, galileo и т.д.). При ближайщей переретрансляции данные отправляются.
 * Никаких изменений в таблицу загрузко <transfer>/delivery.t_loads не вносится.
 *
 * 20.07.2018 by Alex Tues
 * 23.07.2018
 */
package com.tequila.core;

import com.tequila.common.BindingEntity;
import com.tequila.common.Constants;
import com.tequila.data.domain.platform.Messages;
import com.tequila.helper.MessageHelper;
import com.tequila.helper.RetranslatorHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RepeaterHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeaterHandler.class);
    private static final long INITIAL_DELAY = 30000L,
                                    TIMEOUT = 30000L;
    private static final String   EXTENSION = "rpt";

    @Autowired
    private RetranslatorHelper retranslatorHelper;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private MessagesHandler messagesHandler;

    /**
     * Сканирование каталогов (с подкаталогами) на наличие текстовых файлов с расширением "rpt".
     * Эти файлы должны содержать информацию о том для кого, по каким imei-кодам и за какой
     * период следует повторить отправку данных.
     */
    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = TIMEOUT)
    public void repeater() {
        LOGGER.info("[RepeaterHandler] check for repeating data");
        List<File> files = getListOfRepeaterFiles();

        if(!files.isEmpty()) {
            List<String> sb;
            // Обработать самый первый файл из списка; остальные - в следующий раз
            try(BufferedReader source = new BufferedReader(new FileReader(files.get(0).getCanonicalPath()))){
                String canonicalPath = files.get(0).getCanonicalPath();
                LOGGER.info("[RepeaterHandler] process {}", canonicalPath);

                sb = Files.readAllLines(Paths.get(canonicalPath));
                // Файл должен содержать в точности 4 строки
                if(sb.size() == 4) {
                    parse(sb);
                    // Закрыть ресурс, сохранить файл (изменить расширение) и удалить файл
                    source.close();
                    Files.copy(Paths.get(canonicalPath), Paths.get(changePath(canonicalPath)));
                    Files.deleteIfExists(Paths.get(canonicalPath));
                }
                else {
                    // Не удалять файл; оставить для исправления
                    LOGGER.info("[RepeaterHandler] there must be exactly 4 rows");
                }
            }
            catch(IOException ex) {
                ex.toString();
            }
        }
    }

    // Список текстовых файлов с расширением "rpt"
    private List<File> getListOfRepeaterFiles() {
        File dir = new File(".");
        List<File> files = (List<File>) FileUtils.listFiles(dir, new String[] {EXTENSION}, true);
        return files;
    }

    // Обработать содержимое файла
    private void parse(List<String> sb) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT);

        long   retId = Long.parseLong(sb.get(0).trim());
        String imeis = sb.get(1).trim();
        String datef = sb.get(2).trim();
        String datet = sb.get(3).trim();

        // Список imei-кодов
        List<Long> imeiList = new ArrayList<>();
        List<BindingEntity> bindingEntityList;
        if(imeis.isEmpty()) {
            // Список imei-кодов пуст: выбрать все привязки для данного ретранслятора
            bindingEntityList = retranslatorHelper.getBindingEntityByRetranslator(retId);
            if(bindingEntityList.isEmpty()){
                LOGGER.info("[RepeaterHandler] binding list for retranslator {} is empty", retId);
                return;
            }
            // Выбрать только imei-коды
            for(BindingEntity binding: bindingEntityList) {
                imeiList.add(binding.getImei());
            }
        }
        else {
            // Список imei-кодов не пуст: выбрать все привязки из строки
            String[] imeiArray = imeis.split(",");
            for(int i = 0; i < imeiArray.length; i++) {
                if(!imeiArray[i].isEmpty()) {
                    imeiList.add(Long.parseLong(imeiArray[i]));
                }
            }
        }

        // Преобразования, связанные с датами
        if(datef.isEmpty()) datef = Constants.DATE_BASE;
        if(datet.isEmpty()) datet = sdf.format(new Date());
        datef = "'" + datef + "'";
        datet = "'" + datet + "'";

        // Для каждого imei-кода найти соответствующие записи
        for(int i = 0; i < imeiList.size(); i++) {
            String sql = "SELECT m FROM Messages m" +
                         " WHERE imei=" + imeiList.get(i) +
                         "   AND timestamp_create BETWEEN " + datef + " AND " + datet;
            List<Messages> messages =  messageHelper.getMessages(sql);
            // Сохранить сообщения для дальнейшей ретрансляции
            if(!messages.isEmpty()) messagesHandler.handler(messages);
        }
    }

    // Изменить расширение файла
    private String changePath(String path) {
        int lastpoint = path.lastIndexOf(".");

        return path.substring(0, lastpoint) + "." + new Date().getTime();
    }
}
