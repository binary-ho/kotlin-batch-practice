package com.binaryho.kotlinbatchpractice.entity.sales

import org.springframework.data.jpa.repository.JpaRepository

interface SalesSumRepository : JpaRepository<SalesSum?, Long>
