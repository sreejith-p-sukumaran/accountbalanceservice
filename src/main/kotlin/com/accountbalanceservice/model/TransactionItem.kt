package com.accountbalanceservice.model

import com.accountbalanceservice.enums.TransactionOperation
import java.time.LocalDateTime

data class TransactionItem(

        val id: Long,

        val amount: Double,

        val operation: TransactionOperation,

        val bookedAt: String? = null,

        val rollbackAt: String? = null,

        )