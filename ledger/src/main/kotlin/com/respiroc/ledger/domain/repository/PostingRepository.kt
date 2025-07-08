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

    @Query("""
        SELECT p.accountNumber,
               COALESCE(SUM(CASE WHEN p.postingDate < :startDate THEN p.amount ELSE 0 END), 0) as openingBalance,
               COALESCE(SUM(CASE WHEN p.postingDate BETWEEN :startDate AND :endDate THEN p.amount ELSE 0 END), 0) as periodMovement,
               COUNT(CASE WHEN p.postingDate BETWEEN :startDate AND :endDate THEN 1 END) as transactionCount
        FROM Posting p 
        WHERE (:accountNumber IS NULL OR p.accountNumber = :accountNumber)
        AND p.tenantId = :tenantId 
        GROUP BY p.accountNumber
        HAVING COALESCE(SUM(CASE WHEN p.postingDate < :startDate THEN p.amount ELSE 0 END), 0) != 0
            OR COALESCE(SUM(CASE WHEN p.postingDate BETWEEN :startDate AND :endDate THEN p.amount ELSE 0 END), 0) != 0
        ORDER BY p.accountNumber
    """)
    fun getGeneralLedgerSummary(
        @Param("accountNumber") accountNumber: String?, 
        @Param("tenantId") tenantId: Long, 
        @Param("startDate") startDate: LocalDate, 
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    @Query("""
        SELECT p FROM Posting p 
        LEFT JOIN FETCH p.voucher v
        WHERE p.accountNumber = :accountNumber 
        AND p.tenantId = :tenantId 
        AND p.postingDate BETWEEN :startDate AND :endDate 
        ORDER BY p.postingDate, p.id
    """)
    fun findPostingsByAccountAndDateRange(
        @Param("accountNumber") accountNumber: String, 
        @Param("tenantId") tenantId: Long, 
        @Param("startDate") startDate: LocalDate, 
        @Param("endDate") endDate: LocalDate
    ): List<Posting>
}