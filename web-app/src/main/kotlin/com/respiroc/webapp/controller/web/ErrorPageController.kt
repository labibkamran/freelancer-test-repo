package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/error")
class ErrorPageController(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("/tenant-not-found")
    fun tenantNotFound(model: Model): String {
        try {
            addCommonAttributes(model, companyApi, "Tenant Not Found")
            model.addAttribute("errorMessage", "The requested company was not found")
        } catch (e: Exception) {
            addCommonAttributes(model, "Tenant Not Found")
            model.addAttribute("user", null)
            model.addAttribute("companies", emptyList<Any>())
            model.addAttribute("errorMessage", "The requested company was not found")
        }

        return "error/tenant-not-found"
    }

    @GetMapping("/tenant-access-denied")
    fun tenantAccessDenied(model: Model): String {
        try {
            addCommonAttributes(model, companyApi, "Access Denied")
            model.addAttribute("errorMessage", "You don't have access to this company")
        } catch (e: Exception) {
            addCommonAttributes(model, "Access Denied")
            model.addAttribute("user", null)
            model.addAttribute("companies", emptyList<Any>())
            model.addAttribute("errorMessage", "You don't have access to this company")
        }

        return "error/tenant-access-denied"
    }
} 