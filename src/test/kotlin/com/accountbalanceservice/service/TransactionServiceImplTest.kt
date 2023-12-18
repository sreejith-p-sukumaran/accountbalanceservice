package com.accountbalanceservice.service

import com.accountbalanceservice.entity.Customer
import com.accountbalanceservice.entity.Tenant
import com.accountbalanceservice.entity.Transaction
import com.accountbalanceservice.enums.TransactionOperation
import com.accountbalanceservice.model.*
import com.accountbalanceservice.repository.CustomerRepository
import com.accountbalanceservice.repository.TenantRepository
import com.accountbalanceservice.repository.TransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TransactionServiceImplTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var customerRepository: CustomerRepository

    @Mock
    private lateinit var tenantRepository: TenantRepository

    @Mock
    private lateinit var transactionServiceUtils: TransactionServiceUtils

    @InjectMocks
    private lateinit var transactionService: TransactionServiceImpl

    @Test
    fun `test BookTransaction`() {
        val customerNumber = 1L
        val tenantNumber = 1L
        val transactionRequest = TransactionRequest(customerNumber = 1, tenantNumber = 1, amount = 100.0)
        val tenant1 = Tenant(number=1, name = "enterprise-all-inclusive.com")
        val customer = Customer(number = 1, name = "customer 1", tenant = tenant1)
        val customers = listOf(customer)

        val savedTransaction = Transaction(id = 1, customer = customer, amount = 100.0, bookedAt = LocalDateTime.now())

        `when`(customerRepository.findByNumberAndTenant_Number(customerNumber, tenantNumber)).thenReturn(customers)
        `when`(transactionRepository.save(any())).thenReturn(savedTransaction)

        val result = transactionService.book(transactionRequest)

        assertEquals(savedTransaction, result)
    }

    @Test
    fun `test rollbackTransaction`() {
        val transactionId = 1L
        val tenant1 = Tenant(number=1, name = "enterprise-all-inclusive.com")
        val customer = Customer(number = 1, name = "customer 1", tenant = tenant1)
        val transaction = Transaction(id = 1, customer = customer, amount = 100.0, bookedAt = LocalDateTime.now())

        `when`(transactionServiceUtils.findTransactionByIdOrThrowNotFound(transactionId)).thenReturn(transaction)
        `when`(transactionRepository.save(any())).thenReturn(transaction)

        val result = transactionService.rollback(transactionId)

        assertEquals(LocalDateTime.now().dayOfYear, result.rollbackAt!!.dayOfYear)
        assertEquals(100.0, result.amount)
    }

    @Test
    fun `test listTransactions`() {
        val size: Int = 10
        val page: Int = 0

        val pageable = PageRequest.of(page, size)
        val listTransactionRequest = ListTransactionRequest(customerNumber = 0L, tenantNumber = 0L, pageable = pageable)
        val tenant = Tenant(number=1, name = "tenant 1")
        val transactions = listOf(
                Transaction(id = 1, customer = Customer(number = 1, name = "Customer 1", tenant = tenant), amount = 100.0, bookedAt = LocalDateTime.now())
        )
        val transaction = TransactionItem(id = 1, amount = 100.0, operation = TransactionOperation.BOOK)
        val transactionResponse = TransactionResponse(tenantName = "tenant 1", customerName = "Customer 1", accountBalance = 100.0, transactions = listOf(transaction))
        val expectedResponses = listOf(transactionResponse)

        `when`(transactionRepository.findAll(pageable)).thenReturn(PageImpl(transactions))

        val actualResponses = transactionService.list(listTransactionRequest)

        assertEquals(expectedResponses.size, actualResponses.size)
    }

    @Test
    fun `test auditTransactions`() {
        val size: Int = 10
        val page: Int = 0

        val pageable = PageRequest.of(page, size)
        val listTransactionRequest = ListTransactionRequest(customerNumber = 0L, tenantNumber = 0L, pageable = pageable)
        val tenant = Tenant(number=1, name = "Tenant 1")
        val transactions = listOf(
                Transaction(id = 1, customer = Customer(number = 1, name = "Customer 1", tenant = tenant), amount = 100.0, bookedAt = LocalDateTime.now())
        )

        val auditResponse = AuditResponse(tenantName = "Tenant 1", customerName = "Customer 1", amount = 100.0, operation =  TransactionOperation.BOOK, time = LocalDateTime.now().toString())
        val expectedResponses = listOf(auditResponse)

        `when`(transactionRepository.findAll(pageable)).thenReturn(PageImpl(transactions))

        val actualResponses = transactionService.list(listTransactionRequest)

        assertEquals(expectedResponses.size, actualResponses.size)
    }
}
