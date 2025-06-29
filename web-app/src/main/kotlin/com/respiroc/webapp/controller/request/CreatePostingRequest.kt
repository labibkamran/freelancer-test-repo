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
    fun validate(): Boolean {
        return accountNumber.isNotBlank() && amount != null && amount > BigDecimal.ZERO && 
               (type == "debit" || type == "credit")
    }
    
    fun getSignedAmount(): BigDecimal {
        return if (type == "credit") amount?.negate() ?: BigDecimal.ZERO else amount ?: BigDecimal.ZERO
    }
    
    fun isValid(): Boolean {
        return accountNumber.isNotBlank() && amount != null && amount > BigDecimal.ZERO
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
    fun validate(): Boolean {
        val hasDebitAccount = debitAccount.isNotBlank()
        val hasCreditAccount = creditAccount.isNotBlank()
        
        return (hasDebitAccount xor hasCreditAccount) && // Either debit OR credit, not both
               amount != null && amount > BigDecimal.ZERO &&
               postingDate != null
    }
    
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
        
        // If it contains parentheses, extract code before the first space/parenthesis
        // Examples: "1 (25%)" -> "1", "VAT1 (12%)" -> "VAT1", "2" -> "2"
        return if (trimmed.contains("(")) {
            trimmed.substringBefore("(").trim().takeIf { it.isNotBlank() }
        } else {
            // If no parentheses, use the whole value as is (already clean code)
            trimmed.takeIf { it.isNotBlank() }
        }
    }
}

data class CreateBatchPostingRequest(
    // Individual posting lines - no global date needed
    val postingLines: List<PostingLine?> = emptyList(),

    // Legacy entries for backward compatibility
    val entries: List<PostingEntry?> = emptyList()
) {
    fun validate(): Boolean {
        // Use postingLines if provided, otherwise fall back to legacy entries
        if (postingLines.isNotEmpty()) {
            val validPostingLines = postingLines.filterNotNull().filter { it.validate() }
            if (validPostingLines.isEmpty()) return false
            
            // Sum of debits must equal sum of credits
            val totalDebit = validPostingLines.filter { it.getAccountType() == "debit" }
                .sumOf { it.amount!! }
            val totalCredit = validPostingLines.filter { it.getAccountType() == "credit" }
                .sumOf { it.amount!! }
            
            return totalDebit.compareTo(totalCredit) == 0
        }
        
        if (entries.isEmpty()) return false
        
        val validEntries = entries.filterNotNull().filter { it.isValid() }
        
        if (validEntries.isEmpty()) return false
        
        if (!validEntries.all { it.validate() }) return false
        
        // Sum of debits must equal sum of credits
        val totalDebit = validEntries.filter { it.type == "debit" }
            .sumOf { it.amount!! }
        val totalCredit = validEntries.filter { it.type == "credit" }
            .sumOf { it.amount!! }
        
        return totalDebit.compareTo(totalCredit) == 0
    }
    
    // Convert posting lines to posting entries
    fun getAllPostingEntries(): List<PostingEntry> {
        val fromPostingLines = postingLines.filterNotNull()
            .filter { it.validate() }
            .map { it.toPostingEntry() }
            
        val fromEntries = entries.filterNotNull().filter { it.isValid() }
        
        return fromPostingLines + fromEntries
    }
} 