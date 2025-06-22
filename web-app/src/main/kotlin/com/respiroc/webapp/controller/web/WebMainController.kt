package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WebMainController(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("/")
    fun home(): String {
        return "redirect:/auth/login"
    }

    @GetMapping("/dashboard")
    fun dashboard(
        model: Model
    ): String {
        val user = user()
        val tenantId = TenantContextHolder.getTenantId()
        if (tenantId == null && user.tenants.isNotEmpty()) {
            return "redirect:/dashboard?tenantId=${user.tenants[0].id}";
        } else if (TenantContextHolder.getTenantId() == null) {
            return "redirect:/companies/create"
        }

        model.addAttribute("user", user)

        val companies = companyApi.findAllCompanyByUser(user)
        model.addAttribute("companies", companies)

        val currentCompany = companies.find { it.tenantId == tenantId }
        model.addAttribute("currentCompany", currentCompany)

        return "dashboard/index"
    }
} 