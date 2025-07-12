package com.respiroc.address.domain.repository

import com.respiroc.address.domain.model.Address
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : CustomJpaRepository<Address, Long> {
    fun findAddressByCountryIsoCodeIgnoreCaseAndCityIgnoreCaseAndPostalCodeIgnoreCase(
        countryIsoCode: String,
        city: String,
        postalCode: String
    ): Address?
}