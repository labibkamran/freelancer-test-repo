package com.respiroc.tenant.infrastructure.context

/**
 * Holds the currently selected tenant for the executing thread / request.
 */
object TenantContextHolder {
    private val currentTenant: ThreadLocal<String?> = ThreadLocal()

    fun setTenantId(tenantId: String?) {
        currentTenant.set(tenantId)
    }

    fun getTenantId(): String? = currentTenant.get()

    fun clear() {
        currentTenant.remove()
    }
} 