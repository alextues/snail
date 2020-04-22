/**
 * Сканирование таблицы сообщений <platform>/sakura.messages
 *
 * Внимание: в коде имеется ссылка на компонент MessagesHandler
 * в котором, в свою очередь, имеется ссылка на компонент MessagesScanner
 * (т.н. "god object").
 * Это вполне безопасно если не допускать рекурсивных вызовов.
 * 13.07.2018 by Alex Tues
 * 19.07.2018
 * 06.08.2018
 */
package com.tequila.core;

import com.tequila.common.BindingEntity;
import com.tequila.common.Constants;
import com.tequila.config.SnailExpressConfig;
import com.tequila.data.domain.platform.Messages;
import com.tequila.data.domain.platform.Retranslators;
import com.tequila.data.domain.transfer.TransferLoads;
import com.tequila.data.repository.PlatformRepository;
import com.tequila.data.repository.TransferRepository;
import com.tequila.helper.RetranslatorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Component
public class MessagesScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesScanner.class);

    @Resource
    private PlatformRepository platformRepository;
    @Resource
    private TransferRepository transferRepository;
    @Autowired
    private RetranslatorHelper retranslatorHelper;
    @Autowired
    private SnailExpressConfig snailExpressConfig;
    @Autowired
    private MessagesHandler messagesHandler;

    // Список соответствия ретрансляторов, единиц техники и imei-кодов
    private List<BindingEntity> bindingEntityList;
    // Программный кеш: сопоставление imei-кодов множествам идентификаторов ретрансляторов
    private Map<Long, Set<Long>> bindingCache;
    // Список ретрансляторов
    private List<Retranslators> retranslatorsList;
    // Счетчик обновления данных (при сбросе значения в 0 - обновить данные)
    private int refreshCounter;

    // Вернуть программный кеш
    public Map<Long, Set<Long>> getBindingCache() {
        return bindingCache;
    }

    // Вернуть список ретрансляторов
    public List<Retranslators> getRetranslatorsList() {
        return retranslatorsList;
    }

    /**
     * Обновить список соответствия ретрансляторов, единиц техники и imei-кодов
     *
     * Структура списка соответствия:
     * - id-ретранслятора;
     * - id-единицы техники;
     * - imei-код единицы техники.
     * Используется для построения программного кеша. Пересоздается через фиксированное количество
     * выборок из таблицы сообщений (контролируется счетчиком refreshCounter).
     */
    private List<BindingEntity> reloadBindingEntityList() {
        LOGGER.info("[MessagesScanner] prepare binding list");
        List<BindingEntity> bindList = retranslatorHelper.getBindingEntity();
        // Отсортировать в порядке возрастания imei-кода с учетом идентификатора ретранслятора
        if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
            Collections.sort(bindList, new Comparator<BindingEntity>() {
                @Override
                public int compare(BindingEntity o1, BindingEntity o2) {
                    Long value1 = o1.getImei() + o1.getId_retranslator();
                    Long value2 = o2.getImei() + o2.getId_retranslator();
                    return value1.compareTo(value2);
                }
            });
            LOGGER.info("[MessagesScanner] binding (retranslators : imei)");
            for (int i = 0; i < bindList.size(); i++) {
                LOGGER.info("{} : {}", bindList.get(i).getId_retranslator(), bindList.get(i).getImei());
            }
        }

        /* Тестовая печать:
        ...
        2:866104027070815
        4:866104027070815
        7:866104027070815
        8:866104027070815
        12:866104027070815
        ...
        */

        return bindList;
    }

    /**
     * Обновить программный кеш
     *
     * Структура программного кеша:
     * - Long (imei-код);
     * - Set<Long> (множество id-ретрансляторов).
     * Программный кеш пересоздается всякий раз при перезагрузке списка соответствия ретрансляторов,
     * единиц техники и imei-кодов, что позволяет поддерживать в актуальном состоянии обе структуры.
     */
    private Map<Long, Set<Long>> reloadCache() {
        LOGGER.info("[MessagesScanner] prepare cache");
        Map<Long, Set<Long>> bindCache = new HashMap<>();
        for(int i = 0; i < bindingEntityList.size(); i++) {
            Long key = bindingEntityList.get(i).getImei();
            // Новый ключ
            if(!bindCache.containsKey(key)) {
                bindCache.put(key, new HashSet<>());
            }

            // Текущее значение множества идентификаторов ретрансляторов
            Set<Long> set = bindCache.get(key);
            set.add(bindingEntityList.get(i).getId_retranslator());
            // Заменить предыдущее значение множества новым; не тратим время на проверку
            bindCache.replace(key, set);
        }

        /* Тестовая печать:
        ...
        865733027530435->[7, 8]
        866104027070815->[2, 4, 7, 8, 12]
        865733023819279->[7, 8]
        865733027371723->[7, 8]
        861694031606054->[4, 7, 8, 12]
        ...
        */

        if(snailExpressConfig.getHIGHT_LOG_LEVEL()) {
            LOGGER.info("[MessagesScanner] cache (imei -> retranslators list)");
            for (Map.Entry<Long, Set<Long>> entry : bindCache.entrySet()) {
                Long key = entry.getKey();
                Set<Long> set = bindCache.get(key);
                LOGGER.info("{} -> {}", key, set.toString());
            }
        }

        return bindCache;
    }

    // Обновить список ретрансляторов (только те, что включены)
    private List<Retranslators> reloadRetranslators() {
        LOGGER.info("[MessagesScanner] prepare retranslators list");
        return platformRepository.findByEnabledTrueOrderById();
    }

    // Обновить счетчик обновления данных
    private int reloadReloadCounter() {
        LOGGER.info("[MessagesScanner] prepare reload counter");
        int refCounter = snailExpressConfig.getREFS_REFRESH_COUNTER();
        if(refCounter <= 0) {
            return Constants.REFRESH_COUNTER;
        }

        return refCounter;
    }

    // Обновление справочников
    private void reloadAll() {
        bindingEntityList = reloadBindingEntityList();
        bindingCache = reloadCache();
        retranslatorsList = reloadRetranslators();
        refreshCounter = reloadReloadCounter();
    }

    /**
     * Основной цикл сканирования таблицы сообщений телематической платформы.
     *
     * Выбираются ранее не обработанные сообщения для последующей обработки в соответствии с
     * протоколом ретрансляции; сообщения сохраняются в одной из таблиц <transfer>/delivery.t_*
     * (где "*" обозначает протокол ретрансляции сообщений: ips, teltonika, galileo и т.д.).
     */
    @Scheduled(initialDelayString = "#{T(java.lang.Long).valueOf(snailExpressConfig.getSCAN_MESSAGES_DELAY())}",
               fixedDelayString = "#{T(java.lang.Long).valueOf(snailExpressConfig.getSCAN_MESSAGES_TIMEOUT())}")
    public void scanner() {
        // Подготовить справочники
        if(bindingEntityList == null || bindingCache == null || retranslatorsList == null) {
            reloadAll();
        }

        // Проверить флаг SCAN_NEW_MESSAGES и если он установлен,
        // выбрать данные из таблицы <platform>/sakura.messages
        if(snailExpressConfig.getSCAN_NEW_MESSAGES()) {
            // Набор записей без учета ретрансляторов
            List<Messages> dirtyList = getDirtyMessages();
            // Ничего не выбрано; подождать следующего цикла сканирования
            if(dirtyList.isEmpty()) return;
            // Набор записей для отправки
            List<Messages> clearList = getClearMessages(dirtyList);
            long  fromId = dirtyList.get(0).getId(),
                    toId = dirtyList.get(dirtyList.size() - 1).getId(),
                 counter = clearList.size();

            // Есть ли данные для обработки и ретрансляции?
            if(counter != 0) {
                LOGGER.info("[MessagesScanner] {} records for delivery found", counter);
                // Передать данные дальше на обработку
                messagesHandler.handler(clearList);
            }
            else {
                LOGGER.info("[MessagesScanner] nothing for delivery", counter);
            }
            // Обновить таблицу загрузок сообщений
            updateTransferLoadsTable(fromId, toId, counter);
        }

        // Обновить счетчик обновления данных
        refreshCounter--;
        // Обновить (перезагрузить) справочники
        if(refreshCounter == 0) {
            reloadAll();
        }
    }

    /**
     * Выборка данных из таблицы <platform>/sakura.messages
     *
     * Коллекция содержит не только данные для отправки, но и данные, которые отправлять не надо
     * (т.е. сообщения, для imei-кодов которых нет соответствующего ретранслятора).
     */
    private List<Messages> getDirtyMessages() {
        // Идентификатор последней обработанной записи <platform>/sakura.messages
        TransferLoads tl = transferRepository.findTransferLoadsById(transferRepository.selectTransferLoadsMaxId().get());

        List<Messages> messagesList = platformRepository.selectMessagesByIdAndLimit(
                tl.getTid(),
                snailExpressConfig.getSCAN_LIMIT_MESSAGES());
        return messagesList;
    }

    /**
     * Подготовить коллекцию к обработке и ретрансляции
     *
     * Удалить из коллекции все элементы у которых отсутствует imei-код
     * или для которых нет назначенных ретрансляторов.
     */
    private List<Messages> getClearMessages(List<Messages> messagesList) {
        List<Messages> list = new ArrayList<>();

        for(int i = 0; i < messagesList.size(); i++) {
            Long imeiCode = messagesList.get(i).getImei();
            if(imeiCode != null && bindingCache.containsKey(imeiCode)) {
                list.add(messagesList.get(i));
            }
        }
        return list;
    }

    // Обновить таблицу загрузок сообщений <transfer>/delivery.t_loads
    private void updateTransferLoadsTable(Long fromId, Long toId, Long counter) {
        TransferLoads tl = new TransferLoads();
        tl.setFid(fromId);
        tl.setTid(toId);
        tl.setCount(counter);
        tl.setLoaded(new Timestamp(new Date().getTime()));
        transferRepository.getRepository(TransferLoads.class).save(tl);
    }
}
