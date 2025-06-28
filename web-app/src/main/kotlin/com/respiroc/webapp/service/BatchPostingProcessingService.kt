package com.respiroc.webapp.service

import com.respiroc.ledger.api.AccountInternalApi
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
    private val accountApi: AccountInternalApi,
    private val currencyService: CurrencyService
) {

    fun processBatchPostingRequest(
        request: CreateBatchPostingRequest,
        userContext: UserContext
    ): BatchPostingResult {
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val allPostingEntries = request.getAllPostingEntries()

        // Validate entries
        val validationResult = validatePostingEntries(allPostingEntries)
        if (!validationResult.isValid) {
            return BatchPostingResult.failure(validationResult.errorMessage!!)
        }

        // Validate request balance
        if (!request.validate()) {
            return BatchPostingResult.failure("Debit and credit amounts must be equal")
        }

        // Validate accounts exist
        val accountValidationResult = validateAccountsExist(allPostingEntries)
        if (!accountValidationResult.isValid) {
            return BatchPostingResult.failure(accountValidationResult.errorMessage!!)
        }

        try {
            // Convert to PostingCommand objects
            val postingDataList = convertToPostingCommands(request, allPostingEntries, companyCurrency)
            
            // Create postings
            postingApi.createBatchPostings(postingDataList, userContext)
            
            return BatchPostingResult.success("Journal entry saved successfully!")
        } catch (e: Exception) {
            return BatchPostingResult.failure("Failed to save journal entry: ${e.message}")
        }
    }

    private fun validatePostingEntries(entries: List<PostingEntry>): ValidationResult {
        if (entries.isEmpty()) {
            return ValidationResult.failure("At least one posting entry is required")
        }

        entries.forEach { entry ->
            if (entry.accountNumber.isBlank()) {
                return ValidationResult.failure("Account number is required")
            }

            if (entry.amount == null || entry.amount <= BigDecimal.ZERO) {
                return ValidationResult.failure("Amount must be greater than zero")
            }

            if (!currencyService.isCurrencySupported(entry.currency)) {
                return ValidationResult.failure("Unsupported currency: ${entry.currency}")
            }
        }

        return ValidationResult.success()
    }

    private fun validateAccountsExist(entries: List<PostingEntry>): ValidationResult {
        entries.forEach { entry ->
            if (!accountApi.accountExists(entry.accountNumber)) {
                return ValidationResult.failure("Account ${entry.accountNumber} not found")
            }
        }
        return ValidationResult.success()
    }

    private fun convertToPostingCommands(
        request: CreateBatchPostingRequest,
        allPostingEntries: List<PostingEntry>,
        companyCurrency: String
    ): List<CreatePostingCommand> {
        return allPostingEntries.map { entry ->
            val originalAmount = entry.amount!!
            val originalCurrency = entry.currency
            
            // Find posting date from posting lines
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

    private data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String?
    ) {
        companion object {
            fun success() = ValidationResult(true, null)
            fun failure(message: String) = ValidationResult(false, message)
        }
    }
} 