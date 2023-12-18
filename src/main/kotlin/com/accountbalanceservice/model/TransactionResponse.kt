package com.accountbalanceservice.model

import com.accountbalanceservice.enums.TransactionOperation
import java.time.LocalDateTime

data class TransactionResponse(

        val tenantName: String,

        val customerName: String,

        var accountBalance: Double? = null,

        val transactions: List<TransactionItem>,

        )