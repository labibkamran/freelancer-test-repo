package com.respiroc.company.api

import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.util.context.ContextAwareApi

interface CompanyInternalApi : ContextAwareApi {
    fun createNewCompany(command: CreateCompanyCommand): Tenant
}