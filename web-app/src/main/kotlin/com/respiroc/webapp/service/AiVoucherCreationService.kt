package com.respiroc.webapp.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.respiroc.ledger.application.VoucherService
import com.respiroc.ledger.application.payload.CreatePostingPayload
import com.respiroc.ledger.application.payload.CreateVoucherPayload
import com.respiroc.ledger.application.payload.VoucherPayload
import com.respiroc.ledger.domain.repository.VoucherRepository
import com.respiroc.webapp.domain.model.InvoiceAiExtraction
import com.respiroc.webapp.repository.InvoiceAiExtractionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional
class AiVoucherCreationService(
    private val voucherService: VoucherService,
    private val voucherRepository: VoucherRepository,
    private val aiExtractionRepository: InvoiceAiExtractionRepository
) {
    private val logger = LoggerFactory.getLogger(AiVoucherCreationService::class.java)
    private val objectMapper = jacksonObjectMapper().apply {
        configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    
    fun createVoucherFromAiExtraction(aiExtractionId: Long): VoucherPayload {
        logger.info("=== Creating voucher from AI extraction ID: $aiExtractionId ===")
        
        val aiExtraction = aiExtractionRepository.findById(aiExtractionId)
            .orElseThrow { IllegalArgumentException("AI extraction not found with ID: $aiExtractionId") }
        
        logger.info("Found AI extraction with status: ${aiExtraction.status}")
        
        if (aiExtraction.status != com.respiroc.webapp.domain.model.ExtractionStatus.COMPLETED) {
            throw IllegalStateException("AI extraction is not completed. Current status: ${aiExtraction.status}")
        }
        
        // Get the tenant from the voucher reception document
        val tenant = aiExtraction.voucherReceptionDocument.tenant
        logger.info("Using tenant: ${tenant.slug} (ID: ${tenant.id})")
        
        logger.info("Parsing extraction data from JSON...")
        
        // Parse the JSON structure that includes the "data" wrapper
        val jsonNode = objectMapper.readTree(aiExtraction.extractionData)
        val extractionData = if (jsonNode.has("data")) {
            // Extract the data field and parse it as InvoiceData
            objectMapper.treeToValue(jsonNode.get("data"), InvoiceData::class.java)
        } else {
            // Fallback: try to parse directly as InvoiceData
            objectMapper.readValue(aiExtraction.extractionData, InvoiceData::class.java)
        }
        
        logger.info("Extraction data parsed successfully:")
        logger.info("  - Invoice number: ${extractionData.invoice_details.invoice_number}")
        logger.info("  - Company: ${extractionData.invoice_details.company_name}")
        logger.info("  - Amount: ${extractionData.invoice_details.order_total} ${extractionData.invoice_details.currency}")
        logger.info("  - Date: ${extractionData.invoice_details.invoice_date}")
        logger.info("  - Predicted account: ${extractionData.debit_prediction.account}")
        
        // Create voucher with AI-extracted data
        val voucherPayload = CreateVoucherPayload(
            date = LocalDate.parse(extractionData.invoice_details.invoice_date),
            description = extractionData.invoice_details.description,
            postings = createPostingsFromAiData(extractionData)
        )
        
        logger.info("Created voucher payload with ${voucherPayload.postings.size} postings")
        
        logger.info("Calling voucherService.createVoucher...")
        val voucher = voucherService.createVoucher(voucherPayload, tenant.id)
        logger.info("Voucher created with ID: ${voucher.id}, number: ${voucher.number}")
        
        // Link voucher to AI extraction by updating the voucher entity directly
        logger.info("Linking voucher to AI extraction...")
        val voucherEntity = voucherRepository.findById(voucher.id).orElse(null)
        if (voucherEntity != null) {
            voucherEntity.aiExtractionId = aiExtraction.id
            voucherRepository.save(voucherEntity)
            logger.info("✅ Voucher linked to AI extraction successfully")
        } else {
            logger.error("❌ Could not find voucher entity to link to AI extraction")
        }
        
        // Mark extraction as converted
        aiExtraction.status = com.respiroc.webapp.domain.model.ExtractionStatus.CONVERTED_TO_VOUCHER
        aiExtractionRepository.save(aiExtraction)
        logger.info("Updated AI extraction status to CONVERTED_TO_VOUCHER")
        
        logger.info("✅ Successfully created voucher ${voucher.number} from AI extraction")
        
        return voucher
    }
    
    private fun createPostingsFromAiData(extractionData: InvoiceData): List<CreatePostingPayload> {
        logger.info("=== Creating postings from AI data ===")
        val postings = mutableListOf<CreatePostingPayload>()
        
        val invoiceDate = LocalDate.parse(extractionData.invoice_details.invoice_date)
        val amount = BigDecimal.valueOf(extractionData.invoice_details.order_total)
        val currency = extractionData.invoice_details.currency
        val description = extractionData.invoice_details.description
        
        logger.info("Invoice details:")
        logger.info("  - Date: $invoiceDate")
        logger.info("  - Amount: $amount")
        logger.info("  - Currency: $currency")
        logger.info("  - Description: $description")
        logger.info("  - Predicted account: ${extractionData.debit_prediction.account}")
        logger.info("  - VAT code: ${extractionData.invoice_details.vat_code}")
        
        // 1. Debit posting for the expense account (AI predicted)
        val debitPosting = CreatePostingPayload(
            accountNumber = extractionData.debit_prediction.account,
            amount = amount,
            currency = currency,
            postingDate = invoiceDate,
            description = description,
            vatCode = extractionData.invoice_details.vat_code,
            rowNumber = 0
        )
        postings.add(debitPosting)
        logger.info("Created debit posting: ${debitPosting.accountNumber} - ${debitPosting.amount} ${debitPosting.currency}")
        
        // 2. Credit posting for accounts payable (typically 2400)
        val creditPosting = CreatePostingPayload(
            accountNumber = "2400", // Accounts Payable
            amount = amount.negate(),
            currency = currency,
            postingDate = invoiceDate,
            description = description,
            vatCode = null,
            rowNumber = 1
        )
        postings.add(creditPosting)
        logger.info("Created credit posting: ${creditPosting.accountNumber} - ${creditPosting.amount} ${creditPosting.currency}")
        
        logger.info("✅ Created ${postings.size} postings from AI data")
        
        return postings
    }
}
