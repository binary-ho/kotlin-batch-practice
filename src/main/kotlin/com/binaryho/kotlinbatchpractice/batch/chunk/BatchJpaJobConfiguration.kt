package com.binaryho.kotlinbatchpractice.batch.chunk

import com.binaryho.kotlinbatchpractice.entity.sales.SalesSum
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
class BatchJpaJobConfiguration(
    private val jobRepository: JobRepository,
    private val jpaTransactionManager: JpaTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
) {
    companion object {
        const val CHUNK_SIZE = 1000
        const val JOB_NAME: String = "batchJpaJob"
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    @Bean
    fun batchJpaJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(batchJpaJobStep())
            .build()

    @Bean
    fun batchJpaJobStep(): Step =
        StepBuilder("batchJpaJobStep", jobRepository)
            .chunk<SalesSum, SalesSum>(CHUNK_SIZE, jpaTransactionManager)
            .reader(batchJpaJobReader(null))
            .writer(batchJpaJobWriter())
            .build()

    @Bean
    @StepScope
    fun batchJpaJobReader(
        @Value("#{jobParameters['orderDate']}") orderDate: String?,
    ): JpaPagingItemReader<SalesSum> {
        val params = HashMap<String, Any>()
        params["orderDate"] =
            LocalDate.parse(orderDate, FORMATTER)

        println("orderDate: $orderDate")
        println("params: $params")

        val className: String = SalesSum::class.java.getName()
        val selectQuery = """
                SELECT new $className(null, s.orderDate, SUM(s.amount))
                FROM Sales s
                WHERE s.orderDate =:orderDate
                GROUP BY s.orderDate"""

        return JpaPagingItemReaderBuilder<SalesSum>()
            .name("batchJpaJobReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString(selectQuery)
            .parameterValues(params)
            .build()
    }

    @Bean
    fun batchJpaJobWriter(): JpaItemWriter<SalesSum> {
        val jpaItemWriter = JpaItemWriter<SalesSum>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }
}
