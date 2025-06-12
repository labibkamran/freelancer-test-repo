package com.respiroc.companylookup.infrastructure.brreg

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange("/api")
interface BrregHttpApi {

    @GetExchange("/enheter")
    fun searchEntities(
        @RequestParam("navn") name: String?,
        @RequestParam("navnMetodeForSoek") nameSearchMethod: String? = "FORTLOEPENDE",
        @RequestParam("organisasjonsform") organizationForm: String? = null,
        @RequestParam("postadresse.landkode") postalAddressCountryCodes: List<String>? = null,
        @RequestParam size: Int = 20,
        @RequestParam page: Int = 0
    ): BrregSearchResponse

    @GetExchange("/underenheter")
    fun searchSubEntities(
        @RequestParam("navn") name: String?,
        @RequestParam("navnMetodeForSoek") nameSearchMethod: String? = "FORTLOEPENDE",
        @RequestParam("organisasjonsform") organizationForm: String? = null,
        @RequestParam("postadresse.landkode") postalAddressCountryCodes: List<String>? = null,
        @RequestParam size: Int = 20,
        @RequestParam page: Int = 0
    ): BrregSearchResponse

    @GetExchange("/enheter/{orgnr}")
    fun getEntity(@PathVariable("orgnr") organizationNumber: String): BrregEntityResponse

    @GetExchange("/underenheter/{orgnr}")
    fun getSubEntity(@PathVariable("orgnr") organizationNumber: String): BrregSubEntityResponse
}