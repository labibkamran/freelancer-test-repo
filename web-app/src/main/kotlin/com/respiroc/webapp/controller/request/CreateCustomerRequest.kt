package com.respiroc.webapp.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// TODO not implemented yet
data class CreateCustomerRequest(

    @field:NotBlank(message = "Company name is required")
    val name: String,

    @field:NotBlank(message = "Organization number is required")
    @field:Size(max = 36, message = "Organization number must not exceed 36 characters")
    val organizationNumber: String,

    @field:NotBlank(message = "Type  is required")
    val type: String
)
