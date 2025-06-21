package com.respiroc.tenant.infrastructure.context

/**
 * Holds the currently selected tenant for the executing thread / request.
 */
object TenantContextHolder {
    private val currentTenant: ThreadLocal<Long?> = ThreadLocal()

    fun setTenantId(tenantId: Long?) {
        currentTenant.set(tenantId)
    }

    fun getTenantId(): Long? = currentTenant.get()

    fun clear() {
        currentTenant.remove()
    }
} 