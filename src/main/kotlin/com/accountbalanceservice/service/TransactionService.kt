package com.accountbalanceservice.service

import com.accountbalanceservice.entity.Transaction
import com.accountbalanceservice.model.AuditResponse
import com.accountbalanceservice.model.ListTransactionRequest
import com.accountbalanceservice.model.TransactionRequest
import com.accountbalanceservice.model.TransactionResponse

interface TransactionService {

    fun book(transactionRequest: TransactionRequest): Transaction

    fun rollback(id: Long) : Transaction

    fun list(listTransactionRequest: ListTransactionRequest): List<TransactionResponse>

    fun audit(listTransactionRequest: ListTransactionRequest): List<AuditResponse>

}