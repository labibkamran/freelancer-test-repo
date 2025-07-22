package com.respiroc.webapp.controller.web

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.response.Callout
import com.respiroc.webapp.domain.model.ExtractionStatus
import com.respiroc.webapp.repository.InvoiceAiExtractionRepository
import com.respiroc.webapp.service.AiVoucherCreationService
import com.respiroc.webapp.service.InvoiceData
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/htmx/voucher-reception")
class VoucherReceptionHtmxController(
    private val aiVoucherCreationService: AiVoucherCreationService,
    private val aiExtractionRepository: InvoiceAiExtractionRepository,
    private val voucherReceptionDocumentRepository: com.respiroc.webapp.VoucherReceptionDocumentRepository
) : BaseController() {
    
    private val logger = LoggerFactory.getLogger(VoucherReceptionHtmxController::class.java)
    private val objectMapper = jacksonObjectMapper().apply {
        configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    
    @GetMapping("/show-extraction")
    @HxRequest
    fun showExtraction(
        @RequestParam documentId: Long,
        model: Model
    ): String {
        logger.info("Showing AI extraction for document ID: $documentId")
        
        val aiExtraction = aiExtractionRepository.findByVoucherReceptionDocumentId(documentId)
        if (aiExtraction != null && aiExtraction.status == ExtractionStatus.COMPLETED) {
            // Parse the JSON structure that includes the "data" wrapper
            val jsonNode = objectMapper.readTree(aiExtraction.extractionData)
            val extractionData = if (jsonNode.has("data")) {
                // Extract the data field and parse it as InvoiceData
                objectMapper.treeToValue(jsonNode.get("data"), InvoiceData::class.java)
            } else {
                // Fallback: try to parse directly as InvoiceData
                objectMapper.readValue(aiExtraction.extractionData, InvoiceData::class.java)
            }
            
            model.addAttribute("extraction", extractionData)
            model.addAttribute("aiExtractionId", aiExtraction.id)
            return "fragments/ai-extraction-modal"
        }
        
        model.addAttribute("error", "AI extraction not found or not completed")
        return "fragments/error-message"
    }
    
    @PostMapping("/create-voucher")
    @HxRequest
    fun createVoucherFromAi(
        @RequestParam extractionId: Long,
        model: Model
    ): String {
        logger.info("Creating voucher from AI extraction ID: $extractionId")
        
        try {
            val voucher = aiVoucherCreationService.createVoucherFromAiExtraction(extractionId)
            
            // Return a success message with a link to view the voucher
            model.addAttribute("callout", Callout.Success(
                message = "Voucher created successfully: ${voucher.number}",
                link = "/voucher/${voucher.id}"
            ))
            
            return "fragments/callout-message"
        } catch (e: Exception) {
            logger.error("Failed to create voucher from AI extraction", e)
            model.addAttribute("callout", Callout.Error("Failed to create voucher: ${e.message}"))
            return "fragments/callout-message"
        }
    }
    
    @GetMapping("/refresh-table")
    @HxRequest
    fun refreshTable(model: Model): String {
        val documents = voucherReceptionDocumentRepository.findAllWithAiExtractions()
        model.addAttribute("documents", documents)
        return "voucher-reception/overview :: table"
    }
}
