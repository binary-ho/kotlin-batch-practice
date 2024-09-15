package com.binaryho.kotlinbatchpractice.job.chunk

import com.binaryho.kotlinbatchpractice.entity.pay.Pay
import com.binaryho.kotlinbatchpractice.entity.pay.PayRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager

@Configuration
class JpaPagingItemReaderJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
    private val payRepository: PayRepository,
    private val entityManagerFactory: EntityManagerFactory,
) {
    companion object {
        const val CHUNK_SIZE = 10
        const val JOB_NAME = "jpaPagingItemReaderJob"
        const val SELECT_PAY_OVER_AMOUNT_2000_QUERY = "SELECT p FROM Pay p WHERE amount >= 2000"
    }

    @Bean
    fun jpaPagingItemReaderJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(jpaPagingItemReaderStep())
            .build()

    @Bean
    fun jpaPagingItemReaderStep(): Step =
        StepBuilder("jpaPagingItemReaderStep", jobRepository)
            .chunk<Pay?, Pay?>(CHUNK_SIZE, transactionManager)
            .reader(jpaPagingItemReader())
            .writer(jpaPagingItemWriter())
            .build()

    @Bean
    fun jpaPagingItemReader(): JpaPagingItemReader<Pay> =
        JpaPagingItemReaderBuilder<Pay>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString(SELECT_PAY_OVER_AMOUNT_2000_QUERY)
            .build()

    private fun jpaPagingItemWriter(): ItemWriter<Pay?> =
        ItemWriter<Pay?> { list: Chunk<out Pay?> ->
            list.forEach { pay ->
                println("Current Pay=$pay")
            }
        }
}
