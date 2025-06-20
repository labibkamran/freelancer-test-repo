package com.respiroc.util.dto

data class TenantContext(
    val id: Long,
    val roles: List<TenantRoleDTO>
)
