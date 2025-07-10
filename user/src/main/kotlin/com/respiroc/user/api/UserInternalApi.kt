package com.respiroc.user.api

import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.user.api.result.ForgotResult
import com.respiroc.user.api.result.LoginResult
import com.respiroc.util.context.SpringUser
import com.respiroc.util.context.UserContext

interface UserInternalApi {
    fun signupByEmailPassword(email: String, password: String): LoginResult
    fun loginByEmailPassword(email: String, password: String): LoginResult
    fun changePassword(user: SpringUser, oldPassword: String, newPassword: String)
    fun logout(token: String)
    fun forgetPassword(email: String, ipAddress: String): ForgotResult
    fun resetPassword(token: String, newPassword: String)
    fun setPassword(user: SpringUser, newPassword: String)
    fun findByToken(token: String): UserContext?
    fun findByEmail(email: String): UserContext?
    fun generateToken(user: SpringUser): String
    fun addUserTenantRole(tenant: Tenant, role: TenantRole, user: UserContext)
} 