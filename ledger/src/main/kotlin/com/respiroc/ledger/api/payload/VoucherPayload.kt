package com.respiroc.ledger.api.payload

import com.respiroc.ledger.domain.model.Posting
import java.time.LocalDate

data class VoucherPayload(
    val id: Long,
    val number: Short,
    val date: LocalDate
) 