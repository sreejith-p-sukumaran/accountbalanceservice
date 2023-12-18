package com.accountbalanceservice.entity

import jakarta.persistence.*
import lombok.RequiredArgsConstructor

@Entity
@Table(name = "Tenant")
@RequiredArgsConstructor
data class Tenant (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "number")
    var number: Long,

    @Column(name = "name")
    var name: String,

    @OneToMany(mappedBy = "tenant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var customers: List<Customer> = ArrayList()

) {
    override fun toString(): String {
        return "Customer(id=$id, name=$name)"
    }
}