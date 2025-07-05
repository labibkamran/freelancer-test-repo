package com.respiroc.ledger.api

import com.respiroc.ledger.api.payload.CreatePostingPayload
import com.respiroc.ledger.api.payload.TrialBalancePayload
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.util.context.ContextAwareApi
import java.time.LocalDate

interface PostingInternalApi : ContextAwareApi {
    fun createBatchPostings(postings: List<CreatePostingPayload>): List<Posting>
    fun getTrialBalance(startDate: LocalDate, endDate: LocalDate): TrialBalancePayload
}