package com.respiroc.address.domain.repository

import com.respiroc.address.domain.model.Address
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Query
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
open class AddressRepositoryExtensionImpl : AddressRepositoryExtension {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private fun setAddressParameters(query: Query, address: Address) {
        query.setParameter("countryIsoCode", address.countryIsoCode)
        query.setParameter("adminDivisionCode", address.administrativeDivisionCode)
        query.setParameter("city", address.city)
        query.setParameter("postalCode", address.postalCode)
        query.setParameter("streetAddress1", address.primaryAddress)
        query.setParameter("streetAddress2", address.secondaryAddress)
    }

    @Transactional
    override fun upsertAddress(address: Address): Address {
        val query = entityManager.createNativeQuery(
            """
            INSERT INTO addresses (
                country_iso_code,
                administrative_division_code,
                city,
                postal_code,
                street_address_1,
                street_address_2
            )
            VALUES (
                :countryIsoCode,
                :adminDivisionCode,
                :city,
                :postalCode,
                :streetAddress1,
                :streetAddress2
            )
            ON CONFLICT (country_iso_code, city, postal_code)
            DO UPDATE SET 
                administrative_division_code = EXCLUDED.administrative_division_code
            RETURNING *;
            """.trimIndent(),
            Address::class.java
        )

        setAddressParameters(query, address)
        return query.singleResult as Address
    }
}