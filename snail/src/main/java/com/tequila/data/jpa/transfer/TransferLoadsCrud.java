/**
 * CRUD-операции над сущностью <transfer>/delivery.t_loads
 * (таблица загрузок сообщений из таблицы <platform>/sakura.messages)
 *
 * 20.06.2018 by ALex Tues
 * 12.07.2018
 */
package com.tequila.data.jpa.transfer;

import com.tequila.data.domain.transfer.TransferLoads;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransferLoadsCrud extends CrudRepository<TransferLoads, Long> {
    Optional<TransferLoads> findTransferLoadsById(Long id);

    @Query("SELECT COUNT(t) FROM TransferLoads t")
    Optional<Long> selectTransferLoadsCount();
    @Query("SELECT MAX(t.id) FROM TransferLoads t")
    Optional<Long> selectTransferLoadsMaxId();
}
