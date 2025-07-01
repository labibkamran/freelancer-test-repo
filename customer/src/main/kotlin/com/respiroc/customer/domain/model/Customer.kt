package com.respiroc.customer.domain.model

import com.respiroc.tenant.domain.model.Tenant
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener::class)
open class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = -1

    @Column(name = "tenant_id", nullable = false, updatable = false, insertable = false)
    open var tenantId: Long = -1

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    open lateinit var tenant: Tenant

    @Size(max = 36)
    @Column(name = "organization_number", length = 36)
    open lateinit var organizationNumber: String

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    open lateinit var type: CustomerType

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    open lateinit var updatedAt: Instant
}