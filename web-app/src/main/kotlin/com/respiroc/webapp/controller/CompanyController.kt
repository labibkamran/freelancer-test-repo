package com.respiroc.webapp.controller

import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.companylookup.domain.model.CompanyInfo
import com.respiroc.companylookup.domain.model.CompanySearchResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/companies")
class CompanyController(
    private val companyLookupApi: CompanyLookupInternalApi
) {

    @GetMapping("/search")
    fun searchCompanies(
        @RequestParam query: String,
        @RequestParam countryCode: String
    ): CompanySearchResult {
        return companyLookupApi.search(query, countryCode)
    }

    @GetMapping("/{companyId}")
    fun getCompanyInfo(
        @PathVariable companyId: String,
        @RequestParam countryCode: String
    ): CompanyInfo {
        return companyLookupApi.getInfo(companyId, countryCode)
    }
} 