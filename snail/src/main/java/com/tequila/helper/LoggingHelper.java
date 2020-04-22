/**
 * Логгирование отправки сообщений
 *
 * 16.07.2018  by Alex Tues
 */
package com.tequila.helper;

import com.tequila.common.Constants;
import com.tequila.data.domain.transfer.TransferIps;
import com.tequila.data.domain.transfer.TransferLog;
import com.tequila.data.repository.TransferRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

@Component
public class LoggingHelper {
    @Resource
    private TransferRepository transferRepository;

    // Сообщение по протоколу Wialon IPS
    public void logIps(TransferIps tips, boolean isNew, boolean result, String comment) {
        TransferLog log = new TransferLog();
        if(isNew) {
            // Для нового сообщения идентификатор еще не известен
            log.setId_parent(Constants.ZERO);
        }
        else {
            // Для существующего сообщения индентификатор уже известен
            log.setId_parent(tips.getId());
        }
        log.setId_message(tips.getId_message());
        log.setProtocol("IPS");
        log.setTimestamp_create(new Timestamp(new Date().getTime()));
        log.setDelivered(result);
        log.setComment(comment);
        transferRepository.getRepository(TransferLog.class).save(log);
    }
}
