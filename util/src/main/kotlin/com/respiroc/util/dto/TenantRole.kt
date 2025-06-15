package com.respiroc.util.dto

data class TenantRole(
    val name: String,
    val code: String,
    val description: String,
    val permissions: Set<TenantPermission>
)
