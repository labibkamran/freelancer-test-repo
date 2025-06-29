package com.respiroc.ledger.api

import com.respiroc.ledger.api.command.CreatePostingCommand
import com.respiroc.ledger.api.result.TrialBalanceResult
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.util.context.UserContext
import java.time.LocalDate

interface PostingInternalApi {
    fun createBatchPostings(
        postings: List<CreatePostingCommand>,
        user: UserContext
    ): List<Posting>

    fun getTrialBalance(startDate: LocalDate, endDate: LocalDate, user: UserContext): TrialBalanceResult
}