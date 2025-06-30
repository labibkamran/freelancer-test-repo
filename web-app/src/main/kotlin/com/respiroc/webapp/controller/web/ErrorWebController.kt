package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/error", "/errors"])
class ErrorWebController: BaseController() {

    @GetMapping("/tenant-not-found")
    fun tenantNotFound(model: Model): String {
        model.addAttribute("title", "Tenant Not Found")
        model.addAttribute("errorMessage", "The requested company was not found")
        return "error/tenant-not-found"
    }

    @GetMapping("/tenant-access-denied")
    fun tenantAccessDenied(model: Model): String {
        model.addAttribute("title", "Access Denied")
        model.addAttribute("errorMessage", "You don't have access to this company")
        return "error/tenant-access-denied"
    }
} 