package com.respiroc.ledger.api.payload

import java.time.LocalDate

data class VoucherPayload(
    val id: Long,
    val number: String,
    val date: LocalDate
) 