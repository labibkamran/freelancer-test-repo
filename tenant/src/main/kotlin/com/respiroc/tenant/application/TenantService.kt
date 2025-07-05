package com.respiroc.tenant.application

import com.respiroc.tenant.api.TenantInternalApi
import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.tenant.domain.repository.TenantRepository
import com.respiroc.tenant.domain.repository.TenantRoleRepository
import com.respiroc.util.constant.TenantRoleCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TenantService(
    private val tenantRepository: TenantRepository,
    private val tenantRoleRepository: TenantRoleRepository
) : TenantInternalApi {

    override fun createNewTenant(name: String): Tenant {
       val tenant = Tenant()
        tenant.name = name
        return tenantRepository.saveAndFlush(tenant)
    }

    override fun findTenantRoleByCode(role: TenantRoleCode): TenantRole {
        return tenantRoleRepository.findByCode(role.code)
    }
}