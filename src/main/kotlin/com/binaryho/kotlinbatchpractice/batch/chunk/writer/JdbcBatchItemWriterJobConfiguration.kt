package com.binaryho.kotlinbatchpractice.batch.chunk.writer

import com.binaryho.kotlinbatchpractice.entity.pay.Pay
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class JdbcBatchItemWriterJobConfiguration(
    private val dataSource: DataSource,
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val CHUNK_SIZE = 10
        const val JOB_NAME = "jdbcBatchItemWriterJob"
        const val SELECT_PAY_QUERY = """
            SELECT id, amount, transaction_name as transactionName, transaction_date_time as transactionDateTime 
            FROM PAY
        """

        const val INSERT_PAY_QUERY = """
            INSERT into PAY2(id, amount, transaction_name, transaction_date_time)
            VALUES (:id, :amount, :transactionName, :transactionDateTime)
        """
    }

    @Bean
    fun jdbcBatchItemWriterJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(jdbcBatchItemWriterStep())
            .build()

    @Bean
    fun jdbcBatchItemWriterStep(): Step =
        StepBuilder("jdbcBatchItemWriterStep", jobRepository)
            .chunk<Pay?, Pay?>(CHUNK_SIZE, transactionManager)
            .reader(jdbcBatchItemWriterReader())
            .processor(printPayProcessor())
            .writer(jdbcBatchItemWriter())
            .build()

    @Bean
    fun jdbcBatchItemWriterReader(): JdbcCursorItemReader<Pay> =
        JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .sql(SELECT_PAY_QUERY)
            .name("jdbcBatchItemWriter")
            .build()

    @Bean
    fun printPayProcessor(): ItemProcessor<Pay, Pay> =
        ItemProcessor { pay ->
            println(pay)
            pay
        }

    @Bean // beanMapped()을 사용할때는 필수
    fun jdbcBatchItemWriter(): JdbcBatchItemWriter<Pay> =
        JdbcBatchItemWriterBuilder<Pay>() // 여기의 Generic Type은 Writer가 넘겨 받는 값의 타입
            // .columnMapped() -> Map의 형태로 insert할 값 전달
            .dataSource(dataSource)
            .assertUpdates(true)
            .sql(INSERT_PAY_QUERY)
            .beanMapped() // insert 할 갓ㅂ들 객체로 전달
            .build()
}
