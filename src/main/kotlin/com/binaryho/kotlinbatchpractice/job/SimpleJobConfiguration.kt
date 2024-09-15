package com.binaryho.kotlinbatchpractice.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
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
            .start(simpleStep1())
            .build()

    @Bean
    fun simpleStep1(): Step =
        StepBuilder("simple-step-1", jobRepository)
            .tasklet(
                { _, _ ->
                    println(">>>>>> Simple Step 1")
                    RepeatStatus.FINISHED
                },
                transactionManager,
            ).build()
}
