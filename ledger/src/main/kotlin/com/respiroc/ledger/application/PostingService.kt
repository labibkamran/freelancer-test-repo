package com.respiroc.ledger.application

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.payload.TrialBalanceEntry
import com.respiroc.ledger.api.payload.TrialBalancePayload
import com.respiroc.ledger.api.payload.GeneralLedgerPayload
import com.respiroc.ledger.api.payload.GeneralLedgerAccountEntry
import com.respiroc.ledger.api.payload.GeneralLedgerPostingEntry
import com.respiroc.ledger.domain.repository.PostingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional
class PostingService(
    private val postingRepository: PostingRepository,
    private val accountApi: AccountInternalApi
) : PostingInternalApi {

    @Transactional(readOnly = true)
    override fun getTrialBalance(startDate: LocalDate, endDate: LocalDate): TrialBalancePayload {
        val tenantId = currentTenantId()
        val accountNumbers = postingRepository.findDistinctAccountNumbersByTenant(tenantId)
        val accounts = accountApi.findAllAccounts().associateBy { it.noAccountNumber }

        val trialBalanceEntries = accountNumbers.mapNotNull { accountNumber ->
            val account = accounts[accountNumber]
            if (account != null) {
                val openingBalance = postingRepository.getAccountBalanceBeforeDate(accountNumber, tenantId, startDate)
                val movement = postingRepository.getAccountMovementInPeriod(accountNumber, tenantId, startDate, endDate)
                val closingBalance = openingBalance + movement

                // Only include accounts that have activity or balance
                if (openingBalance.compareTo(BigDecimal.ZERO) != 0 ||
                    movement.compareTo(BigDecimal.ZERO) != 0 ||
                    closingBalance.compareTo(BigDecimal.ZERO) != 0
                ) {
                    TrialBalanceEntry(
                        accountNumber = accountNumber,
                        accountName = account.accountName,
                        openingBalance = openingBalance,
                        difference = movement,
                        closingBalance = closingBalance
                    )
                } else null
            } else null
        }

        val totalOpeningBalance = trialBalanceEntries.sumOf { it.openingBalance }
        val totalDifference = trialBalanceEntries.sumOf { it.difference }
        val totalClosingBalance = trialBalanceEntries.sumOf { it.closingBalance }

        return TrialBalancePayload(
            entries = trialBalanceEntries,
            totalOpeningBalance = totalOpeningBalance,
            totalDifference = totalDifference,
            totalClosingBalance = totalClosingBalance
        )
    }

    @Transactional(readOnly = true)
    override fun getGeneralLedger(
        startDate: LocalDate,
        endDate: LocalDate,
        accountNumber: String?
    ): GeneralLedgerPayload {
        val tenantId = currentTenantId()
        val accounts = accountApi.findAllAccounts().associateBy { it.noAccountNumber }

        val summaryData = postingRepository.getGeneralLedgerSummary(accountNumber, tenantId, startDate, endDate)

        val accountEntries = summaryData.mapNotNull { row ->
            val accNumber = row[0] as String
            val openingBalance = row[1] as BigDecimal
            val periodMovement = row[2] as BigDecimal
            val transactionCount = row[3] as Long

            val account = accounts[accNumber]
            if (account != null) {
                val closingBalance = openingBalance + periodMovement

                // Only get detailed postings if needed for display
                val postings = if (transactionCount > 0) {
                    postingRepository.findPostingsByAccountAndDateRange(accNumber, tenantId, startDate, endDate)
                        .map { posting ->
                            GeneralLedgerPostingEntry(
                                id = posting.id,
                                voucherId = posting.voucherId,
                                voucherNumber = posting.voucher?.number,
                                date = posting.postingDate,
                                description = posting.description,
                                vatCode = posting.vatCode,
                                currency = posting.currency,
                                amount = posting.amount,
                                originalCurrency = posting.originalCurrency,
                                originalAmount = posting.originalAmount
                            )
                        }
                } else {
                    emptyList()
                }

                GeneralLedgerAccountEntry(
                    accountNumber = accNumber,
                    accountName = account.accountName,
                    openingBalance = openingBalance,
                    postings = postings,
                    closingBalance = closingBalance
                )
            } else null
        }

        val totalAmount = accountEntries.sumOf { it.closingBalance }

        return GeneralLedgerPayload(
            accounts = accountEntries,
            totalAmount = totalAmount
        )
    }
}