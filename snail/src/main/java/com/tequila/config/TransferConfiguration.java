/**
 * Конфигурирование источника данных к сущностям вспомогательных таблиц
 * (на всех бинах отсутствует аннотация @Primary)
 *
 * 07.06.2018 by Alex Tues
 * 12.07.2018
 */
package com.tequila.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "pgEntityManagerTransfer",
        transactionManagerRef = "pgTransactionManagerTransfer",
        basePackages = "com.tequila.data.jpa.transfer"
)
public class TransferConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.transfer.datasource")
    public DataSource pgDataSourceTransfer() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "pgEntityManagerTransfer")
    public LocalContainerEntityManagerFactoryBean pgEntityManagerFactoryTransfer(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(pgDataSourceTransfer())
                .packages("com.tequila.data.domain.transfer")
                .persistenceUnit("pgTransferPU")
                .build();
    }

    @Bean(name = "pgTransactionManagerTransfer")
    public PlatformTransactionManager pgTransactionManagerTrasfer(@Qualifier("pgEntityManagerTransfer") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
