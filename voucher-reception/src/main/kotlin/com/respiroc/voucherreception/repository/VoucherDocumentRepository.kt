package com.respiroc.voucherreception.repository

import com.respiroc.util.repository.CustomJpaRepository
import com.respiroc.voucherreception.model.VoucherDocument
import com.respiroc.voucherreception.model.VoucherDocumentStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
@Repository
interface VoucherDocumentRepository : CustomJpaRepository<VoucherDocument, Long> {
    
    fun findByCompanyIdAndTenantId(companyId: Long, tenantId: Long): List<VoucherDocument>
    
    fun findByCompanyIdAndTenantIdAndStatus(
        companyId: Long, 
        tenantId: Long, 
        status: VoucherDocumentStatus
    ): List<VoucherDocument>
    
    fun findByIdAndTenantId(id: Long, tenantId: Long): VoucherDocument?
    
    @Query("SELECT vd FROM VoucherDocument vd WHERE vd.tenant.id = :tenantId AND vd.status = :status ORDER BY vd.receivedAt DESC")
    fun findByTenantIdAndStatus(
        @Param("tenantId") tenantId: Long,
        @Param("status") status: VoucherDocumentStatus
    ): List<VoucherDocument>
    
    fun findByAttachedVoucherId(voucherId: Long): List<VoucherDocument>
}