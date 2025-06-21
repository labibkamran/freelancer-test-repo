package com.respiroc.util.constant

enum class TenantPermissionCode(val code: String) {
    ALL_READ("all:read"),
    ALL_WRITE("all:write"),
    HOURS_SUBMIT("hours:submit"),
    EXPENSES_SUBMIT("expenses:submit")
}