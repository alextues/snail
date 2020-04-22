/**
 * CRUD-операции над сущностью <platform>/sakura.retranslator2unit
 * (привязка ретрансляторов к единицам техники)
 *
 * 09.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.data.jpa.platform;

import com.tequila.data.domain.platform.Retranslator2Unit;
import org.springframework.data.repository.CrudRepository;

public interface Retranslator2UnitCrud extends CrudRepository<Retranslator2Unit, Long> {
}
