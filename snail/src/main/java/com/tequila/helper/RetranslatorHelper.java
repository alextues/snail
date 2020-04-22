/**
 * Работа с привязками ретрансляторов к единицам техники и imei-кодам
 *
 * 18.06.2018 by Alex Tues
 * 13.07.2018
 */
package com.tequila.helper;

import com.tequila.common.BindingEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class RetranslatorHelper {
    @PersistenceContext
    private EntityManager em;

    /**
     * Получить список привязки включенных ретрансляторов к единицам техники
     *
     * Критерий отбора: значение поля <platform>/sakura.retranslators.enabled == true.
     */
    public List<BindingEntity> getBindingEntity() {
        List<?> result = em.createQuery("SELECT r2u.id, r2u.id_retranslator, r2u.id_unit, u.imei FROM Retranslator2Unit r2u" +
                "   LEFT JOIN Retranslators r ON r2u.id_retranslator = r.id" +
                "   LEFT JOIN Units u ON r2u.id_unit = u.id" +
                "   WHERE r.enabled = :enabled" +

                // 06.08.2018 begin
                //
                "   AND r.id = 4" +
                // 06.08.2018 end
                //

                " ORDER BY u.imei").
                setParameter("enabled", true).
                getResultList();
        List<BindingEntity> bindingEntityList =  new ArrayList<>();
        extractValues(result, bindingEntityList);

        return bindingEntityList;
    }

    /**
     * Получить список привязки заданного ретранслятора к единицам техники
     *
     * Значение поля <platform>/sakura.retranslators.enabled не учитывается.
     */
    public List<BindingEntity> getBindingEntityByRetranslator(Long id_retranslator) {
        List<?> result = em.createQuery("SELECT r2u.id, r2u.id_retranslator, r2u.id_unit, u.imei FROM Retranslator2Unit r2u" +
                "   LEFT JOIN Retranslators r ON r2u.id_retranslator = r.id" +
                "   LEFT JOIN Units u ON r2u.id_unit = u.id" +
                "   WHERE r2u.id_retranslator = :id_retranslator ORDER BY u.imei").
                setParameter("id_retranslator", id_retranslator).
                getResultList();
        List<BindingEntity> bindingEntityList =  new ArrayList<>();
        extractValues(result, bindingEntityList);

        return bindingEntityList;
    }

    // Сформировать список, состоящий из сущностей BindingEntity
    private void extractValues(List<?> result, List<BindingEntity> bindingEntityList) {
        for(Iterator i = result.iterator(); i.hasNext();) {
            Object[] values = (Object[])i.next();
            BindingEntity bindingEntity = new BindingEntity();
            bindingEntity.setId((Long) values[0]);
            bindingEntity.setId_retranslator((Long) values[1]);
            bindingEntity.setId_unit((Long) values[2]);
            bindingEntity.setImei((Long) values[3]);
            bindingEntityList.add(bindingEntity);
        }
    }
}
