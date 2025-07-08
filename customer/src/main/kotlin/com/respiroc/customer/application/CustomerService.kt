package com.respiroc.customer.application

import com.respiroc.customer.api.CustomerInternalApi
import com.respiroc.customer.api.payload.NewCustomerPayload
import com.respiroc.customer.domain.model.Customer
import com.respiroc.customer.domain.repository.CustomerRepository
import com.respiroc.customer.exception.CustomerExistException
import com.respiroc.customer.exception.CustomerNotFoundException
import com.respiroc.tenant.domain.model.Tenant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerService(
    private val customerRepository: CustomerRepository,
) : CustomerInternalApi {

    /**
     * Create a customer
     *
     * @throws CustomerExistException if the customer already exists.
     */
    override fun createNewCustomer(
        payload: NewCustomerPayload,
        tenantId: Long
    ): Customer {
        if (isExistByNameOrOrganizationNumber(payload.name, payload.organizationNumber))
            throw CustomerExistException("Customer already exists")
        val customer = Customer()
        val tenant = Tenant()
        tenant.id = tenantId
        customer.name = payload.name
        customer.organizationNumber = payload.organizationNumber
        customer.type = payload.type
        customer.tenantId = tenantId
        customer.tenant = tenant
        customer.privateCustomer = payload.privateCustomer
        return customerRepository.save(customer)
    }

    fun isExistByNameOrOrganizationNumber(name: String, organizationNumber: String?): Boolean {
        return if (organizationNumber != null)
            customerRepository.existsCustomersByNameContainingIgnoreCaseOrOrganizationNumberContainingIgnoreCase(
                name,
                organizationNumber
            )
        else
            customerRepository.existsCustomersByNameContainingIgnoreCase(name)
    }

    override fun findAllCustomerByTenantId(tenantId: Long): List<Customer> {
        return customerRepository.findCustomersByTenantId(tenantId)
    }

    override fun findByNameContainingAndTenantId(name: String, tenantId: Long): List<Customer> {
        return customerRepository.findCustomersByNameContainingIgnoreCaseAndTenantId(name, tenantId)
    }


    /**
     * Deletes a customer by ID and tenant ID.
     *
     * @throws CustomerNotFoundException if the customer does not exist.
     */
    override fun deleteByIdAndTenantId(id: Long, tenantId: Long) {
        val exists = customerRepository.existsByIdAndTenantId(id, tenantId)
        if (!exists)
            throw CustomerNotFoundException("Customer with id=$id and tenantId=$tenantId not found.")
        customerRepository.deleteById(id)
    }

}