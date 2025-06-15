package com.respiroc.webapp.filter

import com.respiroc.tenant.infrastructure.context.  TenantContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TenantContextFilter : OncePerRequestFilter() {
    companion object {
        const val SESSION_ATTR_TENANT_ID = "ACTIVE_TENANT_ID"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val tenantId = request.session?.getAttribute(SESSION_ATTR_TENANT_ID) as? Long
            TenantContextHolder.setTenantId(tenantId)
            filterChain.doFilter(request, response)
        } finally {
            TenantContextHolder.clear()
        }
    }
} 