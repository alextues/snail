/**
 * Работа с сообщениями по заданным критериям
 *
 * 20.07.2018 by Alex Tues
 */
package com.tequila.helper;

import com.tequila.data.domain.platform.Messages;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class MessageHelper {
    @PersistenceContext
    private EntityManager em;

    /**
     * Получить список сообщений для заданного imei-кода за период
     *
     * Передается заранее сформированный SQL-запрос.
     */
    public List<Messages> getMessages(String sql) {
        List<Messages> result = em.createQuery(sql).getResultList();

        return result;
    }
}
