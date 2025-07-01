package com.respiroc.webapp.service

import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.command.CreatePostingCommand
import com.respiroc.util.context.UserContext
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.request.CreateBatchPostingRequest
import com.respiroc.webapp.controller.request.PostingLine
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class BatchPostingProcessingService(
    private val postingApi: PostingInternalApi,
    private val vatApi: VatInternalApi,
    private val currencyService: CurrencyService
) {

    companion object {
        private const val VAT_ACCOUNT_NUMBER = "2710"
    }

    fun processBatchPostingRequest(
        request: CreateBatchPostingRequest,
        userContext: UserContext
    ): BatchPostingResult {
        return try {
            val companyCurrency = currencyService.getCompanyCurrency("NO") // TODO: Replace with actual country code
            val validPostingLines = request.getValidPostingLines()
            val postingCommands = convertToPostingCommands(validPostingLines, companyCurrency)

            postingApi.createBatchPostings(postingCommands, userContext)

            BatchPostingResult.success("Journal entry saved successfully!")
        } catch (e: Exception) {
            BatchPostingResult.failure("Failed to save journal entry: ${e.message}")
        }
    }

    private fun convertToPostingCommands(
        postingLines: List<PostingLine>,
        companyCurrency: String
    ): List<CreatePostingCommand> {
        return postingLines.flatMap { line ->
            val originalAmount = line.amount!!
            val originalCurrency = line.currency
            val vatCode = line.getVatCode()

            if (vatCode != null) {
                // Create two postings: one for base amount and one for VAT
                createVatPostings(line, originalAmount, originalCurrency, companyCurrency, vatCode)
            } else {
                // Create single posting without VAT
                listOf(createSinglePosting(line, originalAmount, originalCurrency, companyCurrency, null))
            }
        }
    }

        private fun createVatPostings(
        line: PostingLine,
        originalAmount: BigDecimal,
        originalCurrency: String,
        companyCurrency: String,
        vatCode: String
    ): List<CreatePostingCommand> {
        val vatCodeEntity = vatApi.findVatCodeByCode(vatCode)
            ?: throw IllegalArgumentException("Invalid VAT code: $vatCode")

        val totalSignedAmount = calculateConvertedSignedAmount(line, originalCurrency, companyCurrency)
        val totalAbsAmount = totalSignedAmount.abs()
        
        val baseAmount = vatApi.calculateBaseAmountFromVatInclusive(totalAbsAmount, vatCodeEntity)
        val vatAmount = vatApi.calculateVatAmount(baseAmount, vatCodeEntity)
        
        val signedBaseAmount = if (totalSignedAmount < BigDecimal.ZERO) baseAmount.negate() else baseAmount
        val signedVatAmount = if (totalSignedAmount < BigDecimal.ZERO) vatAmount.negate() else vatAmount

        // Calculate original amounts if currency conversion is needed
        val (originalBaseAmount, originalVatAmount) = if (originalCurrency != companyCurrency) {
            val originalAbsAmount = originalAmount.abs()
            val originalBase = vatApi.calculateBaseAmountFromVatInclusive(originalAbsAmount, vatCodeEntity)
            val originalVat = vatApi.calculateVatAmount(originalBase, vatCodeEntity)
            
            val signedOriginalBase = if (originalAmount < BigDecimal.ZERO) originalBase.negate() else originalBase
            val signedOriginalVat = if (originalAmount < BigDecimal.ZERO) originalVat.negate() else originalVat
            
            Pair(signedOriginalBase, signedOriginalVat)
        } else {
            Pair(null, null)
        }

        val basePosting = CreatePostingCommand(
            accountNumber = line.getAccountNumber(),
            amount = signedBaseAmount,
            currency = companyCurrency,
            postingDate = line.postingDate,
            description = line.description,
            originalAmount = originalBaseAmount,
            originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
            vatCode = null
        )

        val vatPosting = CreatePostingCommand(
            accountNumber = VAT_ACCOUNT_NUMBER, // 2710 hard-coded
            amount = signedVatAmount,
            currency = companyCurrency,
            postingDate = line.postingDate,
            description = line.description,
            originalAmount = originalVatAmount,
            originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
            vatCode = vatCode
        )

        return listOf(basePosting, vatPosting)
    }

    private fun createSinglePosting(
        line: PostingLine,
        originalAmount: BigDecimal,
        originalCurrency: String,
        companyCurrency: String,
        vatCode: String?
    ): CreatePostingCommand {
        return CreatePostingCommand(
            accountNumber = line.getAccountNumber(),
            amount = calculateConvertedSignedAmount(line, originalCurrency, companyCurrency),
            currency = companyCurrency,
            postingDate = line.postingDate,
            description = line.description,
            originalAmount = if (originalCurrency != companyCurrency) originalAmount else null,
            originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
            vatCode = vatCode
        )
    }

    private fun calculateConvertedSignedAmount(
        line: PostingLine,
        originalCurrency: String,
        companyCurrency: String
    ): BigDecimal {
        val signedAmount = line.getSignedAmount()
        return if (originalCurrency == companyCurrency) {
            signedAmount
        } else {
            currencyService.convertCurrency(signedAmount, originalCurrency, companyCurrency)
        }
    }

    data class BatchPostingResult(
        val isSuccess: Boolean,
        val message: String
    ) {
        companion object {
            fun success(message: String) = BatchPostingResult(true, message)
            fun failure(message: String) = BatchPostingResult(false, message)
        }
    }
} 