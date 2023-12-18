package com.accountbalanceservice.repository

import com.accountbalanceservice.entity.Customer
import com.accountbalanceservice.entity.Transaction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository


interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByNumberAndTenant_Number(customerNumber: Long, tenantNumber: Long): List<Customer>
    fun findByNumber(customerNumber: Long): List<Customer>
}
