package com.respiroc.ledger.domain.repository

import com.respiroc.ledger.domain.model.Posting
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
interface PostingRepository : CustomJpaRepository<Posting, Long> {
    
    fun findByTenantIdOrderByPostingDateDescCreatedAtDesc(tenantId: Long): List<Posting>
    
    fun findByAccountNumberAndTenantIdOrderByPostingDateDescCreatedAtDesc(
        accountNumber: String, 
        tenantId: Long
    ): List<Posting>
    
    fun findByPostingDateBetweenAndTenantIdOrderByPostingDateDescCreatedAtDesc(
        startDate: LocalDate,
        endDate: LocalDate,
        tenantId: Long
    ): List<Posting>
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Posting p WHERE p.accountNumber = :accountNumber AND p.tenantId = :tenantId")
    fun getAccountBalance(@Param("accountNumber") accountNumber: String, @Param("tenantId") tenantId: Long): BigDecimal
}