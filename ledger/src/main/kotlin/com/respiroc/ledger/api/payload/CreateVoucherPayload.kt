package com.respiroc.ledger.api.payload

import java.time.LocalDate

data class CreateVoucherPayload(
    val date: LocalDate,
    val description: String?,
    val postings: List<CreatePostingPayload>
) 