package com.respiroc.webapp.controller

import com.respiroc.user.application.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) : BaseController() {

    data class RegisterCommand(
        @field:NotBlank val username: String,
        @field:Email val email: String,
        @field:NotBlank val displayName: String,
        @field:NotBlank val password: String
    )

    @PostMapping("/register")
    fun register(@Valid @RequestBody cmd: RegisterCommand): ResponseEntity<Map<String, Any?>> {
        val user = userService.registerUser(cmd.username, cmd.email, cmd.displayName, cmd.password)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("id" to user.id))
    }
} 