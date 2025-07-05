package com.respiroc.company.api

import com.respiroc.company.api.command.*
import com.respiroc.company.domain.model.Company
import com.respiroc.util.context.ContextAwareApi

interface CompanyInternalApi : ContextAwareApi {
    fun createNewCompany(command: CreateCompanyCommand): Company
    fun findAllCompany(): List<Company>
    fun findCurrentCompany(): Company?
}