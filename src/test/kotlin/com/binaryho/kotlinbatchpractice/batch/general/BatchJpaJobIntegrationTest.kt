package com.binaryho.kotlinbatchpractice.batch.general

import com.binaryho.kotlinbatchpractice.batch.chunk.BatchJpaJobConfiguration
import com.binaryho.kotlinbatchpractice.config.TestBatchConfiguration
import com.binaryho.kotlinbatchpractice.entity.sales.Sales
import com.binaryho.kotlinbatchpractice.entity.sales.SalesRepository
import com.binaryho.kotlinbatchpractice.entity.sales.SalesSum
import com.binaryho.kotlinbatchpractice.entity.sales.SalesSumRepository
import com.binaryho.kotlinbatchpractice.support.BatchTestSupport
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.Test

@SpringBootTest(classes = [TestBatchConfiguration::class, BatchJpaJobConfiguration::class])
internal class BatchJpaJobIntegrationTest(
    private val batchJpaJob: Job,
) : BatchTestSupport() {
    @Autowired
    protected lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var salesRepository: SalesRepository

    @Autowired
    private lateinit var salesSumRepository: SalesSumRepository

    @AfterTest
    fun tearDown() {
        deleteAll(Sales::class.java)
        deleteAll(SalesSum::class.java)
    }

    @Test
    @DisplayName("기간내_Sales가_집계되어_SalesSum이된다")
    fun testBatchJpaJobConfiguration() {
        // given
        val orderDate = LocalDate.of(2024, 9, 16)
        val amount1 = 1000L
        val amount2 = 500L
        val amount3 = 100L

        save(Sales(orderDate = orderDate, amount = amount1, orderNo = "1"))
        save(Sales(orderDate = orderDate, amount = amount2, orderNo = "2"))
        save(Sales(orderDate = orderDate, amount = amount3, orderNo = "3"))

        val jobParameters =
            JobParametersBuilder(jobLauncherTestUtils.uniqueJobParameters)
                .addString("orderDate", orderDate.format(BatchJpaJobConfiguration.FORMATTER))
                .toJobParameters()

        // when
        jobLauncherTestUtils.job = batchJpaJob
        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        // then
        Assertions.assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)

        val sales = salesRepository.findAll()
        Assertions.assertThat(sales.size).isEqualTo(3)

        val salesSum = salesSumRepository.findAll()
        Assertions.assertThat(salesSum.size).isEqualTo(1)
        Assertions.assertThat(salesSum[0]?.orderDate).isEqualTo(orderDate)
        Assertions.assertThat(salesSum[0]?.amountSum).isEqualTo(amount1 + amount2 + amount3)
    }
}
