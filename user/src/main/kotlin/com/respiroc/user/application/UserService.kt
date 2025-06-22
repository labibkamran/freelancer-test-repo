package com.respiroc.user.application

import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantPermission
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.user.api.UserInternalApi
import com.respiroc.user.api.result.ForgotResult
import com.respiroc.user.api.result.LoginResult
import com.respiroc.user.application.jwt.JwtUtils
import com.respiroc.user.domain.model.Permission
import com.respiroc.user.domain.model.Role
import com.respiroc.user.domain.model.User
import com.respiroc.user.domain.model.UserSession
import com.respiroc.user.domain.model.UserTenantRole
import com.respiroc.user.domain.repository.UserRepository
import com.respiroc.user.domain.repository.UserSessionRepository
import com.respiroc.user.domain.repository.UserTenantRoleRepository
import com.respiroc.util.context.PermissionContext
import com.respiroc.util.context.RoleContext
import com.respiroc.util.context.SpringUser
import com.respiroc.util.context.TenantContext
import com.respiroc.util.context.TenantPermissionContext
import com.respiroc.util.context.TenantRoleContext
import com.respiroc.util.context.UserContext
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.attribute.UserPrincipalNotFoundException
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val userSessionRepository: UserSessionRepository,
    private val userTenantRoleRepository: UserTenantRoleRepository,
    private val jwt: JwtUtils
) : UserInternalApi {

    private val random: SecureRandom = SecureRandom()
    private val base64Encoder: Base64.Encoder = Base64.getUrlEncoder()
    private val passwordEncoder = BCryptPasswordEncoder()

    private val JWT_TOKEN_PERIOD : Long = 24 * 60 * 60 * 1000

    override fun signupByEmailPassword(email: String, password: String) {
        val existUser = userRepository.findByEmail(email)
        if (existUser != null) {
            throw IllegalArgumentException("User already exists")
        }

        val newUser = User()
        newUser.email = email
        newUser.passwordHash = passwordEncoder.encode(password)
        signup(newUser)
    }

    override fun loginByEmailPassword(
        email: String,
        password: String
    ): LoginResult {
        val user = userRepository.findByEmail(email) ?: throw UsernameNotFoundException("Email not found")
        if (!passwordEncoder.matches(password, user.passwordHash)) throw BadCredentialsException("Login incorrect")

        return login(user)
    }

    override fun changePassword(
        user: SpringUser,
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

    override fun setPassword(user: SpringUser, newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun findByToken(token: String): UserContext? {
        return userRepository.findByToken(token, Instant.now())?.let { user ->
            if (jwt.isTokenValid(token = token, subject = user.email)) {
                user.toUserContext()
            } else {
                null
            }
        }
    }

    override fun findByEmail(email: String): UserContext? {
        TODO("Not yet implemented")
    }

    override fun generateToken(user: SpringUser): String {
        TODO("Not yet implemented")
    }

    override fun addUserTenantRole(
        tenant: Tenant,
        role: TenantRole,
        user: UserContext
    ) {
        val existUser = userRepository.findByEmail(user.email)  ?: throw UserPrincipalNotFoundException("User not found")
        val utr = UserTenantRole()
        utr.user = existUser
        utr.tenant = tenant
        utr.tenantRole = role
        userTenantRoleRepository.save(utr)
    }

    // ---------------------------------
    // Private Helper
    // ---------------------------------

    private fun signup(user: User) {
        val savedUser: User = userRepository.saveAndFlush(user)
    }

    private fun login(user: User) : LoginResult {
        val springUser = SpringUser(user.toUserContext())
        AccountStatusUserDetailsChecker().check(springUser)

        val token = jwt.generateToken(springUser.username, JWT_TOKEN_PERIOD)

        val userSession = UserSession()
        userSession.user = user
        userSession.token = token
        userSession.tokenIssueAt = Instant.now()
        userSession.tokenExpireAt = Instant.now().plus(30, ChronoUnit.DAYS)
        userSessionRepository.save(userSession)

        user.lastLoginAt = Instant.now()
        userRepository.save(user)

        return LoginResult(token, springUser)
    }

    private fun User.toUserContext(): UserContext {
        return UserContext(
            email = this.email,
            password = this.passwordHash,
            isEnabled = this.isEnabled,
            isLocked = this.isLocked,
            currentTenant = null, // Should be set on tenant filter process
            tenants = this.getTenantContexts(),
            roles = this.roles.map { it -> it.toRoleContext() }.toList()
        )
    }

    private fun User.getTenantContexts(): List<TenantContext> {
        return rolesPerTenant.map { (tenantId, tenantRoles) ->
            TenantContext(
                tenantId,
                tenantRoles.map { it.toTenantRoleContext() }
            )
        }
    }

    private fun TenantRole.toTenantRoleContext(): TenantRoleContext {
        return TenantRoleContext(
            name = this.name,
            code = this.code,
            description = this.description,
            permissions = this.tenantPermissions.map { it.toTenantPermissionContext() }.toList()
        )
    }

    private fun TenantPermission.toTenantPermissionContext(): TenantPermissionContext {
        return TenantPermissionContext(
            name = this.name,
            code = this.code,
            description = this.description
        )
    }

    private fun Role.toRoleContext(): RoleContext {
        return RoleContext(
            name = this.name,
            code = this.code,
            description = this.description,
            permissions = this.permissions.map { it.toPermissionContext() }.toList()
        )
    }

    private fun Permission.toPermissionContext(): PermissionContext {
        return PermissionContext(
            name = this.name,
            code = this.code,
            description = this.description
        )
    }
}