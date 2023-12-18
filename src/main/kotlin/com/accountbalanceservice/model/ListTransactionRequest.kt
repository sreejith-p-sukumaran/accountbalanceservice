package com.accountbalanceservice.model

import org.springframework.data.domain.Pageable

data class ListTransactionRequest(
        val customerNumber: Long,
        val tenantNumber: Long,
        val pageable: Pageable
)