package com.respiroc.ledger.api.payload

import java.math.BigDecimal

data class BalanceSheetPayload(
    val entries: List<BalanceSheetEntry>,
    val totalBalance: BigDecimal
)

data class BalanceSheetEntry(
    val accountNumber: String,
    val accountName: String,
    val accountDescription: String,
    val amount: BigDecimal,
)
