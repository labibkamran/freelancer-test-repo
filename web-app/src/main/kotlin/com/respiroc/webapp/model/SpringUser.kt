package com.respiroc.webapp.model

import com.respiroc.util.dto.PermissionDTO
import com.respiroc.util.dto.RoleDTO
import com.respiroc.util.dto.TenantPermissionDTO
import com.respiroc.util.dto.TenantRoleDTO
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
    mapToGrantedAuthorities(user.roles, user.currentTenant?.roles)
) {
    companion object {

        private fun mapToGrantedAuthorities(
            roles: Collection<RoleDTO>,
            tenantRoles: Collection<TenantRoleDTO>?
        ): List<GrantedAuthority> {
            val authorities: MutableList<GrantedAuthority> = ArrayList()

            authorities.addAll(
                roles.stream()
                    .flatMap { role: RoleDTO -> role.permissions.stream() }
                    .map { permission: PermissionDTO -> SimpleGrantedAuthority(permission.code) }
                    .collect(Collectors.toList<GrantedAuthority>())
            )

            if (tenantRoles != null) {
                authorities.addAll(
                    tenantRoles.stream()
                        .flatMap { role: TenantRoleDTO -> role.permissions.stream() }
                        .map { permission: TenantPermissionDTO -> SimpleGrantedAuthority(permission.code) }
                        .collect(Collectors.toList<GrantedAuthority>())
                )
            }

            return authorities
        }
    }
}