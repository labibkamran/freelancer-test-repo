package com.respiroc.webapp.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.validation.Valid
import java.math.BigDecimal
import java.time.LocalDate

data class CreatePostingRequest(
    @field:NotBlank(message = "Account number is required")
    @field:Size(max = 10, message = "Account number must not exceed 10 characters")
    val accountNumber: String,

    @field:NotNull(message = "Amount is required")
    val amount: BigDecimal,

    @field:NotBlank(message = "Currency is required")
    @field:Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    val currency: String,

    @field:NotNull(message = "Posting date is required")
    val postingDate: LocalDate,

    val description: String?
)

// Single posting entry representing one side of a transaction
data class PostingEntry(
    val accountNumber: String = "",
    val amount: BigDecimal? = null,
    val currency: String = "NOK",
    val type: String = "debit", // "debit" or "credit"
    val description: String? = null,
    val vatCode: String? = null
) {
    fun getSignedAmount(): BigDecimal {
        return if (type == "credit") amount?.negate() ?: BigDecimal.ZERO else amount ?: BigDecimal.ZERO
    }
}

// Single posting line - either debit or credit account, not both
data class PostingLine(
    val debitAccount: String = "",
    val creditAccount: String = "",
    val amount: BigDecimal? = null,
    val currency: String = "NOK",
    val postingDate: LocalDate? = null,
    val description: String? = null,
    val debitVatCode: String? = null,
    val creditVatCode: String? = null
) {
    fun getAccountNumber(): String {
        return if (debitAccount.isNotBlank()) debitAccount else creditAccount
    }
    
    fun getAccountType(): String {
        return if (debitAccount.isNotBlank()) "debit" else "credit"
    }
    
    fun toPostingEntry(): PostingEntry {
        val vatCode = if (debitAccount.isNotBlank()) debitVatCode else creditVatCode
        return PostingEntry(
            accountNumber = getAccountNumber(),
            amount = amount!!,
            currency = currency,
            type = getAccountType(),
            description = description,
            vatCode = extractActualVatCode(vatCode)
        )
    }
    
    private fun extractActualVatCode(vatCodeValue: String?): String? {
        if (vatCodeValue.isNullOrBlank()) return null
        
        val trimmed = vatCodeValue.trim()
        
        return if (trimmed.contains("(")) {
            trimmed.substringBefore("(").trim().takeIf { it.isNotBlank() }
        } else {
            trimmed.takeIf { it.isNotBlank() }
        }
    }
}

data class CreateBatchPostingRequest(
    val postingLines: List<PostingLine?> = emptyList(),
    val entries: List<PostingEntry?> = emptyList()
) {
    fun getAllPostingEntries(): List<PostingEntry> {
        // Only use postingLines since that's what the form uses
        // Using both postingLines and entries can cause duplicate entries
        val fromPostingLines = postingLines.filterNotNull()
            .filter { it.amount != null && it.amount > BigDecimal.ZERO }
            .map { it.toPostingEntry() }
            
        // Only add entries if postingLines is empty (for API usage)
        return if (fromPostingLines.isNotEmpty()) {
            fromPostingLines
        } else {
            entries.filterNotNull()
                .filter { it.amount != null && it.amount > BigDecimal.ZERO }
        }
    }
} 