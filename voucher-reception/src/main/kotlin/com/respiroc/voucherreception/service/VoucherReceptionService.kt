package com.respiroc.voucherreception.service

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.voucherreception.api.VoucherReceptionInternalApi
import com.respiroc.voucherreception.api.command.CreateVoucherDocumentCommand
import com.respiroc.voucherreception.model.VoucherDocument
import com.respiroc.voucherreception.model.VoucherDocumentStatus
import com.respiroc.voucherreception.repository.VoucherDocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

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
        val allCompanies = companyInternalApi.findAllCompanies()
        val company = allCompanies.find { getSlugFromCompanyName(it.name) == companySlug }
            ?: throw IllegalArgumentException("Company not found with slug: $companySlug")
        
        // Set tenant context for this request
        TenantContextHolder.setTenantId(company.tenant.id!!)
        
        // Check file size limit (5MB)
        if (file.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("File size exceeds 5MB limit: ${file.size} bytes")
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
        val allCompanies = companyInternalApi.findAllCompanies()
        val company = allCompanies.find { getSlugFromCompanyName(it.name) == companySlug }
            ?: throw IllegalArgumentException("Company not found with slug: $companySlug")
        
        // Set tenant context for this request
        TenantContextHolder.setTenantId(company.tenant.id!!)
        
        // File size is already checked by the worker, but double-check here
        if (fileSize > 5 * 1024 * 1024) {
            throw IllegalArgumentException("File size exceeds 5MB limit: $fileSize bytes")
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
        
        val document = VoucherDocument(
            company = company,
            tenant = tenant,
            filename = command.filename,
            fileData = command.fileData,
            mimeType = command.mimeType,
            fileSize = command.fileSize,
            senderEmail = command.senderEmail,
            receivedAt = LocalDateTime.now(),
            status = VoucherDocumentStatus.PENDING
        )
        
        return voucherDocumentRepository.save(document)
    }
    
    override fun getDocumentsByCompany(companyId: Long, tenantId: Long): List<VoucherDocument> {
        return voucherDocumentRepository.findByCompanyIdAndTenantId(companyId, tenantId)
    }
    
    override fun getDocumentsByStatus(
        companyId: Long,
        tenantId: Long,
        status: VoucherDocumentStatus
    ): List<VoucherDocument> {
        return voucherDocumentRepository.findByCompanyIdAndTenantIdAndStatus(companyId, tenantId, status)
    }
    
    override fun getPendingDocumentsByTenant(tenantId: Long): List<VoucherDocument> {
        return voucherDocumentRepository.findByTenantIdAndStatus(tenantId, VoucherDocumentStatus.PENDING)
    }
    
    override fun getDocument(documentId: Long, tenantId: Long): VoucherDocument? {
        return voucherDocumentRepository.findByIdAndTenantId(documentId, tenantId)
    }
    
    override fun attachToVoucher(documentId: Long, voucherId: Long, tenantId: Long): VoucherDocument {
        val document = getDocument(documentId, tenantId)
            ?: throw IllegalArgumentException("Document not found: $documentId")
        
        val updatedDocument = document.copy(
            attachedVoucherId = voucherId,
            status = VoucherDocumentStatus.ATTACHED,
            updatedAt = LocalDateTime.now()
        )
        
        return voucherDocumentRepository.save(updatedDocument)
    }
    
    override fun deleteDocument(documentId: Long, tenantId: Long): Boolean {
        val document = getDocument(documentId, tenantId) ?: return false
        
        // Mark as deleted instead of hard delete
        val deletedDocument = document.copy(
            status = VoucherDocumentStatus.DELETED,
            updatedAt = LocalDateTime.now()
        )
        
        voucherDocumentRepository.save(deletedDocument)
        
        // File data is stored in database, no physical file to delete
        
        return true
    }
    
    override fun getCompanyEmailSlug(companyId: Long): String {
        val company = companyInternalApi.findCompanyById(companyId)
            ?: throw IllegalArgumentException("Company not found: $companyId")
        
        return getSlugFromCompanyName(company.name)
    }
    
    
    private fun getSlugFromCompanyName(name: String): String {
        return name.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("\\s+"), "-")
            .trim('-')
    }
}