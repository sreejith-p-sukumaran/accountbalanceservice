package com.accountbalanceservice.model

import com.accountbalanceservice.enums.TransactionOperation
import java.time.LocalDateTime

data class AuditResponse(

        val tenantName: String,

        val customerName: String,

        val amount: Double,

        val operation: TransactionOperation,

        val time: String,

        )