package com.respiroc.user.domain.repository

import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.tenant.domain.model.TenantRole
import com.respiroc.user.domain.model.User
import com.respiroc.user.domain.model.UserTenantRole
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserTenantRoleRepository : CustomJpaRepository<UserTenantRole, Long> {

    @Query("SELECT utr.tenantRole FROM UserTenantRole utr WHERE utr.user = :user AND utr.tenant = :tenant")
    fun findTenantRolesByUserAndTenant(user: User, tenant: Tenant): List<TenantRole>
}