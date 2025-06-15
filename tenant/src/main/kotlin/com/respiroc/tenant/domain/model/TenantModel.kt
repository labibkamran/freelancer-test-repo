package com.respiroc.tenant.domain.model

import jakarta.persistence.*

@Entity
@Table(name = "tenants")
class TenantModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    val id: Long? = null,

    @Column(name = "id", nullable = false, unique = true, length = 36)
    val tenantIdentifier: String,

    @Column(nullable = false, length = 255)
    var name: String
) {
    // convenience toString, equals, hashCode
    override fun toString(): String = "Tenant(id=$id, tenantIdentifier=$tenantIdentifier, name=$name)"
} 