package com.respiroc.company.api

import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.company.domain.model.Company
import com.respiroc.util.context.ContextAwareApi

interface CompanyInternalApi : ContextAwareApi {
    fun getOrCreateCompany(command: CreateCompanyCommand): Company
}