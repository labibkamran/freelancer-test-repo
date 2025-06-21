package com.respiroc.util.context

data class RoleContext(
    val name: String,
    val code: String,
    val description: String,
    val permissions: List<PermissionContext>
)