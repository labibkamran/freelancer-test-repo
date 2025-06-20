package com.respiroc.tenant.api

import com.respiroc.tenant.domain.model.Tenant

interface TenantInternalApi {
    fun createNewTenant(name: String): Tenant
}