package com.binaryho.kotlinbatchpractice.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DataSourceTransactionManager

class ScopeJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val SCOPE_STEP_1 = "scopeStep1"
        const val SCOPE_STEP_2 = "scopeStep2"
    }

    @Bean
    @JobScope
    fun scopeStep1(
        @Value("#{jobParameters['requestDate']}") requestDate: String?,
    ): Step =
        StepBuilder(SCOPE_STEP_1, jobRepository)
            .tasklet(
                { _, _ ->
                    println(">>>>>> Scope Step 1")
                    println(">>>>>> requestDate = {$requestDate}")
                    RepeatStatus.FINISHED
                },
                transactionManager,
            ).build()

    @Bean
    fun scopeStep2(): Step =
        StepBuilder(SCOPE_STEP_2, jobRepository)
            .tasklet(scopeStep2Tasklet(null), transactionManager)
            .build()

    // Step Scope 니까 위에서 null 넣어줘도, Step 실행될 때 빈 생성되서 값 바인딩 된다.
    @Bean
    @StepScope
    fun scopeStep2Tasklet(
        @Value("#{jobParameters['requestDate']}") requestDate: String?,
    ): Tasklet =
        Tasklet { _, _ ->
            println(">>>>> ScopeStep 2 Tasklet: requestDate = $requestDate")
            RepeatStatus.FINISHED
        }
}
