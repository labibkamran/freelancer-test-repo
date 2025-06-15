package com.respiroc.user.application

import com.respiroc.user.api.UserInternalApi
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

    override fun signup() {
        TODO("Not yet implemented")
    }

    override fun loginByUsernamePassword() {
        TODO("Not yet implemented")
    }

    override fun changePassword() {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun forgetPassword() {
        TODO("Not yet implemented")
    }

    override fun resetPassword() {
        TODO("Not yet implemented")
    }

    override fun setPassword() {
        TODO("Not yet implemented")
    }

    override fun findByToken(token: String): UserContext? {
        TODO("Not yet implemented")
    }

    override fun findByEmail() {
        TODO("Not yet implemented")
    }

    override fun generateToken() {
        TODO("Not yet implemented")
    }
}