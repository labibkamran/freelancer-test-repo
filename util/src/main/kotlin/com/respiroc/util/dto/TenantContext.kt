package com.respiroc.util.dto

data class TenantContext(
    val id: String,
    val name: String,
    val roles: Set<TenantRole>
)
