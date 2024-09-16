package com.binaryho.kotlinbatchpractice.batch.general

import com.binaryho.kotlinbatchpractice.batch.general.OddDecider.Companion.EVEN
import com.binaryho.kotlinbatchpractice.batch.general.OddDecider.Companion.ODD
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager

@Configuration
class DeciderJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val DECIDER_JOB = "deciderJob"
        const val DECIDER_START_STEP = "deciderStartStep"
        const val EVEN_STEP = "evenStep"
        const val ODD_STEP = "oddStep"
    }

    @Bean
    fun deciderJob(): Job =
        JobBuilder(DECIDER_JOB, jobRepository)
            .start(startStep())
            .next(decider())
            .from(decider())
            .on(ODD.name)
            .to(oddStep())
            .from(decider())
            .on(EVEN.name)
            .to(evenStep())
            .end()
            .build()

    @Bean
    fun startStep(): Step =
        StepBuilder(DECIDER_START_STEP, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>> Start!")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun evenStep(): Step =
        StepBuilder(EVEN_STEP, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>> 짝수입니다.")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun oddStep(): Step =
        StepBuilder(ODD_STEP, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>> 홀수입니다.")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun decider(): JobExecutionDecider = OddDecider()
}
