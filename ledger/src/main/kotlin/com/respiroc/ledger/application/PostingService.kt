package com.respiroc.ledger.application

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.payload.CreatePostingPayload
import com.respiroc.ledger.api.payload.TrialBalanceEntry
import com.respiroc.ledger.api.payload.TrialBalancePayload
import com.respiroc.ledger.api.payload.GeneralLedgerPayload
import com.respiroc.ledger.api.payload.GeneralLedgerAccountEntry
import com.respiroc.ledger.api.payload.GeneralLedgerPostingEntry
import com.respiroc.ledger.domain.exception.AccountNotFoundException
import com.respiroc.ledger.domain.exception.InvalidVatCodeException
import com.respiroc.ledger.domain.exception.PostingsNotBalancedException
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.ledger.domain.repository.PostingRepository
import com.respiroc.util.context.ContextAwareApi
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional
class PostingService(
    private val postingRepository: PostingRepository,
    private val accountApi: AccountInternalApi,
    private val vatApi: VatInternalApi
) : PostingInternalApi, ContextAwareApi {

    override fun createBatchPostings(postings: List<CreatePostingPayload>): List<Posting> {
        validatePostingCommands(postings)
        validateBalance(postings)

        val createdPostings = postings.map { postingData ->
            createPostingEntity(postingData, currentTenantId())
        }

        return postingRepository.saveAll(createdPostings)
    }

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
                                voucherNumber = posting.voucher?.getDisplayNumber(),
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

    // -------------------------------
    // Private Helper Methods
    // -------------------------------

    private fun validatePostingCommands(postings: List<CreatePostingPayload>) {
        postings.forEach { postingData ->
            validateAccount(postingData.accountNumber)
            validateVatCode(postingData.vatCode)
        }
    }

    private fun validateAccount(accountNumber: String) {
        if (accountApi.findAccountByNumber(accountNumber) == null) {
            throw AccountNotFoundException(accountNumber)
        }
    }

    private fun validateVatCode(vatCode: String?) {
        if (vatCode != null && !vatApi.vatCodeExists(vatCode)) {
            throw InvalidVatCodeException(vatCode)
        }
    }

    private fun validateBalance(postings: List<CreatePostingPayload>) {
        val totalAmount = postings.sumOf { it.amount }
        // Round to 2 decimal places for balance validation (currency conversion can introduce extra decimals)
        val roundedAmount = totalAmount.setScale(2, java.math.RoundingMode.HALF_UP)
        if (roundedAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw PostingsNotBalancedException(roundedAmount)
        }
    }

    private fun createPostingEntity(postingData: CreatePostingPayload, tenantId: Long): Posting {
        val posting = Posting()
        posting.accountNumber = postingData.accountNumber
        posting.amount = postingData.amount
        posting.currency = postingData.currency
        posting.postingDate = postingData.postingDate
        posting.description = postingData.description
        posting.tenantId = tenantId
        posting.originalAmount = postingData.originalAmount
        posting.originalCurrency = postingData.originalCurrency
        posting.vatCode = postingData.vatCode

        return posting
    }
}