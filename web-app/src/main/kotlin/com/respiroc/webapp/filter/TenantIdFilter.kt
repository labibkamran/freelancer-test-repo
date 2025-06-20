package com.respiroc.webapp.filter

import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ResponseStatusException

class TenantIdFilter(
    private val paths: List<String> = listOf("/api/execute/**"),
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

        if (tenantId.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant ID is required")
        }

        TenantContextHolder.setTenantId(tenantId.toLong())

        // Wrap the request so the parameter disappears for the rest of the app
        val wrappedRequest = object : HttpServletRequestWrapper(request) {
            override fun getParameter(name: String?): String? =
                if (name == paramName) null else super.getParameter(name)

            override fun getParameterValues(name: String?): Array<String>? =
                if (name == paramName) null else super.getParameterValues(name)

            override fun getParameterMap(): MutableMap<String, Array<String>> =
                super.getParameterMap().toMutableMap().apply { remove(paramName) }
        }

        try {
            chain.doFilter(wrappedRequest, response)
        } finally {
            TenantContextHolder.clear()
        }
    }
}