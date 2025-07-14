package com.respiroc.address.domain.repository

import com.respiroc.address.domain.model.Address


interface AddressRepositoryExtension {
    fun upsertAddress(address: Address): Address
}
