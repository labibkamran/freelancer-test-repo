package com.respiroc.util.context

data class TenantContext(
    val id: Long,
    val roles: List<TenantRoleContext>
)