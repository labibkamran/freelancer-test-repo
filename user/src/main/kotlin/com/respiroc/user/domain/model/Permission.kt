package com.respiroc.user.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "permissions")
open class Permission : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    open var id: Long = -1

    @Size(max = 128)
    @NotNull
    @Column(name = "name", nullable = false, length = 128)
    open lateinit var name: String

    @Size(max = 128)
    @NotNull
    @Column(name = "code", nullable = false, length = 128)
    open lateinit var code: String

    @Size(max = 255)
    @NotNull
    @Column(name = "description", nullable = false)
    open lateinit var description: String

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at")
    open lateinit var updatedAt: Instant

    @ManyToMany(targetEntity = Role::class, fetch = FetchType.LAZY, mappedBy = "permissions")
    open var roles: Set<Role>? = null
}