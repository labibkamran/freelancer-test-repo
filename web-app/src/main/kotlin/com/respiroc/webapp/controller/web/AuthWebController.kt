package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthWebController: BaseController() {

    @GetMapping("/login")
    fun loginPage(model: Model): String {
        if (isLoggedIn())
            return "redirect:/dashboard"
        model.addAttribute(titleAttributeName, "Login")
        return "auth/login"
    }

    @GetMapping("/signup")
    fun signupPage(model: Model): String {
        if (isLoggedIn())
            return "redirect:/dashboard"
        model.addAttribute(titleAttributeName, "Sign Up")
        return "auth/signup"
    }

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): String {
        val jwtCookie = Cookie("token", "")
        jwtCookie.isHttpOnly = true
        jwtCookie.secure = false
        jwtCookie.path = "/"
        jwtCookie.maxAge = 0
        response.addCookie(jwtCookie)

        return "redirect:/auth/login"
    }

    private fun isLoggedIn(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null &&
                authentication.isAuthenticated &&
                authentication !is AnonymousAuthenticationToken
    }
} 