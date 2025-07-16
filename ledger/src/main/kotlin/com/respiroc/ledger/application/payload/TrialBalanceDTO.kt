package com.respiroc.ledger.application.payload

import java.math.BigDecimal

data class TrialBalanceDTO (
    val entries: List<TrialBalanceEntry>,
    val totalOpeningBalance: BigDecimal,
    val totalDifference: BigDecimal,
    val totalClosingBalance: BigDecimal
)

data class TrialBalanceEntry(
    val accountNumber: String,
    val accountName: String,
    val openingBalance: BigDecimal,
    val difference: BigDecimal,
    val closingBalance: BigDecimal
) {
    val openingBalanceClass: String
        get() = balanceClass(openingBalance)

    val differenceClass: String
        get() = balanceClass(difference)

    val closingBalanceClass: String
        get() = balanceClass(closingBalance)

    private fun balanceClass(amount: BigDecimal): String = when {
        amount > BigDecimal.ZERO -> POSITIVE
        amount < BigDecimal.ZERO -> NEGATIVE
        else -> ZERO
    }

    companion object {
        private const val POSITIVE = "r-positive-amount"
        private const val NEGATIVE = "r-negative-amount"
        private const val ZERO = "r-zero-amount"
    }
}