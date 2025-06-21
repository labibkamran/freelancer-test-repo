package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WebMainController : BaseController() {

    @GetMapping("/")
    fun home(): String {
        return "redirect:/auth/login"
    }

    @GetMapping("/dashboard")
    fun dashboard(
        model: Model,
        @RequestParam(name = "tenantId", required = false) tenantId: String?
    ): String {
        try {
            val currentUser = user()
            model.addAttribute("user", currentUser)
            
            // TODO: Replace with actual tenant/company data from services
            val currentTenantId = tenantId ?: "23"
            model.addAttribute("currentTenantId", currentTenantId)
            
            // TODO: Fetch actual companies from CompanyService
            val companies = listOf(
                mapOf("id" to 1, "name" to "Example Company AS"),
                mapOf("id" to 2, "name" to "Test Company AS"),
                mapOf("id" to 23, "name" to "Default Company AS")
            )
            model.addAttribute("companies", companies)
            
            // TODO: Set current company based on tenantId
            val currentCompany = companies.find { it["id"] == currentTenantId.toIntOrNull() }
                ?: mapOf("id" to 23, "name" to "Default Company AS")
            model.addAttribute("currentCompany", currentCompany)
            
            // TODO: Replace with actual business logic
            // Show cash flow alert conditionally
            model.addAttribute("showCashFlowAlert", currentTenantId == "23")
            
        } catch (e: Exception) {
            return "redirect:/auth/login"
        }
        return "dashboard/index"
    }

    @GetMapping("/companies/create")
    fun createCompany(model: Model): String {
        try {
            val currentUser = user()
            model.addAttribute("user", currentUser)
        } catch (e: Exception) {
            return "redirect:/auth/login"
        }
        return "company/create"
    }
} 