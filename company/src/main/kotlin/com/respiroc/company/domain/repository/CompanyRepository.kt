package com.respiroc.company.domain.repository

import com.respiroc.company.domain.model.Company
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : CustomJpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE c.tenantId = :tenantId")
    fun findByTenantId(tenantId: Long): Company?

    @Query("SELECT c FROM Company c WHERE c.tenantId IN :tenantIds")
    fun findByTenantIdIn(tenantIds: List<Long>): List<Company>
}