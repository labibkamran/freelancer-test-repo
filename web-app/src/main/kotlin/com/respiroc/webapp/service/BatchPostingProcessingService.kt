package com.respiroc.webapp.service

import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.command.CreatePostingCommand
import com.respiroc.util.context.UserContext
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.request.CreateBatchPostingRequest
import com.respiroc.webapp.controller.request.PostingEntry
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
            val allPostingEntries = request.getAllPostingEntries()
            val postingCommands = convertToPostingCommands(request, allPostingEntries, companyCurrency)
            
            postingApi.createBatchPostings(postingCommands, userContext)
            
            BatchPostingResult.success("Journal entry saved successfully!")
        } catch (e: Exception) {
            BatchPostingResult.failure("Failed to save journal entry: ${e.message}")
        }
    }

    private fun convertToPostingCommands(
        request: CreateBatchPostingRequest,
        allPostingEntries: List<PostingEntry>,
        companyCurrency: String
    ): List<CreatePostingCommand> {
        return allPostingEntries.map { entry ->
            val originalAmount = entry.amount!!
            val originalCurrency = entry.currency
            val postingDate = findPostingDateForEntry(request, entry)
            
            CreatePostingCommand(
                accountNumber = entry.accountNumber,
                amount = calculateConvertedSignedAmount(entry, originalCurrency, companyCurrency),
                currency = companyCurrency,
                postingDate = postingDate,
                description = entry.description,
                originalAmount = if (originalCurrency != companyCurrency) originalAmount else null,
                originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
                vatCode = entry.vatCode
            )
        }
    }

    private fun findPostingDateForEntry(
        request: CreateBatchPostingRequest,
        entry: PostingEntry
    ): LocalDate {
        val postingLines = request.postingLines.filterNotNull()
        val matchingPostingLine = postingLines.find { line ->
            line.getAccountNumber() == entry.accountNumber &&
                    line.amount == entry.amount &&
                    line.getAccountType() == entry.type
        }
        return matchingPostingLine?.postingDate ?: LocalDate.now()
    }

    private fun calculateConvertedSignedAmount(
        entry: PostingEntry,
        originalCurrency: String,
        companyCurrency: String
    ): BigDecimal {
        val signedAmount = entry.getSignedAmount()
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