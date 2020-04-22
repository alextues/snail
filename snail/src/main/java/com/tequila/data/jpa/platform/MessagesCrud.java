/**
 * CRUD-операции над сущностью <platform>/sakura.messages
 * (полученные телематической платформой сообщения)
 *
 * 08.06.2018 by Alex Tues
 * 12.07.2018
 * 18.07.2018
 */
package com.tequila.data.jpa.platform;

import com.tequila.data.domain.platform.Messages;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessagesCrud extends CrudRepository<Messages, Long> {
    Optional<Messages> findMessagesById(Long id);
    List<Messages> findMessagesByImei(Long imei);

    @Query("SELECT MAX(m.id) FROM Messages m")
    Optional<Long> selectMessagesMaxId();

    // Выборка очередного набора записей (сообщений) из телематической платформы.
    // Поскольку в SQL-запросе нельзя задавать лимит выборки (LIMIT), то
    // для ограничения объема возвращаемых результатов используется пагинация
    @Query("SELECT m FROM Messages m WHERE m.id > :id ORDER BY m.id")
    List<Messages> selectMessagesByIdAndLimit(@Param("id") Long id, Pageable pageable);

    // Выборка очередного набора записей (сообщений) из телематической платформы с
    // учетом imei-кода (добавлено для целей отладки; использовать осторожно).
    // Поскольку в SQL-запросе нельзя задавать лимит выборки (LIMIT), то
    // для ограничения объема возвращаемых результатов используется пагинация
    @Query("SELECT m FROM Messages m WHERE m.id > :id AND m.imei = :imei ORDER BY m.id")
    List<Messages> selectMessagesByIdAndImeiAndLimit(@Param("id") Long id, @Param("imei") Long imei, Pageable pageable);
}
