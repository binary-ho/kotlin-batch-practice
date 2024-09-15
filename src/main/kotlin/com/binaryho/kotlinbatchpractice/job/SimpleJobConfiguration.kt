package com.binaryho.kotlinbatchpractice.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager

@Configuration
class SimpleJobConfiguration(
    val jobRepository: JobRepository,
    val transactionManager: DataSourceTransactionManager,
) {
    @Bean
    fun simpleJob(): Job =
        JobBuilder("simple-job", jobRepository)
            .start(simpleStep1(null))
            .build()

    @Bean
    @JobScope
    fun simpleStep1(
        @Value("#{jobParameters['requestDate']}") requestDate: String?,
    ): Step =
        StepBuilder("simple-step-1", jobRepository)
            .tasklet(
                { _, _ ->
                    println(">>>>>> Simple Step 1")
                    println(">>>>>> requestDate = {$requestDate}")
                    RepeatStatus.FINISHED
                },
                transactionManager,
            ).build()
}
