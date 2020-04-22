/**
 * Репозиторий для CRUD-операций над сущностями <transfer>/delivery.*
 * (вспомогательные таблицы)
 *
 * 13.06.2018 by Alex Tues
 * 20.06.2018
 * 12.07.2018
 * 06.08.2018
 */
package com.tequila.data.repository;

import com.tequila.data.domain.transfer.TransferIps;
import com.tequila.data.domain.transfer.TransferLoads;
import com.tequila.data.jpa.transfer.TransferIpsCrud;
import com.tequila.data.jpa.transfer.TransferLoadsCrud;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository("transferRepository")
public class TransferRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferRepository.class);

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
        LOGGER.info("[TransferRepository] created successfully");
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

        LOGGER.error("[TransferRepository] failure to get repository for {}", clazz.getName());
        return null;
    }

    public Object findAll(Class clazz) {
        return getRepository(clazz).findAll();
    }

    // Сущность <transfer>/delivery.t_loads
    public TransferLoads findTransferLoadsById(Long id) {
        TransferLoadsCrud repository = (TransferLoadsCrud) getRepository(TransferLoads.class);
        return repository.findTransferLoadsById(id).get();
    }

    // Пользовательские SQL-запросы
    public Optional<Long> selectTransferLoadsCount() {
        TransferLoadsCrud repository = (TransferLoadsCrud) getRepository(TransferLoads.class);
        return repository.selectTransferLoadsCount();
    }

    public Optional<Long> selectTransferLoadsMaxId() {
        TransferLoadsCrud repository = (TransferLoadsCrud) getRepository(TransferLoads.class);
        return repository.selectTransferLoadsMaxId();
    }

    public List<TransferIps> selectUndeliveredIPSMessages(Long id, Boolean delivered, Timestamp currentTimestamp, int limit) {
        TransferIpsCrud repository = (TransferIpsCrud) getRepository(TransferIps.class);
        return repository.selectUndeliveredIPSMessages(id, delivered, currentTimestamp, PageRequest.of(0, limit));
    }

    public List<TransferIps> selectUndeliveredIPSMessagesByRetranslator(Long id, Long id_retranslator, Boolean delivered, Timestamp currentTimestamp, int limit) {
        TransferIpsCrud repository = (TransferIpsCrud) getRepository(TransferIps.class);
        return repository.selectUndeliveredIPSMessagesByRetranslator(id, id_retranslator, delivered, currentTimestamp, PageRequest.of(0, limit));
    }
}
