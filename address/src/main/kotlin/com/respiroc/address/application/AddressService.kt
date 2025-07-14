package com.respiroc.address.application

import com.respiroc.address.application.payload.CreateAddressPayload
import com.respiroc.address.domain.model.Address
import com.respiroc.address.domain.repository.AddressRepository
import org.springframework.stereotype.Service

@Service
class AddressService(val addressRepository: AddressRepository) {
    fun getOrCreateAddress(payload: CreateAddressPayload): Address {
        return addressRepository.upsertAddress(createNormalizeAddress(payload))
    }

    private fun createNormalizeAddress(payload: CreateAddressPayload): Address {
        val address = Address()
        address.countryIsoCode = payload.countryIsoCode.uppercase()
        address.city = normalizeAddressField(payload.city)!!
        address.postalCode = removeSpaces(payload.postalCode)
        address.administrativeDivisionCode = removeSpaces(payload.administrativeDivisionCode)?.uppercase()
        address.primaryAddress = normalizeAddressField(payload.primaryAddress)!!
        address.secondaryAddress = normalizeAddressField(payload.secondaryAddress)
        return address
    }

    fun normalizeAddressField(input: String?): String? {
        if (input == null) return null
        val cleaned = removeSpaces(input, " ")!!
        if (cleaned.isEmpty()) return cleaned
        return cleaned
            .lowercase()
            .split(" ")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercaseChar() } }
    }

    fun removeSpaces(input: String?, replacement: String = ""): String? {
        return input
            ?.trim()
            ?.replace(Regex("\\s+"), replacement)
    }
}