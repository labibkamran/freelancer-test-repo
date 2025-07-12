package com.respiroc.address.api

import com.respiroc.address.api.payload.CreateAddressPayload
import com.respiroc.address.domain.model.Address

interface AddressInternalApi {
    fun getOrCreateAddress(payload: CreateAddressPayload): Address
}