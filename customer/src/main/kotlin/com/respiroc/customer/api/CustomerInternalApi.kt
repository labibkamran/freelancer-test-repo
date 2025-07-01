package com.respiroc.customer.api

import com.respiroc.customer.api.command.CreateCustomerCommand
import com.respiroc.customer.domain.model.Customer
import com.respiroc.util.context.UserContext

interface CustomerInternalApi {
    fun createNewCustomer(createCustomerCommand: CreateCustomerCommand, user: UserContext): Customer
    fun editCustomer(id: Long, createCustomerCommand: CreateCustomerCommand, user: UserContext): Customer
    fun findAllCustomerByTenantId(tenantId: Long): List<Customer>;
    fun findByNameContainingAndTenantId(name: String, tenantId: Long): List<Customer>;
    fun getCustomerById();
}