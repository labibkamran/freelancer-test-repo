package com.respiroc.ledger.application

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.command.CreatePostingCommand
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
        val tenantId = TenantContextHolder.getTenantId()
            ?: throw IllegalStateException("No tenant context available")

        postings.forEach { postingData ->
            if (accountApi.findAccountByNumber(postingData.accountNumber) == null) {
                throw IllegalArgumentException("No account found with account number = ${postingData.accountNumber}")
            }
            
            // Validate VAT code if provided
            if (postingData.vatCode != null) {
                if (!vatApi.vatCodeExists(postingData.vatCode)) {
                    throw IllegalArgumentException("Invalid VAT code: ${postingData.vatCode}")
                }
            }
        }

        val totalAmount = postings.sumOf { it.amount }
        if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw IllegalArgumentException("Postings must balance: total amount is $totalAmount")
        }

        val createdPostings = postings.map { postingData ->
            val posting = Posting()
            posting.accountNumber = postingData.accountNumber
            posting.amount = postingData.amount
            posting.currency = postingData.currency
            posting.postingDate = postingData.postingDate
            posting.description = postingData.description
            posting.tenantId = tenantId
            posting.originalAmount = postingData.originalAmount
            posting.originalCurrency = postingData.originalCurrency
            
            // Handle VAT fields
            if (postingData.vatCode != null) {
                val vatCode = vatApi.findVatCodeByCode(postingData.vatCode)
                if (vatCode != null) {
                    posting.vatCode = vatCode.code
                    posting.vatRate = vatCode.rate
                    posting.vatAmount = vatApi.calculateVatAmount(postingData.amount.abs(), vatCode)
                }
            }
            
            posting
        }

        return postingRepository.saveAll(createdPostings)
    }

    @Transactional(readOnly = true)
    override fun findAllPostingsByUser(user: UserContext): List<Posting> {
        val tenantId = TenantContextHolder.getTenantId()
            ?: throw IllegalStateException("No tenant context available")

        return postingRepository.findByTenantIdOrderByPostingDateDescCreatedAtDesc(tenantId)
    }

    @Transactional(readOnly = true)
    override fun findPostingsByAccountNumber(accountNumber: String, user: UserContext): List<Posting> {
        val tenantId = TenantContextHolder.getTenantId()
            ?: throw IllegalStateException("No tenant context available")

        return postingRepository.findByAccountNumberAndTenantIdOrderByPostingDateDescCreatedAtDesc(
            accountNumber, tenantId
        )
    }

    @Transactional(readOnly = true)
    override fun findPostingsByDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        user: UserContext
    ): List<Posting> {
        val tenantId = TenantContextHolder.getTenantId()
            ?: throw IllegalStateException("No tenant context available")

        return postingRepository.findByPostingDateBetweenAndTenantIdOrderByPostingDateDescCreatedAtDesc(
            startDate, endDate, tenantId
        )
    }

    @Transactional(readOnly = true)
    override fun getAccountBalance(accountNumber: String, user: UserContext): BigDecimal {
        val tenantId = TenantContextHolder.getTenantId()
            ?: throw IllegalStateException("No tenant context available")

        return postingRepository.getAccountBalance(accountNumber, tenantId)
    }
}