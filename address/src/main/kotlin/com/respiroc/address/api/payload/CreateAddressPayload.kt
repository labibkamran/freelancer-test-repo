package com.respiroc.address.api.payload

class CreateAddressPayload(
    val countryIsoCode: String,
    val administrativeDivisionCode: String?,
    val city: String,
    val postalCode: String,
    val primaryAddress: String,
    val secondaryAddress: String?
)