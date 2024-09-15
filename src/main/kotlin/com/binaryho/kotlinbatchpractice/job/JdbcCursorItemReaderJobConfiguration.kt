package com.binaryho.kotlinbatchpractice.job

import com.binaryho.kotlinbatchpractice.entity.pay.Pay
import com.binaryho.kotlinbatchpractice.entity.pay.PayRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class JdbcCursorItemReaderJobConfiguration(
    private val dataSource: DataSource,
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
    private val payRepository: PayRepository,
) {
    companion object {
        const val CHUNK_SIZE = 10

        const val JOB_NAME = "jdbcCursorItemReaderJob"
        const val STEP_NAME = "jdbcCursorItemReaderStep"
        const val ITEM_READER_NAME = "jdbcCursorItemReader"

        const val SELECT_PAY_QUERY = "SELECT id, amount, transaction_name, transaction_date_time FROM PAY"
    }

    @Bean
    fun jdbcCursorItemReaderJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(jdbcCursorItemReaderStep())
            .build()

    @Bean
    fun jdbcCursorItemReaderStep(): Step =
        StepBuilder(STEP_NAME, jobRepository)
            .chunk<Pay, Pay>(CHUNK_SIZE, transactionManager)
            .reader(jdbcCursorItemReader())
            .writer(jdbcCursorItemWriter())
            .build()

    @Bean
    fun jdbcCursorItemReader(): JdbcCursorItemReader<Pay> =
        JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .sql(SELECT_PAY_QUERY)
            .name(ITEM_READER_NAME)
            .build()

    private fun jdbcCursorItemWriter(): ItemWriter<Pay?> =
        ItemWriter<Pay?> { list: Chunk<out Pay?> ->
            println("Current Pay=$list")
            payRepository.saveAll(list)
        }
    //        JdbcBatchItemWriterBuilder<Pay>()
//            .dataSource(dataSource)
//            .build()
}
