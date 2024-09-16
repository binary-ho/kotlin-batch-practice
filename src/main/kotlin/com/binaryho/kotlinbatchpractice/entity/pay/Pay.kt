package com.binaryho.kotlinbatchpractice.entity.pay

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "PAY")
class Pay(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column
    var amount: Long,
    @Column
    var transactionName: String,
    @Column
    var transactionDateTime: LocalDateTime,
) {
    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    }

    override fun toString(): String =
        "id: $id, amount: $amount, transactionName: $transactionName, transactionDateTime: $transactionDateTime"
}
