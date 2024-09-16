package com.binaryho.kotlinbatchpractice.batch.chunk.itemstream

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import java.util.*

@Configuration
class ItemStreamErrorCaseJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: DataSourceTransactionManager,
) {
    companion object {
        const val JOB_NAME = "ItemStreamErrorCaseJob"
    }

    @Bean
    fun itemStreamErrorCaseJob(): Job =
        JobBuilder(JOB_NAME, jobRepository)
            .start(itemStreamErrorCaseStep())
            .build()

    fun itemStreamErrorCaseStep(): Step =
        StepBuilder("itemStreamStep", jobRepository)
            .chunk<Int, Int>(10, transactionManager)
            .reader(itemStreamErrorCaseReader())
            .writer { println(">>>>>> Write Items ($it)") }
            .build()

    fun itemStreamErrorCaseReader(): ItemStreamReader<Int> =
        object : ItemStreamReader<Int> {
            private var offset = 0
            private lateinit var queue: Queue<Int>

            override fun open(executionContext: ExecutionContext) {
                val initQueue = listOf(1, 2, 3, 4, 5)
                offset = executionContext.getInt("READ_OFFSET", 0)
                queue = LinkedList(initQueue.subList(offset, initQueue.size))
                println(">>>>>> [OPEN] ItemStream Open Offset : $offset")
            }

            override fun update(executionContext: ExecutionContext) {
                executionContext.put("READ_OFFSET", offset)
                println(">>>>>> [UPDATE] ItemStream Update Offset : $offset")
            }

            override fun close() {
                println(">>>>>> [CLOSE]")
            }

            override fun read(): Int? {
                val result = queue.poll() ?: return null
                if (result == 5) {
                    throw RuntimeException("예외 발생")
                }
                println(">>>>>> [READ] : $result, offset: $offset")
                offset++
                return result
            }
        }
}
