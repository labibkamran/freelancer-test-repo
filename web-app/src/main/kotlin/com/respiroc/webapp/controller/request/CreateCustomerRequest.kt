package com.respiroc.webapp.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// TODO not implemented yet
data class CreateCustomerRequest(
    @field:NotBlank(message = "Company name is required")
    @field:Size(max = 255, message = "Company name must not exceed 255 characters")
    val name: String,

    @field:NotBlank(message = "Organization number is required")
    @field:Size(max = 36, message = "Organization number must not exceed 36 characters")
    val organizationNumber: String,

    @field:NotBlank(message = "Country code is required")
    val type: String
)
