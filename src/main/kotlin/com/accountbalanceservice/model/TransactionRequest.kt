package com.accountbalanceservice.model

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank

data class TransactionRequest(
        val tenantNumber: Long,
        val customerNumber: Long,

        @field:DecimalMin(value = "-999999.99", inclusive = true, message = "amount must be greater than or equal to -999999.99")
        @field:DecimalMax(value = "999999.99", inclusive = true, message = "amount must be less than or equal to 999999.99")
        val amount: Double

)