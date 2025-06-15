package com.respiroc.tenant.application

import com.respiroc.tenant.domain.model.TenantModel
import com.respiroc.tenant.domain.repository.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TenantService(private val tenantRepository: TenantRepository) {

    fun createTenant(identifier: String, name: String): TenantModel {
        require(!tenantRepository.findByTenantIdentifier(identifier).isPresent) { "Tenant identifier already exists" }
        val tenantModel = TenantModel(tenantIdentifier = identifier, name = name)
        return tenantRepository.save(tenantModel)
    }

    fun findById(id: Long): TenantModel = tenantRepository.findById(id)
        .orElseThrow { IllegalArgumentException("Tenant not found") }

    fun listAll(): List<TenantModel> = tenantRepository.findAll()
} 