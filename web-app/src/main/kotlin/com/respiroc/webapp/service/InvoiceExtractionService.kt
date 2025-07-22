package com.respiroc.webapp.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.respiroc.ledger.application.AccountService
import com.respiroc.ledger.application.VatService
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.ExchangeStrategies
import reactor.core.publisher.Mono
import java.io.File
import java.io.IOException

@Service
class InvoiceExtractionService(
    @Value("\${OPENAI_API_KEY:}") private val openaiApiKey: String,
    private val vatService: VatService,
    private val accountService: AccountService
) {
    private val logger = LoggerFactory.getLogger(InvoiceExtractionService::class.java)
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    
    private val webClient = WebClient.builder()
        .exchangeStrategies(ExchangeStrategies.builder()
            .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
            .build())
        .build()

    fun extractInvoiceData(file: File): InvoiceExtractionResult {
        logger.info("=== Starting invoice extraction for file: ${file.name} ===")
        
        val extractedText = extractTextFromPdf(file)
        if (extractedText.isBlank()) {
            logger.error("No text could be extracted from the PDF")
            return InvoiceExtractionResult.Error("No text could be extracted from the PDF")
        }
        
        logger.info("Extracted text length: ${extractedText.length} characters")
        logger.info("Extracted text preview: ${extractedText.take(200)}...")
        
        val openaiResult = sendToOpenAI(extractedText)
        if (openaiResult == null) {
            logger.error("Failed to process with OpenAI API")
            return InvoiceExtractionResult.Error("Failed to process with OpenAI API")
        }
        
        logger.info("OpenAI processing completed successfully")
        logger.info("Raw OpenAI response: $openaiResult")
        
        return try {
            val cleanedJson = cleanOpenAIResponse(openaiResult)
            logger.info("Cleaned JSON: $cleanedJson")
            val invoiceData = objectMapper.readValue(cleanedJson, InvoiceData::class.java)
            
            // Validate and potentially correct categorization
            val validatedData = validateAndCorrectCategorization(invoiceData)
            
            logger.info("Successfully parsed and validated InvoiceData: $validatedData")
            InvoiceExtractionResult.Success(validatedData)
        } catch (e: Exception) {
            logger.error("Failed to parse OpenAI response as JSON", e)
            InvoiceExtractionResult.Error("Failed to parse OpenAI response: ${e.message}")
        }
    }

    private fun extractTextFromPdf(file: File): String {
        return try {
            val document = PDDocument.load(file)
            try {
                val stripper = PDFTextStripper()
                val text = stripper.getText(document)
                text.trim()
            } finally {
                document.close()
            }
        } catch (e: IOException) {
            logger.error("Error extracting text from PDF", e)
            ""
        }
    }

    private fun cleanOpenAIResponse(response: String): String {
        return response.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    private fun buildCategorizationContext(): String {
        val vatCodes = vatService.findAllVatCodes()
        val costAccounts = accountService.findAllAccounts()
            .filter { account -> 
                val accountNumber = account.noAccountNumber
                // Filter for cost accounts: 4XXX (Cost of Goods) and 6XXX-7XXX (Operating Expenses)
                accountNumber.startsWith("4") || 
                (accountNumber.startsWith("6") || accountNumber.startsWith("7"))
            }
            .sortedBy { it.noAccountNumber }

        val vatCodesContext = vatCodes.joinToString("\n") { vat ->
            "- Code: ${vat.code}, Description: ${vat.description}, Rate: ${vat.rate}%, Type: ${vat.vatType}, Category: ${vat.vatCategory}"
        }

        val costAccountsContext = costAccounts.joinToString("\n") { account ->
            "- ${account.noAccountNumber}: ${account.accountName} (${account.accountDescription})"
        }

        return """
            AVAILABLE CATEGORIZATION RULES:
            
            VAT CODES (for vat_code field):
            $vatCodesContext
            
            COST ACCOUNTS (for debit_prediction.account field):
            $costAccountsContext
            
            IMPORTANT RULES:
            1. For vat_code: Use only the exact codes from the VAT codes list above
            2. For debit_prediction.account: Use only account numbers from the cost accounts list above (4XXX, 6XXX, 7XXX series)
            3. If no exact match is found, use the most appropriate code/account based on the invoice content
            4. For Norwegian invoices, VAT rate 25% typically uses code "1", 12% uses code "13", 0% uses code "0"
        """.trimIndent()
    }

    private fun sendToOpenAI(text: String): String? {
        if (openaiApiKey.isBlank()) {
            logger.error("OpenAI API key is not configured")
            return null
        }

        val categorizationContext = buildCategorizationContext()

        val prompt = """
            You are an invoice extraction assistant specialized in Norwegian accounting standards.
            
            $categorizationContext
            
            Extract the following details from this invoice text and return a JSON response in this exact format:

            {
              "debit_prediction": {
                "account": "6540"
              },
              "invoice_details": {
                "invoice_number": "INV-2025-0092",
                "invoice_date": "2025-07-15",
                "due_date": "2025-08-15",
                "KID_number": "1234567890123456789012345",
                "account_number": "98765432101",
                "swift_bic": "DNBANOKKXXX",
                "company_name": "Example Supplies AS",
                "company_number": "981234567",
                "order_total": 12500.50,
                "currency": "NOK",
                "vat_percentage": 25.0,
                "vat_code": "1",
                "vat_amount": 2500.10,
                "description": "Office chairs and desks, July 2025",
                "project": "Office Upgrade Q3"
              }
            }

            CRITICAL INSTRUCTIONS:
            1. For vat_code: MUST use one of the exact codes from the VAT codes list above
            2. For debit_prediction.account: MUST use one of the exact account numbers from the cost accounts list above
            3. For Norwegian invoices: VAT rate 25% = code "1", 12% = code "13", 0% = code "0"
            4. For cost accounts: Choose the most appropriate account based on the invoice description
            5. If unsure about account, prefer general accounts like 6540 (Inventory) or 6790 (Other External Services)
            6. For optional fields, use null if not found:
               - due_date: Use null if no due date is specified
               - KID_number: Use null if not found (common for international invoices)
               - account_number: Use null if no bank account number is provided
               - swift_bic: Use null if no SWIFT/BIC code is provided
               - project: Use null if no specific project is mentioned
            7. For dates: Use YYYY-MM-DD format
            8. For amounts: Use decimal numbers (e.g., 12500.50, not 12500,50)
            9. For company_number: Use only the numeric part (e.g., "981234567" not "NO 981 234 567 MVA")

            Here is the invoice text:
            \"\"\"
            $text
            \"\"\"
        """.trimIndent()

        return try {
            val requestBody = mapOf(
                "model" to "gpt-4o",
                "messages" to listOf(
                    mapOf("role" to "system", "content" to "You are a specialized invoice extraction assistant for Norwegian accounting. Always use the provided categorization rules exactly."),
                    mapOf("role" to "user", "content" to prompt)
                ),
                "temperature" to 0.1
            )

            val response = webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer $openaiApiKey")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OpenAIResponse::class.java)
                .block()

            response?.choices?.firstOrNull()?.message?.content
        } catch (e: Exception) {
            logger.error("Error calling OpenAI API", e)
            null
        }
    }

    private fun validateAndCorrectCategorization(invoiceData: InvoiceData): InvoiceData {
        var correctedVatCode = invoiceData.invoice_details.vat_code
        var correctedAccount = invoiceData.debit_prediction.account
        
        // Validate VAT code
        if (!vatService.vatCodeExists(invoiceData.invoice_details.vat_code)) {
            logger.warn("Invalid VAT code '${invoiceData.invoice_details.vat_code}' detected, attempting to correct...")
            correctedVatCode = determineCorrectVatCode(invoiceData.invoice_details.vat_percentage)
            logger.info("Corrected VAT code from '${invoiceData.invoice_details.vat_code}' to '$correctedVatCode'")
        }
        
        // Validate account number
        val account = accountService.findAccountByNumber(invoiceData.debit_prediction.account)
        if (account == null || !isCostAccount(account.noAccountNumber)) {
            logger.warn("Invalid account number '${invoiceData.debit_prediction.account}' detected, attempting to correct...")
            correctedAccount = determineCorrectAccount(invoiceData.invoice_details.description)
            logger.info("Corrected account from '${invoiceData.debit_prediction.account}' to '$correctedAccount'")
        }
        
        return invoiceData.copy(
            debit_prediction = invoiceData.debit_prediction.copy(account = correctedAccount),
            invoice_details = invoiceData.invoice_details.copy(vat_code = correctedVatCode)
        )
    }
    
    private fun determineCorrectVatCode(vatPercentage: Double): String {
        return when {
            vatPercentage == 25.0 -> "1"  // Standard Norwegian VAT rate
            vatPercentage == 12.0 -> "13" // Low Norwegian VAT rate
            vatPercentage == 0.0 -> "0"   // No VAT
            else -> "1" // Default to standard rate if unclear
        }
    }
    
    private fun determineCorrectAccount(description: String): String {
        val lowerDescription = description.lowercase()
        return when {
            lowerDescription.contains("office") || lowerDescription.contains("supplies") -> "6540" // Inventory
            lowerDescription.contains("rent") || lowerDescription.contains("lease") -> "6300" // Rent of Premises
            lowerDescription.contains("electricity") || lowerDescription.contains("power") -> "6200" // Electricity
            lowerDescription.contains("telephone") || lowerDescription.contains("phone") -> "6900" // Telephone
            lowerDescription.contains("travel") || lowerDescription.contains("transport") -> "7100" // Travel Costs
            lowerDescription.contains("advertising") || lowerDescription.contains("marketing") -> "7320" // Advertising Costs
            lowerDescription.contains("insurance") -> "7500" // Insurance Premiums
            lowerDescription.contains("audit") || lowerDescription.contains("accounting") -> "6700" // Audit and Accounting Fees
            else -> "6790" // Other External Services (default)
        }
    }
    
    private fun isCostAccount(accountNumber: String): Boolean {
        return accountNumber.startsWith("4") || accountNumber.startsWith("6") || accountNumber.startsWith("7")
    }
}

sealed class InvoiceExtractionResult {
    data class Success(val data: InvoiceData) : InvoiceExtractionResult()
    data class Error(val message: String) : InvoiceExtractionResult()
}

data class InvoiceData(
    val debit_prediction: DebitPrediction,
    val invoice_details: InvoiceDetails
)

data class DebitPrediction(
    val account: String
)

data class InvoiceDetails(
    val invoice_number: String,
    val invoice_date: String,
    val due_date: String?,  // Made nullable
    @com.fasterxml.jackson.annotation.JsonProperty("KID_number")
    val KID_number: String?,  // Made nullable
    val account_number: String?,  // Made nullable
    val swift_bic: String?,  // Made nullable
    val company_name: String,
    val company_number: String,
    val order_total: Double,
    val currency: String,
    val vat_percentage: Double,
    val vat_code: String,
    val vat_amount: Double,
    val description: String,
    val project: String?  // Made nullable
)

data class OpenAIResponse(
    val choices: List<OpenAIChoice>
)

data class OpenAIChoice(
    val message: OpenAIMessage
)

data class OpenAIMessage(
    val content: String
) 