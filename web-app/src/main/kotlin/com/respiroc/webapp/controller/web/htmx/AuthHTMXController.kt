package com.respiroc.webapp.controller.web.htmx

import com.respiroc.user.api.UserInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.LoginRequest
import com.respiroc.webapp.controller.request.SignupRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/htmx/auth")
class AuthHTMXController(
    private val userApi: UserInternalApi
) : BaseController() {

    private val JWT_TOKEN_PERIOD: Int = 24 * 60 * 60

    @PostMapping("/login")
    @HxRequest
    fun loginHTMX(
        @ModelAttribute loginRequest: LoginRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        try {
            val result = userApi.loginByEmailPassword(
                email = loginRequest.email,
                password = loginRequest.password
            )

            val jwtCookie = Cookie("token", result.token)
            jwtCookie.isHttpOnly = true
            jwtCookie.secure = false // Set to true in production with HTTPS
            jwtCookie.path = "/"
            jwtCookie.maxAge = JWT_TOKEN_PERIOD
            response.addCookie(jwtCookie)

            return "redirect:htmx:/dashboard"
        } catch (_: Exception) {
            model.addAttribute(errorMessageAttributeName, "Invalid email or password")
            return "fragments/error-message"
        }
    }

    @PostMapping("/signup")
    @HxRequest
    fun signupHTMX(
        @ModelAttribute signupRequest: SignupRequest,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            userApi.signupByEmailPassword(
                signupRequest.email,
                signupRequest.password
            )
            redirectAttributes.addFlashAttribute(successMessageAttributeName, "Registration successful. Please login.")
            return "redirect:htmx:/auth/login"
        } catch (e: Exception) {
            model.addAttribute(errorMessageAttributeName, e.message ?: "An error occurred during registration")
            return "fragments/error-message"
        }
    }
} 