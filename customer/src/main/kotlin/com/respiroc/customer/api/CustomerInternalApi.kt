package com.respiroc.customer.api

import com.respiroc.customer.api.payload.NewCustomerPayload
import com.respiroc.customer.domain.model.Customer

interface CustomerInternalApi {
    fun createNewCustomer(newCustomerPayload: NewCustomerPayload, tenantId: Long): Customer
    fun findAllCustomerByTenantId(tenantId: Long): List<Customer>
    fun findByNameContainingAndTenantId(name: String, tenantId: Long): List<Customer>
    fun deleteByIdAndTenantId(id: Long, tenantId: Long)
}