package com.binaryho.kotlinbatchpractice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    private val dataSource: DataSource,
) {
    @Bean
    fun transactionManager(): DataSourceTransactionManager = DataSourceTransactionManager(dataSource)
}
