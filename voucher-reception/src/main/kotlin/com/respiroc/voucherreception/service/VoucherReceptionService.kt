package com.respiroc.voucherreception.service

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.voucherreception.api.VoucherReceptionInternalApi
import com.respiroc.voucherreception.api.command.CreateVoucherDocumentCommand
import com.respiroc.voucherreception.model.VoucherDocument
import com.respiroc.voucherreception.repository.VoucherDocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
@Transactional
class VoucherReceptionService(
    private val voucherDocumentRepository: VoucherDocumentRepository,
    private val companyInternalApi: CompanyInternalApi,
) : VoucherReceptionInternalApi {
    
    private val logger = LoggerFactory.getLogger(VoucherReceptionService::class.java)
    
    override fun receiveDocument(
        companySlug: String,
        file: MultipartFile,
        senderEmail: String?
    ): VoucherDocument {
        // Find company by slug (Worker calls don't have tenant context)
        val company = companyInternalApi.findCompanyBySlug(companySlug)
            ?: throw IllegalArgumentException("Company not found with slug: $companySlug")
        
        // Set tenant context for this request
        TenantContextHolder.setTenantId(company.tenant.id!!)
        
        // Check file size limit (25MB)
        if (file.size > 25 * 1024 * 1024) {
            throw IllegalArgumentException("File size exceeds 25MB limit: ${file.size} bytes")
        }
        
        // Create document record with file data
        val command = CreateVoucherDocumentCommand(
            companyId = company.id!!,
            tenantId = company.tenant.id!!,
            filename = file.originalFilename ?: "unnamed",
            fileData = file.bytes,
            mimeType = file.contentType,
            fileSize = file.size,
            senderEmail = senderEmail
        )
        
        return receiveDocumentCommand(command)
    }
    
    override fun receiveDocumentFromWorker(
        companySlug: String,
        filename: String,
        mimeType: String,
        fileSize: Long,
        fileData: ByteArray,
        senderEmail: String?
    ): VoucherDocument {
        // Find company by slug (Worker calls don't have tenant context)
        val company = companyInternalApi.findCompanyBySlug(companySlug)
            ?: throw IllegalArgumentException("Company not found with slug: $companySlug")
        
        // Set tenant context for this request
        TenantContextHolder.setTenantId(company.tenant.id!!)
        
        // File size is already checked by the worker, but double-check here
        if (fileSize > 25 * 1024 * 1024) {
            throw IllegalArgumentException("File size exceeds 25MB limit: $fileSize bytes")
        }
        
        // Create document record with file data
        val command = CreateVoucherDocumentCommand(
            companyId = company.id!!,
            tenantId = company.tenant.id!!,
            filename = filename,
            fileData = fileData,
            mimeType = mimeType,
            fileSize = fileSize,
            senderEmail = senderEmail
        )
        
        return receiveDocumentCommand(command)
    }
    
    override fun receiveDocumentCommand(command: CreateVoucherDocumentCommand): VoucherDocument {
        val company = companyInternalApi.findCompanyById(command.companyId)
            ?: throw IllegalArgumentException("Company not found: ${command.companyId}")
        
        val tenant = company.tenant
        
        val document = VoucherDocument()
        document.company = company
        document.tenant = tenant
        document.filename = command.filename
        document.fileData = command.fileData
        document.mimeType = command.mimeType
        document.fileSize = command.fileSize
        document.senderEmail = command.senderEmail
        document.receivedAt = Instant.now()
        
        return voucherDocumentRepository.save(document)
    }
    
    override fun getDocumentsByCompany(companyId: Long, tenantId: Long): List<VoucherDocument> {
        return voucherDocumentRepository.findByCompanyIdAndTenantId(companyId, tenantId)
    }
    
    
    override fun getPendingDocumentsByTenant(tenantId: Long): List<VoucherDocument> {
        return voucherDocumentRepository.findByTenantIdAndAttachedVoucherIdIsNull(tenantId)
    }
    
    override fun getDocument(documentId: Long, tenantId: Long): VoucherDocument? {
        return voucherDocumentRepository.findByIdAndTenantId(documentId, tenantId)
    }
    
    override fun attachToVoucher(documentId: Long, voucherId: Long, tenantId: Long): VoucherDocument {
        val document = getDocument(documentId, tenantId)
            ?: throw IllegalArgumentException("Document not found: $documentId")
        
        document.attachedVoucherId = voucherId
        
        return voucherDocumentRepository.save(document)
    }
    
    override fun deleteDocument(documentId: Long, tenantId: Long): Boolean {
        val document = getDocument(documentId, tenantId) ?: return false
        
        // Actually delete the document
        voucherDocumentRepository.delete(document)
        
        return true
    }
    
    override fun getCompanyEmailSlug(companyId: Long): String {
        val company = companyInternalApi.findCompanyById(companyId)
            ?: throw IllegalArgumentException("Company not found: $companyId")
        
        return company.name.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "-")
            .trim('-')
    }
}