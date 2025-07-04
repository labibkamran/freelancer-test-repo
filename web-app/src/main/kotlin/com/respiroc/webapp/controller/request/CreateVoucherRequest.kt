package com.respiroc.webapp.controller.request

import java.time.LocalDate
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class CreateVoucherRequest(
    @field:NotNull(message = "Voucher date is required")
    val voucherDate: LocalDate = LocalDate.now(),
    
    val voucherDescription: String? = null,
    
    @field:Valid
    val postingLines: List<PostingLine?> = emptyList()
) {
    fun getValidPostingLines(): List<PostingLine> {
        return postingLines.filterNotNull()
            .filter { it.amount != null && it.amount > java.math.BigDecimal.ZERO }
            .filter { it.getAccountNumber().isNotBlank() }
    }
} 