/**
 * Репозиторий для CRUD-операций над сущностями <platform>/sakura.*
 * (телематическая платформа)
 *
 * 08.06.2018 by Alex Tues
 * 20.06.2018
 * 12.07.2018
 */
package com.tequila.data.repository;

import com.tequila.data.domain.platform.Messages;
import com.tequila.data.domain.platform.Retranslators;
import com.tequila.data.domain.platform.Units;
import com.tequila.data.jpa.platform.MessagesCrud;
import com.tequila.data.jpa.platform.RetranslatorsCrud;
import com.tequila.data.jpa.platform.UnitsCrud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("platformRepository")
public class PlatformRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformRepository.class);

    @Autowired
    private ListableBeanFactory beanFactory;

    private Repositories repositories;

    public ListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void postConstruct() {
        repositories = new Repositories(beanFactory);
        LOGGER.info("[PlatformRepository] created successfully");
    }

    // Получить экземпляр CRUD
    @SuppressWarnings("unchecked")
    public CrudRepository getRepository(Class clazz) {
        /*
        Optional<Object> repository = repositories.getRepositoryFor(clazz);
        if(repository != null && repository.get() instanceof CrudRepository) {
            return (CrudRepository<Object, Serializable>) repository.get();
        }
        */
        Optional<?> repository = repositories.getRepositoryFor(clazz);
        if(repository.isPresent()) {
            return (CrudRepository<Object, Serializable>) repository.get();
        }

        LOGGER.error("[PlatformRepository] failure to get repository for {}", clazz.getName());
        return null;
    }

    public Object findAll(Class clazz) {
        return getRepository(clazz).findAll();
    }

    // Сущность <platform>/sakura.messages
    public Messages findMessagesById(Long id) {
        MessagesCrud repository = (MessagesCrud) getRepository(Messages.class);
        return repository.findMessagesById(id).get();
    }

    public List<Messages> findMessagesByImei(Long imei) {
        MessagesCrud repository = (MessagesCrud) getRepository(Messages.class);
        return repository.findMessagesByImei(imei);
    }

    // Сущность <platform>/sakura.retranslators
    public Retranslators findRetranslatorsById(Long id) {
        RetranslatorsCrud repository = (RetranslatorsCrud) getRepository(Retranslators.class);
        return repository.findRetranslatorsById(id).get();
    }

    public List<Retranslators> findByEnabledOrderById(Boolean enabled) {
        RetranslatorsCrud repository = (RetranslatorsCrud) getRepository(Retranslators.class);
        return repository.findByEnabledOrderById(enabled);
    }

    public List<Retranslators> findByEnabledTrueOrderById() {
        RetranslatorsCrud repository = (RetranslatorsCrud) getRepository(Retranslators.class);
        return repository.findByEnabledTrueOrderById();
    }

    public Retranslators findByNameIgnoreCase(String name) {
        RetranslatorsCrud repository = (RetranslatorsCrud) getRepository(Retranslators.class);
        return repository.findByNameIgnoreCase(name);

    }

    // Сущность <platform>/sakura.units
    public Units findUnitsById(Long id) {
        UnitsCrud repository = (UnitsCrud) getRepository(Units.class);
        return repository.findUnitsById(id).get();
    }

    public Units findUnitsByImei(Long imei) {
        UnitsCrud repository = (UnitsCrud) getRepository(Units.class);
        return repository.findUnitsByImei(imei);
    }

    // Пользовательские SQL-запросы
    public List<Messages> selectMessagesByIdAndLimit(Long id, int limit) {
        MessagesCrud repository = (MessagesCrud) getRepository(Messages.class);
        return repository.selectMessagesByIdAndLimit(id, PageRequest.of(0, limit));
    }

    public List<Messages> selectMessagesByIdAndImeiAndLimit(Long id, Long imei, int limit) {
        MessagesCrud repository = (MessagesCrud) getRepository(Messages.class);
        return repository.selectMessagesByIdAndImeiAndLimit(id, imei, PageRequest.of(0, limit));
    }

    public Optional<Long> selectMessagesMaxId() {
        MessagesCrud repository = (MessagesCrud) getRepository(Messages.class);
        return repository.selectMessagesMaxId();
    }

    public List<Units> selectAllUnits(Long unit_type) {
        UnitsCrud repository = (UnitsCrud) getRepository(Units.class);
        return repository.selectAllUnits(unit_type);
    }

    public List<Units> selectAllUnitsByRetranslator(Long id_retranslator) {
        UnitsCrud repository = (UnitsCrud) getRepository(Units.class);
        return repository.selectAllUnitsByRetranslator(id_retranslator);
    }

    public List<Units> selectAllUnitsByRetranslatorList(Long[] retranslator_list) {
        UnitsCrud repository = (UnitsCrud) getRepository(Units.class);
        return repository.selectAllUnitsByRetranslatorList(retranslator_list);
    }
}
