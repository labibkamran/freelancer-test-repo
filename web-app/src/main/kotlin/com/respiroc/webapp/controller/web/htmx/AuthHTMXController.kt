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

            setJwtCookie(result.token, response)
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
        response: HttpServletResponse,
        model: Model
    ): String {
        try {
            val result = userApi.signupByEmailPassword(
                signupRequest.email,
                signupRequest.password
            )

            setJwtCookie(result.token, response)
            return "redirect:htmx:/dashboard"
        } catch (e: Exception) {
            model.addAttribute(errorMessageAttributeName, e.message ?: "An error occurred during registration")
            return "fragments/error-message"
        }
    }

    private fun setJwtCookie(token: String, response: HttpServletResponse) {
        val jwtCookie = Cookie("token", token)
        jwtCookie.isHttpOnly = true
        jwtCookie.secure = false // Set to true in production with HTTPS
        jwtCookie.path = "/"
        jwtCookie.maxAge = JWT_TOKEN_PERIOD
        response.addCookie(jwtCookie)
    }
} 