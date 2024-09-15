package com.binaryho.kotlinbatchpractice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// @EnableBatchProcessing	=> Batch 5 부터는 얘가 없어야 자동 실행
@SpringBootApplication
class KotlinBatchPracticeApplication

fun main(args: Array<String>) {
    runApplication<KotlinBatchPracticeApplication>(*args)
}
