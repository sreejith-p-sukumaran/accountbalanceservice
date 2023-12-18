package com.accountbalanceservice.entity

import jakarta.persistence.*
import lombok.RequiredArgsConstructor

@Entity
@Table(name = "Customer")
@RequiredArgsConstructor
data class Customer (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "number")
    var number: Long,

    @Column(name = "name")
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenantId", nullable = false)
    var tenant: Tenant,

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var transactions: List<Transaction> = ArrayList()
) {
    override fun toString(): String {
        return "Customer(id=$id, name=$name, tenant=$tenant)"
    }
}