package com.respiroc.customer.application

import com.respiroc.customer.api.CustomerInternalApi
import com.respiroc.customer.api.command.CreateCustomerCommand
import com.respiroc.customer.domain.model.Customer
import com.respiroc.customer.domain.repository.CustomerRepository
import com.respiroc.util.context.UserContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerService(
    private val customerRepository: CustomerRepository,
) : CustomerInternalApi {
    override fun createNewCustomer(
        createCustomerCommand: CreateCustomerCommand,
        user: UserContext
    ): Customer {
        val customer = Customer()
        customer.name = createCustomerCommand.name
        customer.organizationNumber = createCustomerCommand.organizationNumber
        customer.type = createCustomerCommand.type
        customer.tenantId = user.currentTenant!!.id
        return customerRepository.save(customer)
    }

    override fun editCustomer(
        id: Long,
        createCustomerCommand: CreateCustomerCommand,
        user: UserContext
    ): Customer {
        TODO("Not yet implemented")
    }

    override fun findAllCustomerByTenantId(tenantId: Long): List<Customer> {
        return customerRepository.findAllByTenantId(tenantId)
    }

    override fun findByNameContainingAndTenantId(name: String, tenantId: Long): List<Customer> {
        return customerRepository.findByNameContainingAndTenantId(name, tenantId)
    }

    override fun getCustomerById() {
        TODO("Not yet implemented")
    }
}