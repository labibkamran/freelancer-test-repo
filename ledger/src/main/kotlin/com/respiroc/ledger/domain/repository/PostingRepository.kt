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
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Posting p WHERE p.accountNumber = :accountNumber AND p.tenantId = :tenantId")
    fun getAccountBalance(@Param("accountNumber") accountNumber: String, @Param("tenantId") tenantId: Long): BigDecimal
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Posting p WHERE p.accountNumber = :accountNumber AND p.tenantId = :tenantId AND p.postingDate < :beforeDate")
    fun getAccountBalanceBeforeDate(@Param("accountNumber") accountNumber: String, @Param("tenantId") tenantId: Long, @Param("beforeDate") beforeDate: LocalDate): BigDecimal
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Posting p WHERE p.accountNumber = :accountNumber AND p.tenantId = :tenantId AND p.postingDate BETWEEN :startDate AND :endDate")
    fun getAccountMovementInPeriod(@Param("accountNumber") accountNumber: String, @Param("tenantId") tenantId: Long, @Param("startDate") startDate: LocalDate, @Param("endDate") endDate: LocalDate): BigDecimal
    
    @Query("SELECT DISTINCT p.accountNumber FROM Posting p WHERE p.tenantId = :tenantId ORDER BY p.accountNumber")
    fun findDistinctAccountNumbersByTenant(@Param("tenantId") tenantId: Long): List<String>
}