package com.respiroc.tenant.domain.repository

import com.respiroc.tenant.domain.model.TenantPermission
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TenantPermissionRepository : CustomJpaRepository<TenantPermission, Long> {
}