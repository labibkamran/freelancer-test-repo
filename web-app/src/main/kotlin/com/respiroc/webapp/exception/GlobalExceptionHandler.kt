package com.respiroc.webapp.exception

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.util.context.SpringUser
import com.respiroc.webapp.controller.BaseController
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@ControllerAdvice
class GlobalExceptionHandler(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @ExceptionHandler(TenantNotFoundException::class)
    fun handleTenantNotFound(ex: TenantNotFoundException): ModelAndView {
        val modelAndView = ModelAndView("error/tenant-not-found")
        modelAndView.addObject("errorMessage", ex.message)
        modelAndView.addObject("user", getCurrentSpringUser())
        return modelAndView
    }

    @ExceptionHandler(TenantAccessDeniedException::class)
    fun handleTenantAccessDenied(ex: TenantAccessDeniedException): ModelAndView {
        val modelAndView = ModelAndView("error/tenant-access-denied")
        modelAndView.addObject("errorMessage", ex.message)
        modelAndView.addObject("user", getCurrentSpringUser())
        return modelAndView
    }

    @ExceptionHandler(NoTenantProvidedException::class)
    fun handleNoTenantProvided(ex: NoTenantProvidedException): String {
        val springUser = getCurrentSpringUser()
        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        
        return if (companies.isEmpty()) {
            "redirect:/company/create"
        } else {
            "redirect:/company/select"
        }
    }

    private fun getCurrentSpringUser(): SpringUser {
        return SecurityContextHolder.getContext().authentication.principal as SpringUser
    }
}

@Controller
@RequestMapping("/error")
class ErrorController : BaseController() {

    @GetMapping("/404")
    fun notFound(model: Model): String {
        model.addAttribute("user", getCurrentSpringUser())
        return "error/404"
    }

    @GetMapping("/403")
    fun forbidden(model: Model): String {
        model.addAttribute("user", getCurrentSpringUser())
        return "error/403"
    }

    @GetMapping("/400")
    fun badRequest(model: Model): String {
        model.addAttribute("user", getCurrentSpringUser())
        return "error/400"
    }

    @GetMapping("/500")
    fun internalError(model: Model): String {
        model.addAttribute("user", getCurrentSpringUser())
        return "error/500"
    }

    private fun getCurrentSpringUser(): SpringUser? {
        return try {
            SecurityContextHolder.getContext().authentication?.principal as? SpringUser
        } catch (e: Exception) {
            null
        }
    }
} 