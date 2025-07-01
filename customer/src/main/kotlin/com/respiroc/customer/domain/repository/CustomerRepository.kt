package com.respiroc.customer.domain.repository

import com.respiroc.customer.domain.model.Customer
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CustomJpaRepository<Customer, Long> {

    @Query(
        "SELECT customer FROM Customer customer \n" +
                "LEFT JOIN FETCH customer.tenant \n" +
                "WHERE customer.tenantId = :tenantId"
    )
    fun findAllByTenantId(@Param("tenantId") tenantId: Long): List<Customer>;

    @Query(
        "SELECT customer FROM Customer customer \n" +
                "LEFT JOIN FETCH customer.tenant \n" +
                "WHERE customer.tenantId = :tenantId \n" +
                "AND customer.name LIKE %:name%"
    )
    fun findByNameContainingAndTenantId(@Param("name") name: String, @Param("tenantId") tenantId: Long): List<Customer>;
}