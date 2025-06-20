package com.respiroc.util.dto

data class TenantRoleDTO(
    val name: String,
    val code: String,
    val description: String,
    val permissions: List<TenantPermissionDTO>
)
