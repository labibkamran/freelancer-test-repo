package com.respiroc.customer.api.payload

import com.respiroc.customer.domain.model.CustomerType

data class NewCustomerPayload(val name: String, val organizationNumber: String, val type: CustomerType)
