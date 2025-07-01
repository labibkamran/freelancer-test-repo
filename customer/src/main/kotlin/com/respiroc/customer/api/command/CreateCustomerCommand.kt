package com.respiroc.customer.api.command

import com.respiroc.customer.domain.model.CustomerType

data class CreateCustomerCommand(val name: String, val organizationNumber: String, val type: CustomerType)
