package com.respiroc.companylookup.infrastructure.brreg

import com.respiroc.companylookup.domain.model.*
import com.respiroc.companylookup.infrastructure.CompanyLookupProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component("NO")
class BrregCompanyLookupProvider(
    private val brregHttpApi: BrregHttpApi
) : CompanyLookupProvider {
    
    private val logger = LoggerFactory.getLogger(BrregCompanyLookupProvider::class.java)
    
    override val countryCode: String = "NO"

    override fun searchCompanies(query: String): CompanySearchResult {
        logger.debug("Searching Norwegian companies with query: $query")
        
        val response = brregHttpApi.searchEntities(
            name = query,
            nameSearchMethod = null,
            size = 20,
            page = 0
        )

        val companies = response.embedded?.entities?.map { entity ->
            CompanyBasicInfo(
                id = entity.organizationNumber,
                name = entity.name,
                registrationNumber = entity.organizationNumber,
                address = entity.businessAddress?.addressLines?.joinToString(", ")
            )
        } ?: emptyList()

        return CompanySearchResult(
            companies = companies,
            totalResults = response.page?.totalElements ?: 0,
            countryCode = countryCode
        )
    }

    override fun getCompanyInfo(companyId: String): CompanyInfo {
        logger.debug("Getting Norwegian company info for ID: $companyId")
        val response = brregHttpApi.getEntity(companyId)

        val businessAddress = response.businessAddress
        return CompanyInfo(
            id = response.organizationNumber,
            name = response.name,
            registrationNumber = response.organizationNumber,
            address = CompanyAddressInfo(
                address = businessAddress?.addressLines?.joinToString(", "),
                postalCode = businessAddress?.postalCode ?: extractPostalCode(businessAddress),
                countryCode = businessAddress?.countryCode,
                city = businessAddress?.city,
                municipality = businessAddress?.municipality,
            ),
            countryCode = countryCode,
            foundedDate = response.establishmentDate,
            status = when {
                response.bankrupt == true -> "BANKRUPT"
                response.underLiquidation == true -> "UNDER_LIQUIDATION"
                response.compulsoryLiquidation == true -> "COMPULSORY_LIQUIDATION"
                else -> "ACTIVE"
            },
            industry = response.industryCode1?.description,
            contactInfo = ContactInfo(
                email = response.email,
                phone = response.phone,
                website = response.website
            ),
            financialInfo = null // TODO : Check from Brreg API
        )
    }

    private fun extractPostalCode(address: BrregAddress?): String? {
        if (address == null) return null
        if (address.postalCode != null && address.postalCode.isNotEmpty()) return address.postalCode
        if (address.countryCode?.equals("NO") == true) return null
        if (address.city == null) return null
        val match = countryCodeRegexMap[address.countryCode]?.find(address.city)
        return match?.groupValues?.get(1)
    }
    // TODO: find better solution and add other countries
    private val countryCodeRegexMap = mapOf(
        "DE" to Regex("(?i)\\s*(\\d{5})\\b"),                            // Germany
        "GB" to Regex("(?i)\\s*+([A-Z]{1,2}\\d{1,2}[A-Z]?\\s\\d[A-Z]{2})\\b"), // UK
        "AT" to Regex("(?i)\\s*(\\d{4})\\b"),                   // Australia
        "FR" to Regex("(?i)\\s*(\\d{5})\\b"),                   // France
        "DK" to Regex("(?i)\\s*(\\d{4})\\b"),                   // Denmark
    )
}