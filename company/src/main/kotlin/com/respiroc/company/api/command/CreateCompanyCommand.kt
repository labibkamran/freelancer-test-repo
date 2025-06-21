package com.respiroc.company.api.command

data class CreateCompanyCommand(val name: String, val organizationNumber: String, val countryCode: String)
