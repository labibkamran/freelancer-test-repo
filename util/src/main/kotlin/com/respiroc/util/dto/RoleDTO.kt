package com.respiroc.util.dto

data class RoleDTO(
    val name: String,
    val code: String,
    val description: String,
    val permissions: Set<PermissionDTO>
)