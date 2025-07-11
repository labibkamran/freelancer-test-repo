package com.respiroc.tenant.api

import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.util.constant.TenantRoleCode
import com.respiroc.util.context.ContextAwareApi

interface TenantInternalApi : ContextAwareApi {
    fun createNewTenant(companyId: Long): Tenant
    fun createNewTenant(command: CreateCompanyCommand): Tenant
    fun findTenantRoleByCode(role: TenantRoleCode): TenantRole
}