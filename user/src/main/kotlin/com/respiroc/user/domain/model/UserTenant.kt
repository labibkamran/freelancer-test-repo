package com.respiroc.user.domain.model

import com.respiroc.tenant.domain.model.Tenant
import jakarta.persistence.*


@Entity
@Table(name = "user_tenants")
class UserTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private val id: Long? = null

    @Column(name = "user_id", nullable = false, updatable = false, insertable = false)
    open var userId: Long = -1

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    var user: User? = null

    @Column(name = "tenant_id", nullable = false, updatable = false, insertable = false)
    open var tenantId: Long = -1

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id")
    var tenant: Tenant? = null

    @OneToMany(mappedBy = "userTenant", orphanRemoval = true, fetch = FetchType.LAZY)
    val roles: MutableSet<UserTenantRole> = HashSet()
}