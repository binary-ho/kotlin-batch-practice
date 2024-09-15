package com.binaryho.kotlinbatchpractice.entity.pay

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(name = "PAY")
class Pay(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long = 0,
    @Column
    private var amount: Long,
    @Column
    private var transactionName: String,
    @Column
    private var transactionDateTime: LocalDateTime,
) {
    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    }
}
