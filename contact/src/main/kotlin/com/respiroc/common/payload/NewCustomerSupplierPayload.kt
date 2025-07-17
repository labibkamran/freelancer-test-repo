package com.respiroc.common.payload

import com.respiroc.customer.domain.model.CustomerType

data class NewCustomerSupplierPayload(
    val name: String,
    val organizationNumber: String?,
    val type: CustomerType,
    val privateCustomer: Boolean,
    val countryCode: String? = "",
    val city: String? = "",
    val postalCode: String? = "",
    val administrativeDivisionCode: String? = "",
    val addressPart1: String? = "",
    val addressPart2: String? = "",
)
