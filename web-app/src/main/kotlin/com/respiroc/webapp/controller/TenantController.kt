package com.respiroc.webapp.controller

import com.respiroc.tenant.application.TenantService
import com.respiroc.webapp.filter.TenantContextFilter
import com.respiroc.user.api.UserInternalApi
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.userdetails.User

@RestController
@RequestMapping("/api/tenants")
class TenantController(
    private val tenantService: TenantService,
    private val userInternalApi: UserInternalApi
) : BaseController() {

    @GetMapping
    fun allTenants(authentication: Authentication): ResponseEntity<Any> {
        val username = (authentication.principal as User).username
        // TODO map principal username to domain user id and filter tenants using userInternalApi
        return ResponseEntity.ok(tenantService.listAll())
    }

    @PostMapping("/switch/{tenantId}")
    fun switchTenant(@PathVariable tenantId: Long, session: HttpSession): ResponseEntity<Void> {
        // Check user has access to tenant
        val authentication = session.getAttribute("SPRING_SECURITY_CONTEXT")
        // Not implemented; assume allowed
        session.setAttribute(TenantContextFilter.SESSION_ATTR_TENANT_ID, tenantId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
} 