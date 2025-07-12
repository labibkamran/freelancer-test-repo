package com.respiroc.company.domain.model

import com.respiroc.address.domain.model.Address
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "companies")
@EntityListeners(AuditingEntityListener::class)
open class Company : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = -1

    @Size(max = 36)
    @Column(name = "organization_number", nullable = false, length = 36)
    open lateinit var organizationNumber: String

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @Size(max = 2)
    @Column(name = "country_code", nullable = false, length = 2)
    open lateinit var countryCode: String

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    open lateinit var createdAt: Instant

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    open lateinit var updatedAt: Instant

    @Column(name = "address_id", nullable = true, updatable = false, insertable = false)
    open var addressId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = true)
    open var address: Address? = null

    @Transient
    open lateinit var currencyCode: String
}