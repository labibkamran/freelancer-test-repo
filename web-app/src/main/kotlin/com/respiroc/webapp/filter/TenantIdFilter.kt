package com.respiroc.webapp.filter

import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.util.context.SpringUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ResponseStatusException

class TenantIdFilter(
    private val paths: List<String> = listOf("/dashboard/**"),
    private val paramName: String = "tenantId"
) : OncePerRequestFilter() {

    private val matcher = AntPathMatcher()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        paths.none { matcher.match(it, request.requestURI) }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val tenantId = request.getParameter(paramName)

        // Wrap the request so the parameter disappears for the rest of the app
        val wrappedRequest = object : HttpServletRequestWrapper(request) {
            override fun getParameter(name: String?): String? =
                if (name == paramName) null else super.getParameter(name)

            override fun getParameterValues(name: String?): Array<String>? =
                if (name == paramName) null else super.getParameterValues(name)

            override fun getParameterMap(): MutableMap<String, Array<String>> =
                super.getParameterMap().toMutableMap().apply { remove(paramName) }
        }

        if (!tenantId.isNullOrBlank() && isTenantAccessibleByUser(tenantId)) {
            setCurrentTenant(tenantId)
        } else if (!tenantId.isNullOrBlank()) {
            // TODO: Use custom error for better visibility
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot access this resource")
        }

        try {
            chain.doFilter(wrappedRequest, response)
        } finally {
            TenantContextHolder.clear()
        }
    }

    private fun isTenantAccessibleByUser(tenantId: String): Boolean {
        val springUser = SecurityContextHolder.getContext().authentication.principal as SpringUser
        return springUser.ctx.tenants.any { it.id == tenantId.toLong() }
    }

    private fun setCurrentTenant(tenantId: String) {
        TenantContextHolder.setTenantId(tenantId.toLong())
        val springUser = SecurityContextHolder.getContext().authentication.principal as SpringUser
        springUser.ctx.currentTenant = springUser.ctx.tenants.filter { it.id == tenantId.toLong() }.first()
    }
}