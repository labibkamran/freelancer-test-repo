package com.respiroc.user.api

import com.respiroc.user.api.result.ForgotResult
import com.respiroc.user.api.result.LoginResult
import com.respiroc.user.api.result.SignupResult
import com.respiroc.util.dto.UserContext

interface UserInternalApi {
    fun signup(email: String, password: String): SignupResult
    fun loginByEmailPassword(email: String, password: String): LoginResult
    fun changePassword(user: UserContext, oldPassword: String, newPassword: String)
    fun logout(token: String)
    fun forgetPassword(email: String, ipAddress: String): ForgotResult
    fun resetPassword(token: String, newPassword: String)
    fun setPassword(user: UserContext, newPassword: String)
    fun findByToken(token: String): UserContext?
    fun findByEmail(email: String): UserContext?
    fun generateToken(user: UserContext): String
} 