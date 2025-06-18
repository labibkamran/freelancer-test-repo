package com.respiroc.webapp.controller

import com.respiroc.user.api.UserInternalApi
import com.respiroc.webapp.controller.request.SignupRequest
import com.respiroc.webapp.controller.response.SignupResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/auth"])
class AuthController(
    private val userApi: UserInternalApi,
) : BaseController() {

    @PostMapping(value = ["/signup"])
    fun signup(@RequestBody signupRequest: SignupRequest): ResponseEntity<SignupResponse> {
        if (signupRequest.password != signupRequest.confirmPassword) {
            throw IllegalArgumentException("Passwords don't match")
        }
        val result = userApi.signupByEmailPassword(signupRequest.email, signupRequest.password)
        return ResponseEntity.ok(SignupResponse(result.token))
    }
} 