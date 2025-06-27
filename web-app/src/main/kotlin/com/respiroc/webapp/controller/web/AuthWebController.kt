package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.user.api.UserInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.LoginRequest
import com.respiroc.webapp.controller.request.SignupRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.Cookie

@Controller
@RequestMapping("/auth")
class AuthWebController(
    private val userApi: UserInternalApi,
    private val companyApi: CompanyInternalApi
) : BaseController() {

    private val JWT_TOKEN_PERIOD : Int = 24 * 60 * 60 // 24 hours (adjust based on JWT expiration)

    @GetMapping("/login")
    fun loginPage(model: Model): String {
        // If user is already authenticated, redirect appropriately
        try {
            val springUser = springUser()
            val companies = companyApi.findAllCompanyByUser(springUser.ctx)
            
            return if (companies.isEmpty()) {
                "redirect:/company/create"
            } else {
                "redirect:/company/select"
            }
        } catch (_: Exception) {
            // User not authenticated, continue to login page
        }
        model.addAttribute("title", "Login")
        return "auth/login"
    }

    @PostMapping("/login")
    fun login(
        @ModelAttribute loginRequest: LoginRequest,
        redirectAttributes: RedirectAttributes,
        response: HttpServletResponse
    ): String {
        try {
            val result = userApi.loginByEmailPassword(
                email = loginRequest.email,
                password = loginRequest.password
            )
            
            // Set JWT token in HTTP-only cookie
            val jwtCookie = Cookie("jwt_token", result.token)
            jwtCookie.isHttpOnly = true
            jwtCookie.secure = false // Set to true in production with HTTPS
            jwtCookie.path = "/"
            jwtCookie.maxAge = JWT_TOKEN_PERIOD
            response.addCookie(jwtCookie)
            
            // Redirect based on user's companies
            val springUser = springUser()
            val companies = companyApi.findAllCompanyByUser(springUser.ctx)
            
            return if (companies.isEmpty()) {
                "redirect:/company/create"
            } else {
                "redirect:/company/select"
            }
        } catch (_: Exception) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password")
            return "redirect:/auth/login"
        }
    }

    @GetMapping("/signup")
    fun signupPage(model: Model): String {
        // If user is already authenticated, redirect appropriately
        try {
            val springUser = springUser()
            val companies = companyApi.findAllCompanyByUser(springUser.ctx)
            
            return if (companies.isEmpty()) {
                "redirect:/company/create"
            } else {
                "redirect:/company/select"
            }
        } catch (_: Exception) {
            // User not authenticated, continue to signup page
        }
        model.addAttribute("title", "Sign Up")
        return "auth/signup"
    }

    @PostMapping("/signup")
    fun signup(
        @ModelAttribute signupRequest: SignupRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            userApi.signupByEmailPassword(
                signupRequest.email,
                signupRequest.password
            )
            
            redirectAttributes.addFlashAttribute("success", "Registration successful. Please login")
            return "redirect:/auth/login"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: ${e.message}")
            return "redirect:/auth/signup"
        }
    }

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): String {
        // Clear the JWT cookie
        val jwtCookie = Cookie("jwt_token", "")
        jwtCookie.isHttpOnly = true
        jwtCookie.secure = false
        jwtCookie.path = "/"
        jwtCookie.maxAge = 0 // Expire immediately
        response.addCookie(jwtCookie)
        
        return "redirect:/auth/login"
    }
} 