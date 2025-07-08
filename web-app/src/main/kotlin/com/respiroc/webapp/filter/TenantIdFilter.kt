package com.respiroc.webapp.filter

import com.respiroc.util.context.SpringUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

class TenantIdFilter(
    private val paths: List<String> = listOf(
        "/dashboard/**",
        "/voucher/**",
        "/company/**",
        "/report/**",
        "/ledger/**",
        "/customer/**"
    ),
    private val excludePaths: List<String> = listOf(
        "/company/create",
        "/company/search",
        "/assets/**",
        "/errors/**",
        "/error/**"
    ),
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

        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            chain.doFilter(request, response)
            return
        }

        val tenantId = request.getParameter(paramName)
        val springUser = getCurrentSpringUser()

        // Wrap the request so the parameter disappears for the rest of the app
        val wrappedRequest = createWrappedRequest(request)

        if (!tenantId.isNullOrBlank() && isTenantAccessible(tenantId, springUser)) {
            setCurrentTenant(tenantId, springUser)
        } else if (!tenantId.isNullOrBlank()) {
            response.sendRedirect("/error/tenant-access-denied")
            return
        } else {
            val tenants = try {
                springUser.ctx.tenants
            } catch (e: Exception) {
                emptyList()
            }

            if (tenants.isEmpty()) {
                response.sendRedirect("/company/create")
            } else {
                response.sendRedirect("/dashboard?tenantId=${tenants[0].id}")
            }
            return
        }

        chain.doFilter(wrappedRequest, response)
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
        springUser.ctx.currentTenant = tenant
    }
}