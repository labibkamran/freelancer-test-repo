package com.respiroc.ledger.api

import com.respiroc.ledger.api.payload.GeneralLedgerPayload
import com.respiroc.ledger.api.payload.ProfitLossPayload
import com.respiroc.ledger.api.payload.TrialBalancePayload
import com.respiroc.ledger.domain.model.AccountType
import com.respiroc.util.context.ContextAwareApi
import java.time.LocalDate

interface PostingInternalApi : ContextAwareApi {
    fun getTrialBalance(startDate: LocalDate, endDate: LocalDate): TrialBalancePayload
    fun getPostingsForProfitLoss(startDate: LocalDate, endDate: LocalDate): Map<AccountType, ProfitLossPayload>
    fun getGeneralLedger(startDate: LocalDate, endDate: LocalDate, accountNumber: String? = null): GeneralLedgerPayload
}