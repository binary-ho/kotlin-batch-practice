package com.binaryho.kotlinbatchpractice.config

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

// 타겟을 좁히기 위해, batch 패키지는 제외하
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@ComponentScan(basePackages = ["com.binaryho.kotlinbatchpractice.config"])
@EntityScan(basePackages = ["com.binaryho.kotlinbatchpractice.entity"])
@EnableJpaRepositories(basePackages = ["com.binaryho.kotlinbatchpractice.entity"])
class TestBatchConfiguration
