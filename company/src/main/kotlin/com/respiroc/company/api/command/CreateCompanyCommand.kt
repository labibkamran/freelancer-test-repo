package com.respiroc.company.api.command

data class CreateCompanyCommand(
    val name: String,
    val organizationNumber: String,
    val countryCode: String,
    val addressCountryCode: String?,
    val administrativeDivisionCode: String?,
    val city: String?,
    val postalCode: String?,
    val primaryAddress: String?,
    val secondaryAddress: String?
)
