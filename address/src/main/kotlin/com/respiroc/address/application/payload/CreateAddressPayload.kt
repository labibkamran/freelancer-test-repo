package com.respiroc.address.application.payload

class CreateAddressPayload(
    val countryIsoCode: String,
    val administrativeDivisionCode: String?,
    val city: String,
    val postalCode: String?,
    val addressPart1: String,
    val addressPart2: String?
)