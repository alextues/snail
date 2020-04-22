/**
 * CRUD-операции над сущностью <transfer>/delivery.t_log
 * (таблица регистрации событий по ретрансляции сообщений)
 *
 * 13.07.2018 by ALex Tues
 */
package com.tequila.data.jpa.transfer;

import com.tequila.data.domain.transfer.TransferLog;
import org.springframework.data.repository.CrudRepository;

public interface TransferLogCrud extends CrudRepository<TransferLog, Long> {
}
