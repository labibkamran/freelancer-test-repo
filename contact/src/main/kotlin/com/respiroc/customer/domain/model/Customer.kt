package com.respiroc.customer.domain.model

import com.respiroc.company.domain.model.Company
import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.util.domain.person.PrivatePerson
import jakarta.persistence.*

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

    @Column(name = "company_id", nullable = true, updatable = false, insertable = false)
    open var companyId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    open var company: Company? = null

    @Column(name = "private_person_id", nullable = true, updatable = false, insertable = false)
    open var personId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "private_person_id", nullable = true)
    open var person: PrivatePerson? = null
}