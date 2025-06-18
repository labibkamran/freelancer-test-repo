package com.respiroc.user.application

import com.respiroc.user.api.UserInternalApi
import com.respiroc.user.api.result.ForgotResult
import com.respiroc.user.api.result.LoginResult
import com.respiroc.user.api.result.SignupResult
import com.respiroc.util.dto.UserContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.util.Base64

@Service
@Transactional
class UserService() : UserInternalApi {

    private val random: SecureRandom = SecureRandom()
    private val base64Encoder: Base64.Encoder = Base64.getUrlEncoder()
    private val passwordEncoder = BCryptPasswordEncoder()

    override fun signup(email: String, password: String): SignupResult {
        TODO("Not yet implemented")
    }

    override fun loginByEmailPassword(
        email: String,
        password: String
    ): LoginResult {
        TODO("Not yet implemented")
    }

    override fun changePassword(
        user: UserContext,
        oldPassword: String,
        newPassword: String
    ) {
        TODO("Not yet implemented")
    }

    override fun logout(token: String) {
        TODO("Not yet implemented")
    }

    override fun forgetPassword(
        email: String,
        ipAddress: String
    ): ForgotResult {
        TODO("Not yet implemented")
    }

    override fun resetPassword(token: String, newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun setPassword(user: UserContext, newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun findByToken(token: String): UserContext? {
        TODO("Not yet implemented")
    }

    override fun findByEmail(email: String): UserContext? {
        TODO("Not yet implemented")
    }

    override fun generateToken(user: UserContext): String {
        TODO("Not yet implemented")
    }
}