package com.binaryho.kotlinbatchpractice.job.chunk.reader

import com.binaryho.kotlinbatchpractice.entity.pay.Pay
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class JdbcPagingItemReaderJobConfiguration(
    private val dataSource: DataSource,
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val CHUNK_SIZE = 10
        const val JOB_NAME = "jdbcPagingItemReaderJob"
    }

    @Bean
    fun jdbcPagingItemReaderJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(jdbcPagingItemReaderStep())
            .build()

    @Bean
    fun jdbcPagingItemReaderStep(): Step =
        StepBuilder("jdbcPagingItemReaderStep", jobRepository)
            .chunk<Pay, Pay>(CHUNK_SIZE, transactionManager)
            .reader(jdbcPagingItemReader())
            .writer(jdbcPagingItemWriter())
            .build()

    @Bean
    fun jdbcPagingItemReader(): JdbcPagingItemReader<Pay> {
        val parameterValues: MutableMap<String, Any> = HashMap()
        parameterValues["amount"] = 2000

        return JdbcPagingItemReaderBuilder<Pay>()
            .pageSize(CHUNK_SIZE)
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .queryProvider(createQueryProvider())
            .parameterValues(parameterValues)
            .name("jdbcPagingItemReader")
            .build()
    }

    private fun jdbcPagingItemWriter(): ItemWriter<Pay?> =
        ItemWriter<Pay?> { list: Chunk<out Pay?> ->
            list.forEach { pay ->
                println("Current Pay=$pay")
            }
        }

    @Bean
    fun createQueryProvider(): PagingQueryProvider {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("id, amount, transaction_name, transaction_date_time")
        queryProvider.setFromClause("FROM PAY")
        queryProvider.setWhereClause("WHERE amount >= :amount")

        val sortKeys: MutableMap<String, Order> = HashMap(1)
        sortKeys["id"] = Order.ASCENDING

        queryProvider.setSortKeys(sortKeys)
        return queryProvider.getObject()
    }
}
