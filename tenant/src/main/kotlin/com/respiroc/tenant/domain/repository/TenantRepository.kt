package com.respiroc.tenant.domain.repository

import com.respiroc.tenant.domain.model.TenantModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TenantRepository : JpaRepository<TenantModel, Long> {
    fun findByTenantIdentifier(identifier: String): Optional<TenantModel>
} 