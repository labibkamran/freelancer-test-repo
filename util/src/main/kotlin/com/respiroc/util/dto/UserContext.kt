package com.respiroc.util.dto

data class UserContext(
    val email: String,
    val password: String,
    val isEnabled: Boolean,
    val isLocked: Boolean,
    val currentTenant: TenantContext,
    val roles: Set<RoleDTO>,
)