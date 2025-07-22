package com.respiroc.webapp.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.respiroc.ledger.domain.model.Voucher
import com.respiroc.webapp.domain.model.InvoiceAiExtraction
import com.respiroc.webapp.repository.InvoiceAiExtractionRepository
import org.springframework.stereotype.Service

@Service
class AiExtractionDisplayService(
    private val aiExtractionRepository: InvoiceAiExtractionRepository
) {
    private val objectMapper = jacksonObjectMapper()
    
    fun getAiExtractionForVoucher(voucher: Voucher): InvoiceData? {
        return voucher.aiExtractionId?.let { aiExtractionId ->
            try {
                val aiExtraction = aiExtractionRepository.findById(aiExtractionId).orElse(null)
                aiExtraction?.let { extraction ->
                    // Parse the JSON structure that includes the "data" wrapper
                    val jsonNode = objectMapper.readTree(extraction.extractionData)
                    if (jsonNode.has("data")) {
                        objectMapper.treeToValue(jsonNode.get("data"), InvoiceData::class.java)
                    } else {
                        objectMapper.readValue(extraction.extractionData, InvoiceData::class.java)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun getAiExtractionById(aiExtractionId: Long): InvoiceData? {
        return aiExtractionRepository.findById(aiExtractionId).orElse(null)?.let { aiExtraction ->
            try {
                // Parse the JSON structure that includes the "data" wrapper
                val jsonNode = objectMapper.readTree(aiExtraction.extractionData)
                if (jsonNode.has("data")) {
                    objectMapper.treeToValue(jsonNode.get("data"), InvoiceData::class.java)
                } else {
                    objectMapper.readValue(aiExtraction.extractionData, InvoiceData::class.java)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun isVoucherAiGenerated(voucher: Voucher): Boolean {
        return voucher.aiExtractionId != null
    }
}
