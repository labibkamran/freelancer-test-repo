package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WebMainController : BaseController() {

    @GetMapping("/")
    fun home(): String {
        return "redirect:/auth/login"
    }

    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        try {
            val currentUser = user()
            model.addAttribute("user", currentUser)
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