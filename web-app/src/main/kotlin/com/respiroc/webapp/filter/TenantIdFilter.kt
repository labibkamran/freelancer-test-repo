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
        val springUser = getCurrentSpringUser()

        // Wrap the request so the parameter disappears for the rest of the app
        val wrappedRequest = createWrappedRequest(request)

        when {
            !tenantId.isNullOrBlank() && isTenantAccessible(tenantId, springUser) -> {
                setCurrentTenant(tenantId, springUser)
            }
            !tenantId.isNullOrBlank() -> {
                // TODO: Use custom error for better visibility
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot access this resource")
            }
            springUser.ctx.tenants.isNotEmpty() -> {
                setFirstAvailableTenant(springUser)
            }
        }

        try {
            chain.doFilter(wrappedRequest, response)
        } finally {
            TenantContextHolder.clear()
        }
    }

    private fun getCurrentSpringUser(): SpringUser {
        return try {
            SecurityContextHolder.getContext().authentication.principal as SpringUser
        } catch (e: ClassCastException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user context")
        }
    }

    private fun createWrappedRequest(request: HttpServletRequest): HttpServletRequestWrapper {
        return object : HttpServletRequestWrapper(request) {
            override fun getParameter(name: String?): String? =
                if (name == paramName) null else super.getParameter(name)

            override fun getParameterValues(name: String?): Array<String>? =
                if (name == paramName) null else super.getParameterValues(name)

            override fun getParameterMap(): MutableMap<String, Array<String>> =
                super.getParameterMap().toMutableMap().apply { remove(paramName) }
        }
    }

    private fun isTenantAccessible(tenantId: String, springUser: SpringUser): Boolean {
        return springUser.ctx.tenants.any { it.id == tenantId.toLong() }
    }

    private fun setCurrentTenant(tenantId: String, springUser: SpringUser) {
        val tenantIdLong = tenantId.toLong()
        val tenant = springUser.ctx.tenants.find { it.id == tenantIdLong }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant not found")

        TenantContextHolder.setTenantId(tenantIdLong)
        springUser.ctx.currentTenant = tenant
    }

    private fun setFirstAvailableTenant(springUser: SpringUser) {
        val tenant = springUser.ctx.tenants.first()
        TenantContextHolder.setTenantId(tenant.id)
        springUser.ctx.currentTenant = tenant
    }
}