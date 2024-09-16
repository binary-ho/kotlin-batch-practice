package com.binaryho.kotlinbatchpractice.entity.sales

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "SALES")
class Sales(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = 0,
    @Column
    var orderDate: LocalDate,
    @Column
    var amount: Long,
    @Column
    var orderNo: String,
) {
    override fun toString(): String = "id: $id, orderDate: $orderDate, amount: $amount, orderNo: $orderNo"
}
