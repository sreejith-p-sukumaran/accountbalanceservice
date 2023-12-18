package com.accountbalanceservice.controller

import com.accountbalanceservice.entity.Customer
import com.accountbalanceservice.entity.Tenant
import com.accountbalanceservice.entity.Transaction
import com.accountbalanceservice.enums.TransactionOperation
import com.accountbalanceservice.model.*
import com.accountbalanceservice.service.TransactionService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TransactionControllerTest {

    @Mock
    private lateinit var transactionService: TransactionService

    @InjectMocks
    private lateinit var transactionController: TransactionController

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.standaloneSetup(transactionController).build()
    }

    @Test
    fun `test bookTransaction endpoint with valid request`() {
        val transactionRequest = TransactionRequest(customerNumber = 1, tenantNumber = 1, amount = 100.0)
        val tenant1 = Tenant(number=1, name = "enterprise-all-inclusive.com")
        val customer = Customer(number = 1, name = "customer 1", tenant = tenant1)
        val transactionResponse = Transaction(id = 1, customer = customer, amount = 100.0, bookedAt = LocalDateTime.now())

        `when`(transactionService.book(transactionRequest)).thenReturn(transactionResponse)

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/transactions/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(transactionRequest))
        )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty)
    }

    @Test
    fun `test bookTransaction endpoint with invalid request`() {
        val transactionRequest = TransactionRequest(customerNumber = 0, tenantNumber = 1, amount = 100.0)
        val tenant1 = Tenant(number=1, name = "enterprise-all-inclusive.com")
        val customer = Customer(number = 1, name = "customer 1", tenant = tenant1)
        val transactionResponse = Transaction(id = 1, customer = customer, amount = 100.0, bookedAt = LocalDateTime.now())

        `when`(transactionService.book(transactionRequest)).thenReturn(transactionResponse)

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/transactions/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(transactionRequest))
        )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty)
    }

    @Test
    fun `test rollbackTransaction endpoint with valid transactionId`() {
        val transactionId: Long = 1

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/transactions/rollback/$transactionId")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty)
    }

    @Test
    fun `test listTransactions endpoint with valid parameters`() {
        val customerNumber: Long = 1
        val tenantNumber: Long = 1
        val size: Int = 10
        val page: Int = 0

        val pageable = PageRequest.of(page, size)
        val request = ListTransactionRequest(customerNumber = customerNumber, tenantNumber = tenantNumber, pageable = pageable)
        val tenant1 = Tenant(number=1, name = "enterprise-all-inclusive.com")
        val customer = Customer(number = 1, name = "enterprise customer 1", tenant = tenant1)
        val transaction = TransactionItem(id = 1, amount = 100.0, operation = TransactionOperation.BOOK)
        val transactionResponse = TransactionResponse(tenantName = "", customerName = "", accountBalance = 100.0, transactions = listOf(transaction))
        val responses = listOf(transactionResponse)

        `when`(transactionService.list(request)).thenReturn(responses)

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/transactions/list/{tenantNumber}/{customerNumber}", tenantNumber, customerNumber)
                        .param("size", size.toString())
                        .param("page", page.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isArray)
    }

    @Test
    fun `test auditTransactions endpoint with valid parameters`() {
        val customerNumber: Long = 1
        val tenantNumber: Long = 1
        val size: Int = 10
        val page: Int = 0

        val pageable = PageRequest.of(page, size)
        val request = ListTransactionRequest(customerNumber = customerNumber, tenantNumber = tenantNumber, pageable = pageable)
        val auditResponse = AuditResponse(tenantName = "", customerName = "", amount = 100.0, operation =  TransactionOperation.BOOK, time = LocalDateTime.now().toString())
        val responses = listOf(auditResponse)

        `when`(transactionService.audit(request)).thenReturn(responses)

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/transactions/audit")
                        .param("tenantNumber", tenantNumber.toString())
                        .param("customerNumber", customerNumber.toString())
                        .param("size", size.toString())
                        .param("page", page.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").isArray)
    }

    private fun <T> asJsonString(obj: T): String {
        return ObjectMapper().writeValueAsString(obj)
    }
}
