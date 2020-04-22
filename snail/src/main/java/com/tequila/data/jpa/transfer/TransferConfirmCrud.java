/**
 * CRUD-операции над сущностью <transfer>/delivery.t_confirm
 * (таблица признаков ожидания подтверждения при отправке данных)
 *
 * 07.08.2018 by ALex Tues
 */
package com.tequila.data.jpa.transfer;

import com.tequila.data.domain.transfer.TransferConfirm;
import org.springframework.data.repository.CrudRepository;

public interface TransferConfirmCrud extends CrudRepository<TransferConfirm, Long> {
}
