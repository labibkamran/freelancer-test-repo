package com.respiroc.user.domain.repository

import com.respiroc.user.domain.model.User
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UserRepository : CustomJpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u, UserSession us WHERE u.id = us.userId AND us.token = :token AND us.tokenExpireAt > :time AND us.tokenRevokedAt is null")
    fun findByToken(@Param("token") token: String, @Param("time") time: Instant): User?
}