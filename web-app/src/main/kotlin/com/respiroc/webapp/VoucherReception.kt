package com.respiroc.webapp

import com.respiroc.tenant.application.TenantService
import com.respiroc.tenant.domain.model.Tenant
import com.respiroc.util.repository.CustomJpaRepository
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.domain.model.InvoiceAiExtraction
import com.respiroc.webapp.domain.model.ExtractionStatus
import com.respiroc.webapp.repository.InvoiceAiExtractionRepository
import com.respiroc.webapp.service.InvoiceExtractionService
import com.respiroc.webapp.service.InvoiceExtractionResult
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.Query
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

@RestController
class VoucherReceptionController(
    private val voucherReceptionService: VoucherReceptionService,
    private val tenantService: TenantService
) {

    private val logger = LoggerFactory.getLogger(VoucherReceptionController::class.java)

    data class EmailDocumentRequest(
        val filename: String,
        val mimeType: String,
        val fileData: String, // base64 encoded
        val senderEmail: String
    )

    // Cloudflare worker in index.js calls this
    @PostMapping("/api/voucher-reception")
    fun receiveDocumentFromEmail(
        @RequestHeader("X-Tenant-Slug") tenantSlug: String,
        @RequestBody request: EmailDocumentRequest
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Receiving Voucher Receipt for $tenantSlug")
        val tenant = tenantService.findTenantBySlug(tenantSlug)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Company not found"))

        val fileData = try {
            Base64.getDecoder().decode(request.fileData)
        } catch (_: Exception) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid base64 data"))
        }

        val saved = voucherReceptionService.saveDocument(
            fileData = fileData,
            filename = request.filename,
            mimeType = request.mimeType,
            senderEmail = request.senderEmail,
            tenant = tenant
        )

        return ResponseEntity.ok(
            mapOf(
                "id" to (saved.id ?: 0),
                "filename" to saved.attachment.filename,
                "status" to "received"
            )
        )
    }
}

