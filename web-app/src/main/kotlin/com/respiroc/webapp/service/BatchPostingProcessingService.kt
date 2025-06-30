package com.respiroc.webapp.service

import com.respiroc.ledger.api.PostingInternalApi
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
    private val currencyService: CurrencyService
) {

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
        return postingLines.map { line ->
            val originalAmount = line.amount!!
            val originalCurrency = line.currency

            CreatePostingCommand(
                accountNumber = line.getAccountNumber(),
                amount = calculateConvertedSignedAmount(line, originalCurrency, companyCurrency),
                currency = companyCurrency,
                postingDate = line.postingDate ?: LocalDate.now(),
                description = line.description,
                originalAmount = if (originalCurrency != companyCurrency) originalAmount else null,
                originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
                vatCode = line.getVatCode()
            )
        }
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