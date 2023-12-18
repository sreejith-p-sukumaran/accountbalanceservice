package com.accountbalanceservice.repository

import com.accountbalanceservice.entity.Transaction
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository


interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByCustomer_Tenant_Number(tenantId: Long, pageable: Pageable): Page<Transaction>
    fun findByCustomer_NumberAndCustomer_Tenant_Number(customerNumber: Long, tenantNumber: Long, pageable: Pageable): Page<Transaction>

}
