package com.respiroc.util.context

data class UserTenantContext(
    val id: Long,
    val companyName: String,
    val roles: List<TenantRoleContext>
)
