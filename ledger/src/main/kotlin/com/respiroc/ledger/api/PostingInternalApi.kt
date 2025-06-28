package com.respiroc.ledger.api

import com.respiroc.ledger.api.command.CreatePostingCommand
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.util.context.UserContext
import java.math.BigDecimal
import java.time.LocalDate

interface PostingInternalApi {
    fun createBatchPostings(
        postings: List<CreatePostingCommand>,
        user: UserContext
    ): List<Posting>

    fun findAllPostingsByUser(user: UserContext): List<Posting>
    fun findPostingsByAccountNumber(accountNumber: String, user: UserContext): List<Posting>
    fun findPostingsByDateRange(startDate: LocalDate, endDate: LocalDate, user: UserContext): List<Posting>
    fun getAccountBalance(accountNumber: String, user: UserContext): BigDecimal
}