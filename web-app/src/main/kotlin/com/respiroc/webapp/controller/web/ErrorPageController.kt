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
            val springUser = springUser()
            val companies = companyApi.findAllCompanyByUser(springUser.ctx)
            
            model.addAttribute("user", springUser)
            model.addAttribute("companies", companies)
            model.addAttribute("title", "Tenant Not Found")
            model.addAttribute("errorMessage", "The requested company was not found")
        } catch (e: Exception) {
            model.addAttribute("user", null)
            model.addAttribute("companies", emptyList<Any>())
            model.addAttribute("title", "Tenant Not Found")
            model.addAttribute("errorMessage", "The requested company was not found")
        }
        
        return "error/tenant-not-found"
    }

    @GetMapping("/tenant-access-denied")
    fun tenantAccessDenied(model: Model): String {
        try {
            val springUser = springUser()
            val companies = companyApi.findAllCompanyByUser(springUser.ctx)
            
            model.addAttribute("user", springUser)
            model.addAttribute("companies", companies)
            model.addAttribute("title", "Access Denied")
            model.addAttribute("errorMessage", "You don't have access to this company")
        } catch (e: Exception) {
            model.addAttribute("user", null)
            model.addAttribute("companies", emptyList<Any>())
            model.addAttribute("title", "Access Denied")
            model.addAttribute("errorMessage", "You don't have access to this company")
        }
        
        return "error/tenant-access-denied"
    }
} 