package com.binaryho.kotlinbatchpractice.entity.sales

import org.springframework.data.jpa.repository.JpaRepository

interface SalesRepository : JpaRepository<Sales?, Long>
