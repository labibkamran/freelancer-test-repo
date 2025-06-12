package com.respiroc.companylookup.infrastructure

import com.respiroc.companylookup.domain.model.CompanyInfo
import com.respiroc.companylookup.domain.model.CompanySearchResult

interface CompanyLookupProvider {
    val countryCode: String
    
    fun searchCompanies(query: String): CompanySearchResult
    fun getCompanyInfo(companyId: String): CompanyInfo
} 