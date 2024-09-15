package com.binaryho.kotlinbatchpractice.job

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager

@Configuration
class ConditionalJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val CONDITIONAL_JOB = "conditionalJob"
        const val CONDITIONAL_STEP_1 = "conditionalStep1"
        const val CONDITIONAL_STEP_2 = "conditionalStep2"
        const val CONDITIONAL_STEP_3 = "conditionalStep3"
    }

    @Bean
    fun conditionalJob() =
        JobBuilder(CONDITIONAL_JOB, jobRepository)
            .start(conditionalStep1())
            .on(StepResult.FAILED)
            .to(conditionalStep3())
            .on(StepResult.ALL)
            .end()
            .from(conditionalStep1())
            .on(StepResult.ALL)
            .to(conditionalStep2())
            .next(conditionalStep3())
            .on(StepResult.ALL)
            .end()
            .end()
            .build()

    @Bean
    fun conditionalStep1(): Step =
        StepBuilder(CONDITIONAL_STEP_1, jobRepository)
            .tasklet({ contribution: StepContribution, _ ->
                println(">>>>>>> Conditional Step 1 : 이제 실패해볼게!!")
                contribution.exitStatus = ExitStatus.FAILED
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun conditionalStep2(): Step =
        StepBuilder(CONDITIONAL_STEP_2, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>>>> Success Step 2")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()

    @Bean
    fun conditionalStep3(): Step =
        StepBuilder(CONDITIONAL_STEP_3, jobRepository)
            .tasklet({ _, _ ->
                println(">>>>>>> Fail Step 3")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
}