@Service
@Transactional
class VoucherReceptionService(
    private val voucherDocumentRepository: VoucherReceptionDocumentRepository,
    private val attachmentRepository: AttachmentRepository,
    private val attachmentService: AttachmentService,
    private val invoiceExtractionService: InvoiceExtractionService,
    private val aiExtractionRepository: InvoiceAiExtractionRepository,
    private val aiVoucherCreationService: com.respiroc.webapp.service.AiVoucherCreationService
) {

    fun saveDocument(
        fileData: ByteArray,
        filename: String,
        mimeType: String,
        senderEmail: String,
        tenant: Tenant
    ): VoucherReceptionDocument {
        logger.info("=== Saving document to database ===")
        logger.info("Filename: $filename")
        logger.info("MIME type: $mimeType")
        logger.info("File size: ${fileData.size} bytes")
        logger.info("Sender email: $senderEmail")
        logger.info("Tenant: ${tenant.slug}")

        val (pdfBytes, pdfName, pdfMime) =
            attachmentService.convertToPdf(fileData, filename, mimeType)
        
        logger.info("Converted to PDF: $pdfName (${pdfBytes.size} bytes)")

        val attachment = Attachment().apply {
            this.fileData = pdfBytes
            this.filename = pdfName
            this.mimetype = pdfMime
        }
        logger.info("Saving attachment to database...")
        val savedAttachment = attachmentRepository.save(attachment)
        logger.info("✅ Attachment saved with ID: ${savedAttachment.id}")

        val document = VoucherReceptionDocument().apply {
            this.attachment = savedAttachment
            this.senderEmail = senderEmail
            this.tenant = tenant
        }
        logger.info("Saving voucher reception document to database...")
        val savedDocument = voucherDocumentRepository.save(document)
        logger.info("✅ Document saved with ID: ${savedDocument.id}")
        
        // Trigger AI extraction for PDFs
        if (pdfMime == "application/pdf") {
            logger.info("Triggering AI extraction for PDF document...")
            triggerAiExtraction(savedDocument)
        } else {
            logger.info("Skipping AI extraction for non-PDF document")
        }
        
        return savedDocument
    }
    
    private fun triggerAiExtraction(document: VoucherReceptionDocument) {
        // Async processing to avoid blocking the upload
        Thread {
            try {
                logger.info("=== Starting AI extraction for document ID: ${document.id} ===")
                logger.info("Document filename: ${document.attachment.filename}")
                logger.info("Document size: ${document.attachment.fileData.size} bytes")

                // Create temporary file for processing
                val tempFile = java.io.File.createTempFile("invoice_", ".pdf")
                tempFile.writeBytes(document.attachment.fileData)
                logger.info("Created temp file: ${tempFile.absolutePath}")

                logger.info("Calling invoiceExtractionService.extractInvoiceData...")
                val extractionResult = invoiceExtractionService.extractInvoiceData(tempFile)
                logger.info("AI extraction result: $extractionResult")

                val aiExtraction = InvoiceAiExtraction().apply {
                    voucherReceptionDocument = document
                    extractionData = jacksonObjectMapper().writeValueAsString(extractionResult)
                    status = when (extractionResult) {
                        is InvoiceExtractionResult.Success -> ExtractionStatus.COMPLETED
                        is InvoiceExtractionResult.Error -> ExtractionStatus.FAILED
                    }
                    extractionDate = Instant.now()
                    processingErrors = if (extractionResult is InvoiceExtractionResult.Error) {
                        extractionResult.message
                    } else null
                }

                logger.info("Saving AI extraction to database...")
                val savedAiExtraction = aiExtractionRepository.save(aiExtraction)
                logger.info("AI extraction saved with ID: ${savedAiExtraction.id}")

                // Update document status
                document.extractionStatus = aiExtraction.status
                document.extractionDate = aiExtraction.extractionDate
                document.processingErrors = aiExtraction.processingErrors
                val savedDocument = voucherDocumentRepository.save(document)
                logger.info("Document status updated: ${savedDocument.extractionStatus}")

                logger.info("AI extraction completed for document ID: ${document.id} with status: ${aiExtraction.status}")

                // Optionally auto-create voucher if extraction was successful
                if (aiExtraction.status == ExtractionStatus.COMPLETED) {
                    logger.info("=== Starting auto-voucher creation ===")
                    try {
                        // Auto-create voucher
                        val voucher = aiVoucherCreationService.createVoucherFromAiExtraction(savedAiExtraction.id!!)
                        logger.info("✅ Auto-created voucher ${voucher.number} from AI extraction for document ID: ${document.id}")
                        
                        // Update the extraction status to show it was converted
                        aiExtraction.status = ExtractionStatus.CONVERTED_TO_VOUCHER
                        aiExtractionRepository.save(aiExtraction)
                        logger.info("Updated AI extraction status to CONVERTED_TO_VOUCHER")
                        
                        // Update document status
                        document.extractionStatus = aiExtraction.status
                        voucherDocumentRepository.save(document)
                        logger.info("Updated document status to CONVERTED_TO_VOUCHER")
                        
                    } catch (e: Exception) {
                        logger.error("❌ Failed to auto-create voucher for document ID: ${document.id}", e)
                        logger.error("Exception details: ${e.message}")
                        e.printStackTrace()
                        // Don't fail the entire process if auto-creation fails
                    }
                } else {
                    logger.warn("AI extraction was not successful, skipping voucher creation. Status: ${aiExtraction.status}")
                }

                // Clean up temp file
                tempFile.delete()
                logger.info("Cleaned up temp file")

            } catch (e: Exception) {
                logger.error("❌ Error during AI extraction for document ID: ${document.id}", e)
                logger.error("Exception details: ${e.message}")
                e.printStackTrace()

                // Save error state
                val aiExtraction = InvoiceAiExtraction().apply {
                    voucherReceptionDocument = document
                    extractionData = "{}"
                    status = ExtractionStatus.FAILED
                    extractionDate = Instant.now()
                    processingErrors = e.message
                }

                aiExtractionRepository.save(aiExtraction)

                // Update document status
                document.extractionStatus = aiExtraction.status
                document.extractionDate = aiExtraction.extractionDate
                document.processingErrors = aiExtraction.processingErrors
                voucherDocumentRepository.save(document)
            }
        }.start()
    }
    
    companion object {
        private val logger = LoggerFactory.getLogger(VoucherReceptionService::class.java)
    }
}

