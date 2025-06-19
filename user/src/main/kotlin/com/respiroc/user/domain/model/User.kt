package com.respiroc.user.domain.model

import com.respiroc.tenant.domain.model.TenantRole
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "users")
open class User : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    open var id: Long = -1

    @Size(max = 255)
    @Column(name = "email", nullable = false)
    open lateinit var email: String

    @Size(max = 255)
    @Column(name = "password_hash", nullable = false)
    open lateinit var passwordHash: String

    @ColumnDefault("true")
    @Column(name = "is_enabled")
    open var isEnabled: Boolean = true

    @ColumnDefault("false")
    @Column(name = "is_locked")
    open var isLocked: Boolean = false

    @Column(name = "last_login_at")
    open var lastLoginAt: Instant? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    open lateinit var updatedAt: Instant

    @ManyToMany(targetEntity = Role::class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_record_id")], inverseJoinColumns = [JoinColumn(name = "role_record_id")])
    open var roles: List<Role> = ArrayList()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var tenantRoles: MutableSet<UserTenantRole> = HashSet()

    @get:Transient
    val rolesPerTenant: Map<String, Set<TenantRole>>
        get() = tenantRoles.groupBy { it.tenant.id }
            .mapValues { entry -> entry.value.map { it.tenantRole }.toSet() }
}