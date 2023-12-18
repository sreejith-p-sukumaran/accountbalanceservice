package com.accountbalanceservice.controller

import com.accountbalanceservice.model.*
import com.accountbalanceservice.service.TransactionService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
@Validated
class TransactionController @Autowired constructor(private val transactionService: TransactionService) {

    var logger: Logger = LogManager.getLogger(TransactionController::class.java)

    @PostMapping(
            value = ["/book"],
            produces = ["application/json"]
    )
    fun bookTransaction(@Valid
        @RequestBody
        transactionRequest: TransactionRequest): WebResponse<String> {
        val transaction = transactionService.book(transactionRequest)
        logger.info("Transaction booked successfully!!");

        return WebResponse(
                code = 200,
                status = "OK",
                data = "Transaction with ID: ${transaction.id} booked successfully!!"
        )
    }

    @PatchMapping(
            value = ["/rollback/{transactionId}"],
            produces = ["application/json"])
    fun rollbackTransaction(@NotNull
        @PathVariable
        @Min(value = 1, message = "Transaction ID must be greater than or equal to 1")
        @Max(value = Long.MAX_VALUE, message = "Transaction ID must be less than or equal to ${Long.MAX_VALUE}")
                            transactionId: Long): WebResponse<String> {
        transactionService.rollback(transactionId)
        logger.info("Transaction with ID: $transactionId rollback successfully!!");

        return WebResponse(
                code = 200,
                status = "OK",
                data = "Transaction with ID $transactionId rolled back successfully."
        )
    }

    @GetMapping(
            value = ["/list/{tenantNumber}/{customerNumber}"],
            produces = ["application/json"]
    )
    fun listTransactions(
            @PathVariable(value = "tenantNumber", required = true)
            @Min(value = 0, message = "tenantNumber must be a non-negative integer.")
            tenantNumber: Long,

            @PathVariable(value = "customerNumber", required = true)
            @Min(value = 0, message = "customerNumber must be a non-negative integer.")
            customerNumber: Long,

            @RequestParam(value = "size", required = false, defaultValue = "10")
            @Min(value = 1, message = "size must be a positive integer.")
            @Max(value = Int.MAX_VALUE.toLong(), message = "size exceeds the maximum allowed value.")
            size: Int,

            @RequestParam(value = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "page must be a non-negative integer.")
            page: Int
    ): WebResponse<List<TransactionResponse>> {
        logger.info("Transactions retrieved successfully!!");
        return processListTransactionRequest(customerNumber, tenantNumber, size, page)
    }

    @GetMapping(
            value = ["/audit"],
            produces = ["application/json"]
    )
    fun auditTransactions(
            @RequestParam(value = "customerNumber", required = false, defaultValue = "0")
            @Min(value = 0, message = "customerNumber must be a non-negative integer.")
            customerNumber: Long,

            @RequestParam(value = "tenantNumber", required = false, defaultValue = "0")
            @Min(value = 0, message = "tenantNumber must be a non-negative integer.")
            tenantNumber: Long,

            @RequestParam(value = "size", required = false, defaultValue = "10")
            @Min(value = 1, message = "size must be a positive integer.")
            @Max(value = Int.MAX_VALUE.toLong(), message = "size exceeds the maximum allowed value.")
            size: Int,

            @RequestParam(value = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "page must be a non-negative integer.")
            page: Int
    ): WebResponse<List<AuditResponse>> {
        logger.info("Audit data retrieved successfully!!");
        return processAuditTransactionRequest(customerNumber, tenantNumber, size, page)
    }

    private fun processListTransactionRequest(customerNumber: Long, tenantNumber: Long, size: Int, page: Int): WebResponse<List<TransactionResponse>> {
        val pageable = PageRequest.of(page ?: 0, size ?: Int.MAX_VALUE)
        val request = ListTransactionRequest(customerNumber = customerNumber, tenantNumber = tenantNumber, pageable = pageable)
        val responses = transactionService.list(request)
        return WebResponse(
                code = 200,
                status = "OK",
                data = responses
        )
    }

    private fun processAuditTransactionRequest(customerNumber: Long, tenantNumber: Long, size: Int, page: Int): WebResponse<List<AuditResponse>> {
        val pageable = PageRequest.of(page ?: 0, size ?: Int.MAX_VALUE)
        val request = ListTransactionRequest(customerNumber = customerNumber, tenantNumber = tenantNumber, pageable = pageable)
        val responses = transactionService.audit(request)
        return WebResponse(
                code = 200,
                status = "OK",
                data = responses
        )
    }

}