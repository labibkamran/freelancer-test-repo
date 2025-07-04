package com.respiroc.ledger.domain.repository

import com.respiroc.ledger.domain.model.Voucher
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface VoucherRepository : CustomJpaRepository<Voucher, Long> {
    fun findByTenantIdOrderByDateDescNumberDesc(tenantId: Long): List<Voucher>
    
    fun findByIdAndTenantId(id: Long, tenantId: Long): Voucher?
    
    @Query("SELECT COALESCE(MAX(v.number), 0) FROM Voucher v WHERE v.tenantId = :tenantId AND EXTRACT(YEAR FROM v.date) = :year")
    fun findMaxVoucherNumberForYear(@Param("tenantId") tenantId: Long, @Param("year") year: Int): Short
}