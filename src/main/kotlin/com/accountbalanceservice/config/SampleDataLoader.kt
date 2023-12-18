package com.accountbalanceservice.config

import com.accountbalanceservice.entity.Customer
import com.accountbalanceservice.entity.Tenant
import com.accountbalanceservice.entity.Transaction
import com.accountbalanceservice.repository.CustomerRepository
import com.accountbalanceservice.repository.TenantRepository
import com.accountbalanceservice.repository.TransactionRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SampleDataLoader(
        private val tenantRepository: TenantRepository,
        private val customerRepository: CustomerRepository,
        private val transactionRepository: TransactionRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Insert sample data during application startup

        // Sample Tenants
        val tenant1 = Tenant(number=1, name = "enterprise-all-inclusive.com")
        val tenant2 = Tenant(number=2, name = "betrieb-alles-inklusive.de")
        tenantRepository.saveAll(listOf(tenant1, tenant2))

        // Sample Customers
        val customer1 = Customer(number=1, name = "enterprise customer one", tenant = tenant1)
        val customer2 = Customer(number=2, name = "enterprise customer two", tenant = tenant1)
        val customer3 = Customer(number=1, name = "betrieb customer one", tenant = tenant2)
        val customer4 = Customer(number=2, name = "betrieb customer two", tenant = tenant2)

        customerRepository.saveAll(listOf(customer1, customer2, customer3, customer4))

        // Sample Transactions
        val transaction1 = Transaction(customer = customer1, amount = 100.0, bookedAt = LocalDateTime.now())
        val transaction2 = Transaction(customer = customer2, amount = 200.0, bookedAt = LocalDateTime.now())
        val transaction3 = Transaction(customer = customer3, amount = 300.0, bookedAt = LocalDateTime.now())
        val transaction4 = Transaction(customer = customer4, amount = 400.0, bookedAt = LocalDateTime.now())

        transactionRepository.saveAll(listOf(transaction1, transaction2, transaction3, transaction4))
    }
}
