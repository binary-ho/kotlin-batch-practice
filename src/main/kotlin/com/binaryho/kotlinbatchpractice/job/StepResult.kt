package com.binaryho.kotlinbatchpractice.job

import org.springframework.batch.core.ExitStatus

class StepResult {
    companion object {
        const val ALL = "*"
        val FAILED = ExitStatus.FAILED.exitCode
    }
}
