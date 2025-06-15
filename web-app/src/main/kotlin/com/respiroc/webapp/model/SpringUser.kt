package com.respiroc.webapp.model

import com.respiroc.util.dto.Permission
import com.respiroc.util.dto.Role
import com.respiroc.util.dto.TenantPermission
import com.respiroc.util.dto.TenantRole
import com.respiroc.util.dto.UserContext
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.stream.Collectors

class SpringUser(val user: UserContext) : User(
    user.email,
    user.password,
    user.isEnabled,
    true,
    true,
    !user.isLocked,
    mapToGrantedAuthorities(user.roles, user.currentTenant.roles)
) {
    companion object {

        private fun mapToGrantedAuthorities(
            roles: Collection<Role>,
            tenantRoles: Collection<TenantRole>
        ): List<GrantedAuthority> {
            val authorities: MutableList<GrantedAuthority> = ArrayList()

            authorities.addAll(
                roles.stream()
                    .flatMap { role: Role -> role.permissions.stream() }
                    .map { permission: Permission -> SimpleGrantedAuthority(permission.code) }
                    .collect(Collectors.toList<GrantedAuthority>())
            )

            authorities.addAll(
                tenantRoles.stream()
                    .flatMap { role: TenantRole -> role.permissions.stream() }
                    .map { permission: TenantPermission -> SimpleGrantedAuthority(permission.code) }
                    .collect(Collectors.toList<GrantedAuthority>())
            )

            return authorities
        }
    }
}