package com.respiroc.customer.domain.model

import com.respiroc.tenant.domain.model.Tenant
import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
@Table(name = "customers")
open class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = -1

    @Column(name = "tenant_id", nullable = false, updatable = false, insertable = false)
    open var tenantId: Long = -1

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    open lateinit var tenant: Tenant

    @Size(max = 36)
    @Column(name = "organization_number", length = 36)
    open var organizationNumber: String? = null

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    open lateinit var type: CustomerType

    @Column(name = "private_customer", nullable = false)
    open var privateCustomer: Boolean = false
}