@Repository
interface VoucherReceptionDocumentRepository : CustomJpaRepository<VoucherReceptionDocument, Long> {
    @Query("SELECT vrd FROM VoucherReceptionDocument vrd LEFT JOIN FETCH vrd.aiExtraction ORDER BY vrd.receivedAt DESC")
    fun findAllWithAiExtractions(): List<VoucherReceptionDocument>
}

@Entity
@Table(name = "voucher_reception_documents")
class VoucherReceptionDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    lateinit var attachment: Attachment

    @CreationTimestamp
    @Column(name = "received_at", nullable = false)
    var receivedAt: Instant? = null

    @Column(name = "sender_email")
    var senderEmail: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    lateinit var tenant: Tenant
    
    // AI Extraction fields
    @Column(name = "ai_extraction_data")
    var aiExtractionData: String? = null
    
    @Enumerated(EnumType.STRING)
    @Column(name = "extraction_status")
    var extractionStatus: ExtractionStatus? = null
    
    @Column(name = "extraction_date")
    var extractionDate: Instant? = null
    
    @Column(name = "processing_errors")
    var processingErrors: String? = null
    
    // Relationship to AI extraction
    @OneToOne(mappedBy = "voucherReceptionDocument", fetch = FetchType.LAZY)
    var aiExtraction: InvoiceAiExtraction? = null
}

@Controller
@RequestMapping("/voucher-reception")
class VoucherReceptionWebController(
    private val voucherReceptionDocumentRepository: VoucherReceptionDocumentRepository,
    private val voucherReceptionService: VoucherReceptionService,
    private val tenantService: com.respiroc.tenant.application.TenantService
) : BaseController() {

    private val logger = LoggerFactory.getLogger(VoucherReceptionWebController::class.java)

    @GetMapping(value = ["", "/"])
    fun overview(model: Model): String {
        val springUser = springUser()
        val documents = voucherReceptionDocumentRepository.findAllWithAiExtractions()
        addCommonAttributesForCurrentTenant(model, "Voucher Reception")
        model.addAttribute("documents", documents)
        model.addAttribute("tenantSlug", springUser.ctx.currentTenant?.tenantSlug)
        return "voucher-reception/overview"
    }

    @PostMapping("/upload")
    fun handleFileUpload(
        @RequestParam("file") file: org.springframework.web.multipart.MultipartFile,
        model: Model
    ): String {
        logger.info("=== Voucher Reception File Upload ===")
        logger.info("File name: ${file.originalFilename}")
        logger.info("File size: ${file.size} bytes")
        
        val springUser = springUser()
        val tenantSlug = springUser.ctx.currentTenant?.tenantSlug
            ?: throw IllegalStateException("No current tenant found")
        
        val tenant = tenantService.findTenantBySlug(tenantSlug)
            ?: throw IllegalStateException("Tenant not found for slug: $tenantSlug")
        
        try {
            if (file.isEmpty) {
                model.addAttribute("error", "Please select a file to upload")
                return overview(model)
            }
            
            // Save file to database
            val savedDocument = voucherReceptionService.saveDocument(
                fileData = file.bytes,
                filename = file.originalFilename ?: "unknown",
                mimeType = file.contentType ?: "application/octet-stream",
                senderEmail = "web-upload@reai.no",
                tenant = tenant
            )
            
            logger.info("✅ File uploaded successfully with document ID: ${savedDocument.id}")
            model.addAttribute("success", "File uploaded successfully! Document ID: ${savedDocument.id}")
            
        } catch (e: Exception) {
            logger.error("❌ Error uploading file", e)
            model.addAttribute("error", "Error uploading file: ${e.message}")
        }
        
        return overview(model)
    }
}
