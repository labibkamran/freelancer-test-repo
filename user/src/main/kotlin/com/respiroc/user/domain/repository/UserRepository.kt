package com.respiroc.user.domain.repository

import com.respiroc.user.domain.model.User
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CustomJpaRepository<User, Long> {
}