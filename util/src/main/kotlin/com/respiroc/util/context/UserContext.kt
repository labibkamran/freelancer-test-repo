package com.respiroc.util.context

data class UserContext(
    val email: String,
    val password: String,
    val isEnabled: Boolean,
    val isLocked: Boolean,
    var currentTenant: TenantContext?,
    val tenants: List<TenantContext>,
    val roles: List<RoleContext>,
)