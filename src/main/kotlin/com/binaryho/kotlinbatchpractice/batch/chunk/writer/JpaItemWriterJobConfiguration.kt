package com.binaryho.kotlinbatchpractice.batch.chunk.writer

import com.binaryho.kotlinbatchpractice.entity.pay.Pay
import com.binaryho.kotlinbatchpractice.entity.pay.Pay2
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager

@Configuration
class JpaItemWriterJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
) {
    companion object {
        const val CHUNK_SIZE: Int = 10
        const val JOB_NAME = "jpaItemWriterJob"
        const val SELECT_PAY_QUERY = "SELECT p FROM Pay p"
    }

    @Bean
    fun jpaItemWriterJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(jpaItemWriterStep())
            .build()

    @Bean
    fun jpaItemWriterStep(): Step =
        StepBuilder("jpaItemWriterStep", jobRepository)
            .chunk<Pay?, Pay2?>(CHUNK_SIZE, transactionManager)
            .reader(jpaItemWriterReader())
            .processor(jpaItemProcessor())
            .writer(jpaItemWriter())
            .build()

    @Bean
    fun jpaItemWriterReader(): JpaPagingItemReader<Pay> =
        JpaPagingItemReaderBuilder<Pay>()
            .name("jpaItemWriterReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString(SELECT_PAY_QUERY)
            .build()

    @Bean
    fun jpaItemProcessor(): ItemProcessor<Pay, Pay2> =
        ItemProcessor<Pay, Pay2> { pay: Pay ->
            Pay2(
                amount = pay.amount,
                transactionName = pay.transactionName,
                transactionDateTime = pay.transactionDateTime,
            )
        }

    @Bean
    fun jpaItemWriter(): JpaItemWriter<Pay2> {
        val jpaItemWriter: JpaItemWriter<Pay2> = JpaItemWriter<Pay2>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }
}
