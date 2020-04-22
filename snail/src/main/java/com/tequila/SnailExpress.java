/**
 * Доставка (ретрансляция) информации из телематической платформы к внешним системам
 *
 * Поддерживаемые протоколы: Wialon IPS, Galileo, Teltonika.
 * Для внешних систем должны быть заданы: хост, порт, протокол, список единиц техники,
 * информацию о которых необходимо ретранслировать.
 *
 * 05.06.2018 by Alex Tues
 * 29.06.2018
 * 12.07.2018
 * 06.08.2018
 */
package com.tequila;

import com.tequila.common.Prelude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SnailExpress implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnailExpress.class);

    @Autowired
    private Prelude prelude;

    // Точка входа...
    public static void main(String[] args) {
        SpringApplication.run(SnailExpress.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        LOGGER.info("[SnailExpress] start...");
        prelude.prelude();
    }

    // Асинхронное многопоточное исполнение длительной задачи (например,
    // ретрансляция и повторная ретрансляция сообщений во внешние системы)
    @Bean
    public Executor taskExecutor() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(availableProcessors * 2);
        executor.setThreadNamePrefix("SnailExpress-");
        executor.initialize();

        return executor;
    }
}
