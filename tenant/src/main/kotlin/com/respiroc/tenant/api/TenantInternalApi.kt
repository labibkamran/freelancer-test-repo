package com.respiroc.tenant.api

import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.util.constant.TenantRoleCode
import com.respiroc.util.context.ContextAwareApi

interface TenantInternalApi : ContextAwareApi {
    fun createNewTenant(name: String): Tenant
    fun findTenantRoleByCode(role: TenantRoleCode): TenantRole
}