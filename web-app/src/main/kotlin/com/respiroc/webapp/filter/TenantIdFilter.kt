package com.respiroc.webapp.filter

import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.util.context.SpringUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

class TenantIdFilter(
    private val paths: List<String> = listOf("/dashboard/**", "/ledger/**", "/companies/**"),
    private val excludePaths: List<String> = listOf("/companies/create", "/tenant/**", "/error/**"),
    private val paramName: String = "tenantId"
) : OncePerRequestFilter() {

    private val matcher = AntPathMatcher()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        return paths.none { matcher.match(it, uri) } || 
               excludePaths.any { matcher.match(it, uri) }
    }

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
            !tenantId.isNullOrBlank() && !isTenantExists(tenantId, springUser) -> {
                response.sendRedirect("/error/tenant-not-found")
                return
            }
            !tenantId.isNullOrBlank() && !isTenantAccessible(tenantId, springUser) -> {
                response.sendRedirect("/error/tenant-access-denied")
                return
            }
            tenantId.isNullOrBlank() -> {
                // Handle no tenant case - redirect based on user's companies
                val companies = try {
                    springUser.ctx.tenants
                } catch (e: Exception) {
                    emptyList()
                }
                
                if (companies.isEmpty()) {
                    response.sendRedirect("/companies/create")
                } else {
                    response.sendRedirect("/tenant/select")
                }
                return
            }
        }

        try {
            chain.doFilter(wrappedRequest, response)
        } finally {
            TenantContextHolder.clear()
        }
    }

    private fun getCurrentSpringUser(): SpringUser {
        return SecurityContextHolder.getContext().authentication.principal as SpringUser
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

    private fun isTenantExists(tenantId: String, springUser: SpringUser): Boolean {
        return try {
            springUser.ctx.tenants.any { it.id == tenantId.toLong() }
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isTenantAccessible(tenantId: String, springUser: SpringUser): Boolean {
        return try {
            springUser.ctx.tenants.any { it.id == tenantId.toLong() }
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun setCurrentTenant(tenantId: String, springUser: SpringUser) {
        val tenantIdLong = tenantId.toLong()
        val tenant = springUser.ctx.tenants.find { it.id == tenantIdLong }
            ?: return // This shouldn't happen due to prior checks

        TenantContextHolder.setTenantId(tenantIdLong)
        springUser.ctx.currentTenant = tenant
    }
}