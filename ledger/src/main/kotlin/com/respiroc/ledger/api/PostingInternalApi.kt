package com.respiroc.ledger.api

import com.respiroc.ledger.api.payload.CreatePostingPayload
import com.respiroc.ledger.api.payload.TrialBalancePayload
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.util.context.UserContext
import java.time.LocalDate

interface PostingInternalApi {
    fun createBatchPostings(
        postings: List<CreatePostingPayload>,
        user: UserContext
    ): List<Posting>

    fun getTrialBalance(startDate: LocalDate, endDate: LocalDate, user: UserContext): TrialBalancePayload
}