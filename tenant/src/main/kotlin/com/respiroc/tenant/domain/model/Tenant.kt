package com.respiroc.tenant.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "tenants")
open class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    open var recordId: Long = -1

    @Size(max = 36)
    @Column(name = "id", nullable = false, length = 36)
    open lateinit var id: String

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    open lateinit var updatedAt: Instant

    @Size(max = 50)
    @Column(name = "created_by", nullable = false, length = 50)
    open lateinit var createdBy: String

    @Size(max = 50)
    @Column(name = "updated_by", nullable = false, length = 50)
    open lateinit var updatedBy: String
}