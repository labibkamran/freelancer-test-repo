package com.respiroc.util.dto

data class Role(
    val name: String,
    val code: String,
    val description: String,
    val permissions: Set<Permission>
)