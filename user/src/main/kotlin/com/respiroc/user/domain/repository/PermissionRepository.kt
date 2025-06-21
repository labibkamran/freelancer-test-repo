package com.respiroc.user.domain.repository

import com.respiroc.user.domain.model.Permission
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository: CustomJpaRepository<Permission, Long> {
}