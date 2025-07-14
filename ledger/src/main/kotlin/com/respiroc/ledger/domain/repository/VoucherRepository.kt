package com.respiroc.ledger.domain.repository

import com.respiroc.ledger.domain.model.Voucher
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VoucherRepository : CustomJpaRepository<Voucher, Long> {
    @Query("SELECT v FROM Voucher v WHERE v.tenantId = :tenantId ORDER BY v.date DESC, v.number DESC")
    fun findVoucherSummariesByTenantId(@Param("tenantId") tenantId: Long): List<Voucher>

    @Query("SELECT v FROM Voucher v LEFT JOIN FETCH v.postings WHERE v.id = :id AND v.tenantId = :tenantId")
    fun findByIdAndTenantIdWithPostings(@Param("id") id: Long, @Param("tenantId") tenantId: Long): Voucher?

    @Query("SELECT COALESCE(MAX(v.number), 0) FROM Voucher v WHERE v.tenantId = :tenantId AND EXTRACT(YEAR FROM v.date) = :year")
    fun findMaxVoucherNumberForYear(@Param("tenantId") tenantId: Long, @Param("year") year: Int): Short

    @Query(
        """
        SELECT v FROM Voucher v LEFT JOIN FETCH v.postings p WHERE v.tenantId = :tenantId AND
        (p.id IS NULL OR NOT EXISTS 
            (SELECT 1 FROM Posting post WHERE post.voucherId = v.id)
        ) ORDER BY v.createdAt DESC LIMIT 1
    """
    )
    fun findFirstEmptyVoucherByTenantId(@Param("tenantId") tenantId: Long): Voucher?
}