package com.accountbalanceservice.service

import com.accountbalanceservice.entity.Transaction
import com.accountbalanceservice.enums.TransactionOperation
import com.accountbalanceservice.model.TransactionResponse
import com.accountbalanceservice.repository.TransactionRepository
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class TransactionServiceUtils(private val transactionRepository: TransactionRepository) {

    fun findTransactionByIdOrThrowNotFound(id: Long): Transaction {
        return transactionRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Transaction with Id not found") }
    }

}
