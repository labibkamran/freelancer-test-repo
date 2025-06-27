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
        model.addAttribute("user", springUser)

        val tenantId = TenantContextHolder.getTenantId()!!
        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        model.addAttribute("companies", companies)

        val currentCompany = companies.find { it.tenantId == tenantId }
        model.addAttribute("currentCompany", currentCompany)

        val companyName = currentCompany?.name ?: "Default Company"
        model.addAttribute("title", "$companyName - Dashboard")

        return "dashboard/index"
    }
} 