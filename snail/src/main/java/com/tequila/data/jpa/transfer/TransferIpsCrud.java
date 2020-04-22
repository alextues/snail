/**
 * CRUD-операции над сущностью <transfer>/delivery.t_ips
 * (таблица сообщений для ретрансляции по протоколу Wialon IPS)
 *
 * 27.06.2018 by ALex Tues
 * 10.07.2018
 * 12.07.2018
 * 06.08.2018
 */
package com.tequila.data.jpa.transfer;

import com.tequila.data.domain.transfer.TransferIps;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface TransferIpsCrud extends CrudRepository<TransferIps, Long> {
    // Выборка очередного набора недоставленных записей (сообщений) из таблицы сообщений.
    // Поскольку в SQL-запросе нельзя задавать лимит выборки (LIMIT), то
    // для ограничения объема возвращаемых результатов используется пагинация
    @Query("SELECT t FROM TransferIps t " +
            "WHERE t.id > :id " +
              "AND t.delivered = :delivered AND t.timestamp_stop > :currentTimestamp AND t.attempt < t.attempt_limit ORDER BY t.id")
    List<TransferIps> selectUndeliveredIPSMessages(
            @Param("id") Long id,
            @Param("delivered") Boolean delivered,
            @Param("currentTimestamp") Timestamp currentTimestamp,
            Pageable pageable);

    // Выборка очередного набора недоставленных записей (сообщений) из таблицы сообщений
    // с учетом ретранслятора
    // Поскольку в SQL-запросе нельзя задавать лимит выборки (LIMIT), то
    // для ограничения объема возвращаемых результатов используется пагинация
    @Query("SELECT t FROM TransferIps t " +
            "WHERE t.id > :id "+
              "AND t.id_retranslator = :id_retranslator " +
              "AND t.delivered = :delivered AND t.timestamp_stop > :currentTimestamp AND t.attempt < t.attempt_limit ORDER BY t.id")
    List<TransferIps> selectUndeliveredIPSMessagesByRetranslator(
            @Param("id") Long id,
            @Param("id_retranslator") Long id_retranslator,
            @Param("delivered") Boolean delivered,
            @Param("currentTimestamp") Timestamp currentTimestamp,
            Pageable pageable);
}
