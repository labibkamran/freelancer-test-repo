package com.respiroc.customer.domain.repository

import com.respiroc.customer.domain.model.Customer
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CustomJpaRepository<Customer, Long> {

    fun findCustomersByTenantId(tenantId: Long): List<Customer>
    fun findCustomersByNameContainingIgnoreCaseAndTenantId(name: String, tenantId: Long): List<Customer>
    fun existsByIdAndTenantId(id: Long, tenantId: Long): Boolean
    fun existsCustomersByNameContainingIgnoreCaseOrOrganizationNumberContainingIgnoreCase(
        name: String,
        organizationNumber: String
    ): Boolean

    fun existsCustomersByNameContainingIgnoreCase(name: String): Boolean

}