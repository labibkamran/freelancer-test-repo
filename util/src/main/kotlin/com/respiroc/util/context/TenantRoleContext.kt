package com.respiroc.util.context

data class TenantRoleContext(
    val name: String,
    val code: String,
    val description: String,
    val permissions: List<TenantPermissionContext>
)