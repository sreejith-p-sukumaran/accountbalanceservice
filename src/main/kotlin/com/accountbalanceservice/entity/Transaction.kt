package com.accountbalanceservice.entity

import com.accountbalanceservice.enums.TransactionOperation
import jakarta.persistence.*
import lombok.RequiredArgsConstructor
import java.time.LocalDateTime

@Entity
@Table(name = "Transaction")
@RequiredArgsConstructor
data class Transaction (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customerId", nullable = false)
        var customer: Customer,

        @Column(name = "amount")
        var amount: Double,

        @Column(name = "bookedAt")
        var bookedAt: LocalDateTime,

        @Column(name = "rollbackAt")
        var rollbackAt: LocalDateTime? = null,

        ) {
        override fun toString(): String {
                return "Transaction(id=$id, amount=$amount, createdAt=$bookedAt, updatedAt=$rollbackAt)"
        }
}
