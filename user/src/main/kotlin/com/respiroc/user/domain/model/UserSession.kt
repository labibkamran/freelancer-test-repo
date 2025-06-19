package com.respiroc.user.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "user_sessions")
open class UserSession : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    open var id: Long = -1

    @Column(name = "user_record_id", nullable = false, updatable = false, insertable = false)
    open val userId: Long = -1

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_record_id", nullable = false)
    open lateinit var user: User

    @Column(name = "token", nullable = false, length = Integer.MAX_VALUE)
    open lateinit var token: String

    @Column(name = "token_issue_at", nullable = false)
    open lateinit var tokenIssueAt: Instant

    @Column(name = "token_expire_at", nullable = false)
    open lateinit var tokenExpireAt: Instant

    @Column(name = "token_revoked_at")
    open var tokenRevokedAt: Instant? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    open lateinit var updatedAt: Instant
}