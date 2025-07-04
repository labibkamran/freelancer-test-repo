package com.respiroc.ledger.api.payload

import java.time.LocalDate

data class VoucherSummaryPayload(
    val id: Long,
    val number: Short,
    val date: LocalDate,
    val description: String?,
    val postingCount: Int
) 