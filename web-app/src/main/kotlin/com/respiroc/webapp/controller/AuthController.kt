package com.respiroc.webapp.controller

import com.respiroc.user.api.UserInternalApi
import com.respiroc.webapp.controller.request.LoginRequest
import com.respiroc.webapp.controller.request.SignupRequest
import com.respiroc.webapp.controller.response.LoginResponse
import com.respiroc.webapp.controller.response.SignupResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/auth"])
class AuthController(
    private val userApi: UserInternalApi,
) : BaseController() {

    @PostMapping(value = ["/signup"])
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        // TODO: Validate request data
        if (request.password != request.confirmPassword) {
            throw IllegalArgumentException("Passwords don't match")
        }
        val result = userApi.signupByEmailPassword(request.email, request.password)
        return ResponseEntity.ok(SignupResponse(result.token))
    }

    @PostMapping(value = ["/login"])
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        // TODO: Validate request data
        val result = userApi.loginByEmailPassword(email = request.email, password = request.password)
        return ResponseEntity.ok(LoginResponse(result.token))
    }
} 