package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/tenant")
class TenantController(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("/select")
    fun selectTenant(model: Model): String {
        val springUser = springUser()
        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        
        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("title", "Select Company")
        
        return "tenant/select"
    }

    @GetMapping("/switch")
    fun switchTenant(@RequestParam tenantId: Long): String {
        // Simply redirect to dashboard with the new tenantId
        // The TenantIdFilter will handle the validation
        return "redirect:/dashboard?tenantId=$tenantId"
    }
} 