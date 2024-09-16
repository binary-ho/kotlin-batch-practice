package com.binaryho.kotlinbatchpractice.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    private val dataSource: DataSource,
) {
    @Bean
    fun transactionManager(): DataSourceTransactionManager = DataSourceTransactionManager(dataSource)

    @Bean
    fun jpaTransactionManager(entityManagerFactory: EntityManagerFactory?): JpaTransactionManager =
        JpaTransactionManager(entityManagerFactory!!)
}
