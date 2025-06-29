package com.respiroc.ledger.application

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.command.CreatePostingCommand
import com.respiroc.ledger.api.result.TrialBalanceEntry
import com.respiroc.ledger.api.result.TrialBalanceResult
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.ledger.domain.repository.PostingRepository
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.util.context.UserContext
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
) : PostingInternalApi {

    override fun createBatchPostings(
        postings: List<CreatePostingCommand>,
        user: UserContext
    ): List<Posting> {
        val tenantId = requireTenantContext()
        
        validatePostingCommands(postings)
        validateBalance(postings)
        
        val createdPostings = postings.map { postingData ->
            createPostingEntity(postingData, tenantId)
        }

        return postingRepository.saveAll(createdPostings)
    }

    @Transactional(readOnly = true)
    override fun getTrialBalance(startDate: LocalDate, endDate: LocalDate, user: UserContext): TrialBalanceResult {
        val tenantId = requireTenantContext()
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
                    closingBalance.compareTo(BigDecimal.ZERO) != 0) {
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
        
        return TrialBalanceResult(
            entries = trialBalanceEntries,
            totalOpeningBalance = totalOpeningBalance,
            totalDifference = totalDifference,
            totalClosingBalance = totalClosingBalance
        )
    }
    
    // -------------------------------
    // Private Helper Methods
    // -------------------------------
    
    private fun requireTenantContext(): Long {
        return TenantContextHolder.getTenantId()
            ?: throw IllegalStateException("No tenant context available")
    }
    
    private fun validatePostingCommands(postings: List<CreatePostingCommand>) {
        postings.forEach { postingData ->
            validateAccount(postingData.accountNumber)
            validateVatCode(postingData.vatCode)
        }
    }
    
    private fun validateAccount(accountNumber: String) {
        if (accountApi.findAccountByNumber(accountNumber) == null) {
            throw IllegalArgumentException("No account found with account number = $accountNumber")
        }
    }
    
    private fun validateVatCode(vatCode: String?) {
        if (vatCode != null && !vatApi.vatCodeExists(vatCode)) {
            throw IllegalArgumentException("Invalid VAT code: $vatCode")
        }
    }
    
    private fun validateBalance(postings: List<CreatePostingCommand>) {
        val totalAmount = postings.sumOf { it.amount }
        if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw IllegalArgumentException("Postings must balance: total amount is $totalAmount")
        }
    }
    
    private fun createPostingEntity(postingData: CreatePostingCommand, tenantId: Long): Posting {
        val posting = Posting()
        posting.accountNumber = postingData.accountNumber
        posting.amount = postingData.amount
        posting.currency = postingData.currency
        posting.postingDate = postingData.postingDate
        posting.description = postingData.description
        posting.tenantId = tenantId
        posting.originalAmount = postingData.originalAmount
        posting.originalCurrency = postingData.originalCurrency
        
        applyVatInformation(posting, postingData)
        
        return posting
    }
    
    private fun applyVatInformation(posting: Posting, postingData: CreatePostingCommand) {
        if (postingData.vatCode != null) {
            val vatCode = vatApi.findVatCodeByCode(postingData.vatCode)
            if (vatCode != null) {
                posting.vatCode = vatCode.code
                posting.vatRate = vatCode.rate
                posting.vatAmount = vatApi.calculateVatAmount(postingData.amount.abs(), vatCode)
            }
        }
    }
}