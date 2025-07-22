package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.service.InvoiceExtractionService
import com.respiroc.webapp.service.InvoiceExtractionResult
import com.respiroc.webapp.VoucherReceptionService
import com.respiroc.tenant.application.TenantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Controller
@RequestMapping("/smart-receipt")
class SmartReceiptWebController(
    private val invoiceExtractionService: InvoiceExtractionService,
    private val voucherReceptionService: VoucherReceptionService,
    private val tenantService: TenantService
) : BaseController() {

    private val logger = LoggerFactory.getLogger(SmartReceiptWebController::class.java)

    @GetMapping("/upload")
    fun uploadInvoice(model: Model): String {
        addCommonAttributesForCurrentTenant(model, "Upload Invoice")
        return "smart-receipt/upload"
    }

    @PostMapping("/upload")
    fun handleFileUpload(
        @RequestParam("invoiceFile") file: MultipartFile,
        model: Model
    ): String {
        logger.info("=== File upload request received ===")
        logger.info("File name: ${file.originalFilename}")
        logger.info("File size: ${file.size} bytes")
        logger.info("Content type: ${file.contentType}")
        
        addCommonAttributesForCurrentTenant(model, "Upload Invoice")
        
        try {
            if (file.isEmpty) {
                logger.warn("Empty file uploaded")
                model.addAttribute("error", "Please select a file to upload")
                return "smart-receipt/upload"
            }
            
            val allowedExtensions = listOf(".pdf", ".jpg", ".jpeg", ".png")
            val fileName = file.originalFilename ?: ""
            val fileExtension = fileName.substringAfterLast('.', "").lowercase()
            
            logger.info("File extension: $fileExtension")
            
            if (!allowedExtensions.contains(".$fileExtension")) {
                logger.warn("Invalid file extension: $fileExtension")
                model.addAttribute("error", "Invalid file format. Please upload PDF, JPG, JPEG, or PNG files only.")
                return "smart-receipt/upload"
            }
            
            // Get current tenant
            val springUser = springUser()
            val tenantSlug = springUser.ctx.currentTenant?.tenantSlug
                ?: throw IllegalStateException("No current tenant found")
            
            val tenant = tenantService.findTenantBySlug(tenantSlug)
                ?: throw IllegalStateException("Tenant not found for slug: $tenantSlug")
            
            logger.info("Current tenant: ${tenant.slug}")
            
            // Save file to database using VoucherReceptionService
            logger.info("Saving file to database...")
            val savedDocument = voucherReceptionService.saveDocument(
                fileData = file.bytes,
                filename = fileName,
                mimeType = file.contentType ?: "application/octet-stream",
                senderEmail = "web-upload@reai.no", // Default email for web uploads
                tenant = tenant
            )
            
            logger.info("✅ File saved to database with document ID: ${savedDocument.id}")
            logger.info("✅ Document status: ${savedDocument.extractionStatus}")
            
            model.addAttribute("success", "File uploaded successfully! Document ID: ${savedDocument.id}")
            model.addAttribute("uploadedFile", fileName)
            model.addAttribute("documentId", savedDocument.id)
            
            // If it's a PDF, AI extraction will be triggered automatically
            if (fileExtension == "pdf") {
                model.addAttribute("message", "PDF file uploaded. AI extraction is processing in the background. Check the Voucher Reception page for status updates.")
            } else {
                model.addAttribute("message", "File uploaded successfully! (AI extraction available for PDF files only)")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Error processing file upload", e)
            model.addAttribute("error", "Error uploading file: ${e.message}")
        }
        
        logger.info("=== File upload request completed ===")
        return "smart-receipt/upload"
    }
} 