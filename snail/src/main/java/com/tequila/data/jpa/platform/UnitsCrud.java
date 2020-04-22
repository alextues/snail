/**
 * CRUD-операции над сущностью <platform>/sakura.units
 * (справочник единиц техники)
 *
 * 09.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.data.jpa.platform;

import com.tequila.data.domain.platform.Units;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnitsCrud extends CrudRepository<Units, Long> {
    Optional<Units> findUnitsById(Long id);
    Units findUnitsByImei(Long imei);

    @Query("SELECT u FROM Units u WHERE u.unit_type = :unit_type ORDER BY u.id")
    List<Units> selectAllUnits(@Param("unit_type") Long unit_type);

    // Получить список единиц техники для заданного ретранслятора
    @Query("SELECT u FROM Units u" +
            "  LEFT JOIN Retranslator2Unit r2u ON u.id = r2u.id_unit" +
            "  LEFT JOIN Retranslators r ON r.id = r2u.id_retranslator" +
            "  WHERE r.id = :id_retranslator ORDER BY u.imei")
    List<Units> selectAllUnitsByRetranslator(@Param("id_retranslator") Long id_retanslator);

    // Получить список единиц техники для ретрансляторов из списка
    @Query("SELECT u FROM Units u" +
            "  LEFT JOIN Retranslator2Unit r2u ON u.id = r2u.id_unit" +
            "  LEFT JOIN Retranslators r ON r.id = r2u.id_retranslator" +
            "  WHERE r.id IN (:retranslator_list) ORDER BY u.imei")
    List<Units> selectAllUnitsByRetranslatorList(@Param("retranslator_list") Long[] retranslator_list);
}
