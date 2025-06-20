package com.respiroc.tenant.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "tenants")
@EntityListeners(AuditingEntityListener::class)
open class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = -1

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    open lateinit var updatedAt: Instant
}