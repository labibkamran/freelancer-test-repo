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
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
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
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.File
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

    private val logger = LoggerFactory.getLogger(VoucherReceptionService::class.java)

    fun saveDocument(
        fileData: ByteArray,
        filename: String,
        mimeType: String,
        senderEmail: String,
        tenant: Tenant
    ): VoucherReceptionDocument {
        logger.info("Saving document: $filename for tenant: ${tenant.slug}")

        // Create and save attachment
        val attachment = Attachment().apply {
            this.fileData = fileData
            this.filename = filename
            this.mimetype = mimeType
        }
        val savedAttachment = attachmentRepository.save(attachment)

        // Create voucher reception document
        val document = VoucherReceptionDocument(
            attachment = savedAttachment,
            senderEmail = senderEmail,
            tenant = tenant
        )

        val savedDocument = voucherDocumentRepository.save(document)
        logger.info("✅ Document saved with ID: ${savedDocument.id}")

        // Start AI extraction for PDF files
        if (mimeType == "application/pdf") {
            try {
                logger.info("Starting AI extraction for PDF: $filename")
                
                // Create temporary file for processing
                val tempFile = File.createTempFile("invoice_", ".pdf")
                tempFile.writeBytes(fileData)
                
                val extraction = invoiceExtractionService.extractInvoiceData(tempFile)
                
                val aiExtraction = InvoiceAiExtraction().apply {
                    voucherReceptionDocument = savedDocument
                    status = when (extraction) {
                        is InvoiceExtractionResult.Success -> ExtractionStatus.COMPLETED
                        is InvoiceExtractionResult.Error -> ExtractionStatus.FAILED
                    }
                    extractionData = jacksonObjectMapper().writeValueAsString(extraction)
                    extractionDate = Instant.now()
                    processingErrors = if (extraction is InvoiceExtractionResult.Error) {
                        extraction.message
                    } else null
                }
                
                val savedAiExtraction = aiExtractionRepository.save(aiExtraction)
                logger.info("✅ AI extraction completed for document: ${savedDocument.id}")
                
                // Try to auto-create voucher if extraction was successful
                if (aiExtraction.status == ExtractionStatus.COMPLETED) {
                    try {
                        aiVoucherCreationService.createVoucherFromAiExtraction(savedAiExtraction.id!!)
                        logger.info("✅ Auto-created voucher from AI extraction")
                    } catch (e: Exception) {
                        logger.warn("⚠️ Could not auto-create voucher: ${e.message}")
                    }
                }
                
                // Clean up temp file
                tempFile.delete()
                
            } catch (e: Exception) {
                logger.error("❌ AI extraction failed for document: ${savedDocument.id}", e)
                
                val failedExtraction = InvoiceAiExtraction().apply {
                    voucherReceptionDocument = savedDocument
                    status = ExtractionStatus.FAILED
                    extractionData = "{}"
                    extractionDate = Instant.now()
                    processingErrors = e.message
                }
                aiExtractionRepository.save(failedExtraction)
            }
        } else {
            logger.info("Skipping AI extraction for non-PDF file: $filename")
        }

        return savedDocument
    }

    fun getDocumentById(id: Long): VoucherReceptionDocument? {
        return voucherDocumentRepository.findById(id).orElse(null)
    }

    fun getAllDocuments(): List<VoucherReceptionDocument> {
        return voucherDocumentRepository.findAll()
    }
}

@Entity
@Table(name = "voucher_reception_documents")
class VoucherReceptionDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "attachment_id")
    val attachment: Attachment,

    @Column(name = "sender_email")
    val senderEmail: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    val tenant: Tenant,

    @CreationTimestamp
    @Column(name = "received_at")
    val receivedAt: Instant = Instant.now(),

    @OneToOne(mappedBy = "voucherReceptionDocument", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val aiExtraction: InvoiceAiExtraction? = null
) {
    // Default constructor for Hibernate
    constructor() : this(
        id = null,
        attachment = Attachment(),
        senderEmail = null,
        tenant = Tenant(),
        receivedAt = Instant.now(),
        aiExtraction = null
    )
}

@Repository
interface VoucherReceptionDocumentRepository : CustomJpaRepository<VoucherReceptionDocument, Long> {
    
    @Query("""
        SELECT vrd FROM VoucherReceptionDocument vrd 
        LEFT JOIN FETCH vrd.aiExtraction 
        ORDER BY vrd.receivedAt DESC
    """)
    fun findAllWithAiExtractions(): List<VoucherReceptionDocument>
}

@Controller
@RequestMapping("/voucher-reception")
class VoucherReceptionWebController(
    private val voucherReceptionDocumentRepository: VoucherReceptionDocumentRepository,
    private val voucherReceptionService: VoucherReceptionService,
    private val tenantService: TenantService
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
        @RequestParam("file") files: List<MultipartFile>,
        model: Model
    ): String {
        logger.info("=== Voucher Reception File Upload ===")
        logger.info("Number of files: ${files.size}")
        
        val springUser = springUser()
        val tenantSlug = springUser.ctx.currentTenant?.tenantSlug
            ?: throw IllegalStateException("No current tenant found")
        
        val tenant = tenantService.findTenantBySlug(tenantSlug)
            ?: throw IllegalStateException("Tenant not found for slug: $tenantSlug")
        
        try {
            if (files.isEmpty()) {
                model.addAttribute("error", "Please select files to upload")
                return overview(model)
            }
            
            val uploadedFiles = mutableListOf<String>()
            
            files.forEach { file ->
                if (!file.isEmpty) {
                    logger.info("Processing file: ${file.originalFilename}, size: ${file.size} bytes")
                    
                    // Save file to database
                    val savedDocument = voucherReceptionService.saveDocument(
                        fileData = file.bytes,
                        filename = file.originalFilename ?: "unknown",
                        mimeType = file.contentType ?: "application/octet-stream",
                        senderEmail = "web-upload@reai.no",
                        tenant = tenant
                    )
                    
                    uploadedFiles.add(file.originalFilename ?: "unknown")
                    logger.info("✅ File uploaded successfully with document ID: ${savedDocument.id}")
                }
            }
            
            if (uploadedFiles.isNotEmpty()) {
                model.addAttribute("success", "${uploadedFiles.size} file(s) uploaded successfully!")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Error uploading files", e)
            model.addAttribute("error", "Error uploading files: ${e.message}")
        }
        
        return overview(model)
    }

    @GetMapping("/htmx/voucher-reception/refresh-table")
    @HxRequest
    fun refreshTable(model: Model): String {
        val documents = voucherReceptionDocumentRepository.findAllWithAiExtractions()
        model.addAttribute("documents", documents)
        return "voucher-reception/overview :: table"
    }
}
