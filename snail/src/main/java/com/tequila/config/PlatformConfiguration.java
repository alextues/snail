/**
 * Конфигурирование источника данных к сущностям телематической платформы
 * (все бины помечены аннотацией @Primary)
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
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "pgEntityManagerPlatform",
        transactionManagerRef = "pgTransactionManagerPlatform",
        basePackages = "com.tequila.data.jpa.platform"
)
public class PlatformConfiguration {
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.platform.datasource")
    public DataSource pgDataSourcePlatform() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "pgEntityManagerPlatform")
    public LocalContainerEntityManagerFactoryBean pgEntityManagerFactoryPlatform(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(pgDataSourcePlatform())
                .packages("com.tequila.data.domain.platform")
                .persistenceUnit("pgPlatformPU")
                .build();
    }

    @Primary
    @Bean(name = "pgTransactionManagerPlatform")
    public PlatformTransactionManager pgTransactionManagerPlatform(@Qualifier("pgEntityManagerPlatform") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
