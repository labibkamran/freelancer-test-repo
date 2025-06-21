package com.respiroc.company.api

import com.respiroc.company.api.command.*
import com.respiroc.company.domain.model.Company
import com.respiroc.util.context.UserContext

interface CompanyInternalApi {
    fun createNewCompany(command: CreateCompanyCommand, user: UserContext): Company
    fun findAllCompanyByUser(user: UserContext): List<Company>
    fun findCurrentCompanyByUser(user: UserContext): Company?
    fun findCompanyByUserAndTenantId(user: UserContext, tenantId: Long): Company?
}