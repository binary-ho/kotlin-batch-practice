package com.binaryho.kotlinbatchpractice.job.general

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
class StepNextJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val SEQUENTIAL_JOB = "sequentialJob"
        const val SEQUENTIAL_STEP_1 = "sequentialStep1"
        const val SEQUENTIAL_STEP_2 = "sequentialStep2"
        const val SEQUENTIAL_STEP_3 = "sequentialStep3"
    }

    @Bean
    fun sequentialJob(): Job =
        JobBuilder(SEQUENTIAL_JOB, jobRepository)
            .start(sequentialStep1())
            .next(sequentialStep2())
            .next(sequentialStep3())
            .build()

    @Bean
    fun sequentialStep1(): Step =
        StepBuilder(SEQUENTIAL_STEP_1, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>>>> Sequential Step 1")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun sequentialStep2(): Step =
        StepBuilder(SEQUENTIAL_STEP_2, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>>>> Sequential Step 2")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun sequentialStep3(): Step =
        StepBuilder(SEQUENTIAL_STEP_3, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>>>> Sequential Step 3")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
}
