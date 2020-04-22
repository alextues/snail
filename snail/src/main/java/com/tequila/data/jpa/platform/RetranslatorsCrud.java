/**
 * CRUD-операции над сущностью <platform>/sakura.retranslators
 * (справочник ретрансляторов)
 *
 * 08.06.2018 by ALex Tues
 * 12.07.2018
 */
package com.tequila.data.jpa.platform;

import com.tequila.data.domain.platform.Retranslators;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RetranslatorsCrud extends CrudRepository<Retranslators, Long>  {
    Optional<Retranslators> findRetranslatorsById(Long id);
    List<Retranslators> findByEnabledOrderById(Boolean enabled);
    List<Retranslators> findByEnabledTrueOrderById();
    Retranslators findByNameIgnoreCase(String name);
}
