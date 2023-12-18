package com.accountbalanceservice.repository

import com.accountbalanceservice.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository

interface TenantRepository : JpaRepository<Tenant, Long> {
    fun findByNumber(tenantNumber: Long): List<Tenant>
}
