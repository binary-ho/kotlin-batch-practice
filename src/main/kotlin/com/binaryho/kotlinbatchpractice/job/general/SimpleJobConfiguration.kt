package com.binaryho.kotlinbatchpractice.job.general

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
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val SIMPLE_JOB = "simpleJob"
        const val SIMPLE_STEP = "simpleStep1"
    }

    @Bean
    fun simpleJob(): Job =
        JobBuilder(SIMPLE_JOB, jobRepository)
            .start(simpleStep1(null))
            .build()

    @Bean
    @JobScope
    fun simpleStep1(
        @Value("#{jobParameters['requestDate']}") requestDate: String?,
    ): Step =
        StepBuilder(SIMPLE_STEP, jobRepository)
            .tasklet(
                { _, _ ->
                    println(">>>>>> Simple Step 1")
                    println(">>>>>> requestDate = {$requestDate}")
                    RepeatStatus.FINISHED
                },
                transactionManager,
            ).build()
}
