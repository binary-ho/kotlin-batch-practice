package com.binaryho.kotlinbatchpractice.entity.sales

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "SALES_SUM")
class SalesSum(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,
    @Column
    var orderDate: LocalDate,
    @Column
    var amountSum: Long,
) {
    override fun toString(): String = "id: $id, orderDate: $orderDate, amount: $amountSum"
}
