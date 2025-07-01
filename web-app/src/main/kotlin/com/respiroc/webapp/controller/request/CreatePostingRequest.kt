package com.respiroc.webapp.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
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

// Single posting line - represents either debit or credit in a journal entry
data class PostingLine(
    val debitAccount: String = "",
    val creditAccount: String = "",
    val amount: BigDecimal? = null,
    val currency: String = "NOK",
    val postingDate: LocalDate = LocalDate.now(),
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
    
    fun getSignedAmount(): BigDecimal {
        val amt = amount ?: BigDecimal.ZERO
        return if (getAccountType() == "credit") amt.negate() else amt
    }
    
    fun getVatCode(): String? {
        val vatCodeValue = if (debitAccount.isNotBlank()) debitVatCode else creditVatCode
        return extractActualVatCode(vatCodeValue)
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
    val postingLines: List<PostingLine?> = emptyList()
) {
    fun getValidPostingLines(): List<PostingLine> {
        return postingLines.filterNotNull()
            .filter { it.amount != null && it.amount > BigDecimal.ZERO }
            .filter { it.getAccountNumber().isNotBlank() }
    }
} 