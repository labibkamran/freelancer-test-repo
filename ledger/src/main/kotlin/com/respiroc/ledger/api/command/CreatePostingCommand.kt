package com.respiroc.ledger.api.command

import java.math.BigDecimal
import java.time.LocalDate

data class CreatePostingCommand(
    val accountNumber: String,
    val amount: BigDecimal,
    val currency: String,
    val postingDate: LocalDate,
    val description: String?,
    val originalAmount: BigDecimal? = null,
    val originalCurrency: String? = null,
    val vatCode: String? = null
)
