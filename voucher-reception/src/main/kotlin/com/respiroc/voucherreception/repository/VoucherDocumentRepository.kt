package com.respiroc.voucherreception.repository

import com.respiroc.util.repository.CustomJpaRepository
import com.respiroc.voucherreception.model.VoucherDocument
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
@Repository
interface VoucherDocumentRepository : CustomJpaRepository<VoucherDocument, Long> {
    
    fun findByCompanyIdAndTenantId(companyId: Long, tenantId: Long): List<VoucherDocument>
    
    fun findByIdAndTenantId(id: Long, tenantId: Long): VoucherDocument?
    
    fun findByTenantIdAndAttachedVoucherIdIsNull(tenantId: Long): List<VoucherDocument>
    
    fun findByAttachedVoucherId(voucherId: Long): List<VoucherDocument>
}