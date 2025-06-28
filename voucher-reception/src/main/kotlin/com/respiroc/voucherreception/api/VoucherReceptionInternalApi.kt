package com.respiroc.voucherreception.api

import com.respiroc.voucherreception.api.command.CreateVoucherDocumentCommand
import com.respiroc.voucherreception.model.VoucherDocument
import org.springframework.web.multipart.MultipartFile
interface VoucherReceptionInternalApi {
    
    fun receiveDocument(
        companySlug: String,
        file: MultipartFile,
        senderEmail: String? = null
    ): VoucherDocument
    
    fun receiveDocumentFromWorker(
        companySlug: String,
        filename: String,
        mimeType: String,
        fileSize: Long,
        fileData: ByteArray,
        senderEmail: String? = null
    ): VoucherDocument
    
    fun receiveDocumentCommand(command: CreateVoucherDocumentCommand): VoucherDocument
    
    fun getDocumentsByCompany(companyId: Long, tenantId: Long): List<VoucherDocument>
    
    fun getPendingDocumentsByTenant(tenantId: Long): List<VoucherDocument>
    
    fun getDocument(documentId: Long, tenantId: Long): VoucherDocument?
    
    fun attachToVoucher(documentId: Long, voucherId: Long, tenantId: Long): VoucherDocument
    
    fun deleteDocument(documentId: Long, tenantId: Long): Boolean
    
    fun getCompanyEmailSlug(companyId: Long): String
}