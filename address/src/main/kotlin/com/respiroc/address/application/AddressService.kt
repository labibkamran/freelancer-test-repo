package com.respiroc.address.application

import com.respiroc.address.application.payload.CreateAddressPayload
import com.respiroc.address.domain.model.Address
import com.respiroc.address.domain.repository.AddressRepository
import org.springframework.stereotype.Service

@Service
class AddressService(val addressRepository: AddressRepository) {
    fun getOrCreateAddress(payload: CreateAddressPayload): Address {
        val address =
            addressRepository
                .findAddressByCountryIsoCodeIgnoreCaseAndCityIgnoreCaseAndPostalCodeIgnoreCase(
                    countryIsoCode = payload.countryIsoCode,
                    city = payload.city,
                    postalCode = payload.postalCode
                )
        if (address != null) return address
        return createAddress(payload)
    }

    fun createAddress(payload: CreateAddressPayload): Address {
        var address = Address()
        address.countryIsoCode = payload.countryIsoCode
        address.city = payload.city
        address.postalCode = payload.postalCode
        address.administrativeDivisionCode = payload.administrativeDivisionCode
        address.primaryAddress = payload.primaryAddress
        address.secondaryAddress = payload.secondaryAddress
        address = addressRepository.save(address)
        return address
    }
}