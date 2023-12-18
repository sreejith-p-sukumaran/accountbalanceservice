package com.accountbalanceservice.service

import com.accountbalanceservice.entity.Customer
import com.accountbalanceservice.entity.Tenant
import com.accountbalanceservice.entity.Transaction
import com.accountbalanceservice.enums.TransactionOperation
import com.accountbalanceservice.model.*
import com.accountbalanceservice.repository.CustomerRepository
import com.accountbalanceservice.repository.TenantRepository
import com.accountbalanceservice.repository.TransactionRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TransactionServiceImpl(
        val transactionRepository: TransactionRepository,
        val customerRepository: CustomerRepository,
        val tenantRepository: TenantRepository,
        val transactionUtils: TransactionServiceUtils
) : TransactionService {

    override fun book(transactionRequest: TransactionRequest): Transaction {
        val customerNumber = transactionRequest.customerNumber
        val tenantNumber = transactionRequest.customerNumber
        val customers: List<Customer> = customerRepository
                .findByNumberAndTenant_Number(customerNumber, tenantNumber)
        if (customers.isEmpty()) {
            throw NoSuchElementException("Customer with tenantNumber $tenantNumber and customerNumber $customerNumber not found")
        }
        val transaction = Transaction(
                customer = customers[0],
                amount = transactionRequest.amount,
                bookedAt = LocalDateTime.now(),
        )

        return transactionRepository
                .save(transaction);
    }

    override fun rollback(id: Long): Transaction {
        val transaction = transactionUtils
                .findTransactionByIdOrThrowNotFound(id)
        transaction.rollbackAt = LocalDateTime.now()
        transactionRepository
                .save(transaction)

        return transaction

    }

    override fun list(listTransactionRequest: ListTransactionRequest): List<TransactionResponse> {
        val customerNumber = listTransactionRequest.customerNumber
        val tenantNumber = listTransactionRequest.tenantNumber

        val transactions: Page<Transaction> = if (customerNumber != 0L) {
            val customers: List<Customer> = customerRepository
                        .findByNumberAndTenant_Number(customerNumber, tenantNumber)
            if (customers.isEmpty()) {
                throw NoSuchElementException("Customer with customerNumber $customerNumber not found")
            }
            transactionRepository
                    .findByCustomer_NumberAndCustomer_Tenant_Number(customerNumber, tenantNumber, listTransactionRequest.pageable)
        } else if (tenantNumber != 0L) {
            val tenants: List<Tenant> = tenantRepository
                    .findByNumber(tenantNumber)
            if (tenants.isEmpty()) {
                throw NoSuchElementException("Tenant with tenantNumber $tenantNumber not found")
            }
            transactionRepository
                    .findByCustomer_Tenant_Number(tenantNumber, listTransactionRequest.pageable)
        } else {
            transactionRepository
                    .findAll(listTransactionRequest.pageable)
        }

        val accountBalanceMap = mutableMapOf<Pair<String, String>, Double>()
        val responses = transactions.content.groupBy { it.customer.number to it.customer.tenant.number }
                .map { (_, transactions) ->
                    val customer = transactions.first().customer
                    val tenant = customer.tenant
                    val key = Pair(customer.name, tenant.name)
                    val totalAmount = transactions.sumByDouble { transaction ->
                        if (transaction.rollbackAt != null) {
                            -transaction.amount // Deduct the amount for rollback transactions
                        } else {
                            transaction.amount
                        }
                    }
                    accountBalanceMap[key] = accountBalanceMap.getOrDefault(key, 0.0) + totalAmount

                    TransactionResponse(
                            tenantName = tenant.name,
                            customerName = customer.name,
                            accountBalance = null,
                            transactions = transactions.map { transaction ->
                                TransactionItem(
                                        id = transaction.id!!,
                                        amount = transaction.amount,
                                        operation = if (transaction.rollbackAt != null) TransactionOperation.ROLLBACK else TransactionOperation.BOOK,
                                        bookedAt = transaction.bookedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) ?: "",
                                        rollbackAt = transaction.rollbackAt?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) ?: ""
                                )
                            }
                    )
                }

        responses.forEach { response ->
            val key = Pair(response.customerName, response.tenantName)
            response.accountBalance = accountBalanceMap.getOrDefault(key, 0.0)
        }

        return responses
    }

    override fun audit(listTransactionRequest: ListTransactionRequest): List<AuditResponse> {
        val customerNumber = listTransactionRequest.customerNumber
        val tenantNumber = listTransactionRequest.tenantNumber

        val transactions: Page<Transaction> = if (customerNumber != 0L) {
            val customers: List<Customer> = customerRepository
                    .findByNumberAndTenant_Number(customerNumber, tenantNumber)
            if (customers.isEmpty()) {
                throw NoSuchElementException("Customer with customerNumber $customerNumber not found")
            }
            transactionRepository
                    .findByCustomer_NumberAndCustomer_Tenant_Number(customerNumber, tenantNumber, listTransactionRequest.pageable)
        } else if (tenantNumber != 0L) {
            val tenants: List<Tenant> = tenantRepository
                    .findByNumber(tenantNumber)
            if (tenants.isEmpty()) {
                throw NoSuchElementException("Tenant with tenantNumber $tenantNumber not found")
            }
            transactionRepository
                    .findByCustomer_Tenant_Number(tenantNumber, listTransactionRequest.pageable)
        } else {
            transactionRepository
                    .findAll(listTransactionRequest.pageable)
        }

        val responses = transactions.content.groupBy { it.customer.number to it.customer.tenant.number }
                .map { (_, transactions) ->
                    val customer = transactions.first().customer
                    val tenant = customer.tenant
                    Pair(customer.name, tenant.name)
                    transactions.sumByDouble { it.amount }

                    transactions.map { transaction ->
                        AuditResponse(
                                tenantName = tenant.name,
                                customerName = customer.name,
                                amount = transaction.amount,
                                operation = if (transaction.rollbackAt != null) TransactionOperation.ROLLBACK else TransactionOperation.BOOK,
                                time = transaction.bookedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) ?: ""
                        )
                    }
                }.flatten()

        return responses
    }

}