package com.respiroc.tenant.api

import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.util.constant.TenantRoleCode

interface TenantInternalApi {
    fun createNewTenant(name: String): Tenant
    fun findTenantRoleByCode(role: TenantRoleCode): TenantRole
}