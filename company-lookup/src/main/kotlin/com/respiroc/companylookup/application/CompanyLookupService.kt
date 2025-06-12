package com.respiroc.companylookup.application

import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.companylookup.domain.model.CompanyInfo
import com.respiroc.companylookup.domain.model.CompanySearchResult
import com.respiroc.companylookup.infrastructure.CompanyLookupProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CompanyLookupService(
    private val providers: Map<String, CompanyLookupProvider>
) : CompanyLookupInternalApi {

    private val logger = LoggerFactory.getLogger(CompanyLookupService::class.java)

    override fun search(query: String, countryCode: String): CompanySearchResult {
        logger.info("Searching for companies with query: $query in country: $countryCode")
        
        val provider = getProvider(countryCode)
        return provider.searchCompanies(query)
    }

    override fun getInfo(companyId: String, countryCode: String): CompanyInfo {
        logger.info("Getting info for company ID: $companyId in country: $countryCode")
        
        val provider = getProvider(countryCode)
        return provider.getCompanyInfo(companyId)
    }

    private fun getProvider(countryCode: String): CompanyLookupProvider {
        val normalizedCountryCode = countryCode.uppercase()
        logger.info("Looking for provider with country code: $normalizedCountryCode")
        logger.info("Available providers: ${providers.keys}")
        return providers[normalizedCountryCode] 
            ?: throw IllegalArgumentException("No provider available for country code: $countryCode")
    }
} 