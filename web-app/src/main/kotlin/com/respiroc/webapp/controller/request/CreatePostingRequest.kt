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

data class PostingEntry(
    val accountNumber: String = "",
    val debitAmount: BigDecimal? = null,
    val creditAmount: BigDecimal? = null,
    val description: String? = null
) {
    fun validate(): Boolean {
        if (accountNumber.isBlank()) return false
        return (debitAmount != null && debitAmount > BigDecimal.ZERO && creditAmount == null) || 
               (debitAmount == null && creditAmount != null && creditAmount > BigDecimal.ZERO)
    }

    fun getAmount(): BigDecimal {
        return debitAmount ?: creditAmount?.negate() ?: BigDecimal.ZERO
    }
    
    fun isValid(): Boolean {
        return accountNumber.isNotBlank() && (debitAmount != null || creditAmount != null)
    }
}

data class CreateBatchPostingRequest(
    @field:NotNull(message = "Posting date is required")
    val postingDate: LocalDate? = null,

    @field:NotBlank(message = "Currency is required")
    @field:Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    val currency: String = "NOK",

    val description: String? = null,

    val entries: List<PostingEntry?> = emptyList()
) {
    fun validate(): Boolean {
        if (entries.isEmpty()) return false
        
        // Filter out null entries and empty entries
        val validEntries = entries.filterNotNull().filter { it.isValid() }
        
        if (validEntries.isEmpty()) return false
        
        // All entries must have either debit or credit (not both)
        if (!validEntries.all { it.validate() }) return false
        
        // Sum of debits must equal sum of credits
        val totalDebit = validEntries.filter { it.debitAmount != null }
            .sumOf { it.debitAmount!! }
        val totalCredit = validEntries.filter { it.creditAmount != null }
            .sumOf { it.creditAmount!! }
        
        return totalDebit.compareTo(totalCredit) == 0
    }
} 