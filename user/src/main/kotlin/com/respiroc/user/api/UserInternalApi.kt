package com.respiroc.user.api

import com.respiroc.util.dto.UserContext

interface UserInternalApi {
    fun signup()
    fun loginByUsernamePassword()
    fun changePassword()
    fun logout()
    fun forgetPassword()
    fun resetPassword()
    fun setPassword()
    fun findByToken(token: String): UserContext?
    fun findByEmail()
    fun generateToken()
} 