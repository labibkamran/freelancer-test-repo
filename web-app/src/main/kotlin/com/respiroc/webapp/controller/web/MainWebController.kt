package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainWebController(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("/")
    fun home(): String {
        return try {
            val springUser = springUser()
            
            if (springUser.ctx.tenants.isEmpty()) {
                "redirect:/company/create"
            } else {
                "redirect:/company/select"
            }
        } catch (_: Exception) {
            // User not authenticated
            "redirect:/auth/login"
        }
    }

    @GetMapping("/dashboard")
    fun dashboard(
        model: Model
    ): String {
        val springUser = springUser()
        val tenantId = TenantContextHolder.getTenantId()!!
        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        val currentCompany = companies.find { it.tenantId == tenantId }
        val companyName = currentCompany?.name ?: "Default Company"
        addCommonAttributes(model, companyApi, "$companyName - Dashboard")

        return "dashboard/index"
    }
} 