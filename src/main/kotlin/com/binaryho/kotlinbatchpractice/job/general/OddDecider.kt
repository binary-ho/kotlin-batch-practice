package com.binaryho.kotlinbatchpractice.job.general

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import kotlin.random.Random

class OddDecider : JobExecutionDecider {
    companion object {
        val EVEN = FlowExecutionStatus("EVEN")
        val ODD = FlowExecutionStatus("ODD")
    }

    override fun decide(
        jobExecution: JobExecution,
        stepExecution: StepExecution?,
    ): FlowExecutionStatus {
        val randomNumber = Random.nextInt(50) + 1
        println(">>>>>>>>>>> 고른 숫자 : $randomNumber")
        if (randomNumber % 2 == 0) {
            return EVEN
        }
        return ODD
    }
}
