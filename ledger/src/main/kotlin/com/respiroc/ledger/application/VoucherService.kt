package com.respiroc.ledger.application

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.VoucherInternalApi
import com.respiroc.ledger.api.payload.CreatePostingPayload
import com.respiroc.ledger.api.payload.CreateVoucherPayload
import com.respiroc.ledger.api.payload.VoucherPayload
import com.respiroc.ledger.api.payload.VoucherSummaryPayload
import com.respiroc.ledger.domain.exception.AccountNotFoundException
import com.respiroc.ledger.domain.exception.EmptyPostingsException
import com.respiroc.ledger.domain.exception.InvalidVatCodeException
import com.respiroc.ledger.domain.exception.PostingsNotBalancedException
import com.respiroc.ledger.domain.model.Posting
import com.respiroc.ledger.domain.model.Voucher
import com.respiroc.ledger.domain.repository.PostingRepository
import com.respiroc.ledger.domain.repository.VoucherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional
class VoucherService(
    private val voucherRepository: VoucherRepository,
    private val postingRepository: PostingRepository,
    private val accountApi: AccountInternalApi,
    private val vatApi: VatInternalApi
) : VoucherInternalApi {

    override fun createVoucher(payload: CreateVoucherPayload): VoucherPayload {
        val tenantId = currentTenantId()

        val voucherNumber = generateNextVoucherNumber(tenantId, payload.date)

        validatePostingCommands(payload.postings)
        validateBalance(payload.postings)

        val voucher = createVoucherEntity(payload, tenantId, voucherNumber)
        val savedVoucher = voucherRepository.save(voucher)

        val postings = payload.postings.map { postingData ->
            createPostingEntity(postingData, tenantId, savedVoucher.id)
        }
        postingRepository.saveAll(postings)
        
        return VoucherPayload(
            id = savedVoucher.id,
            number = savedVoucher.getDisplayNumber(),
            date = savedVoucher.date
        )
    }

    @Transactional(readOnly = true)
    override fun findAllVoucherSummaries(): List<VoucherSummaryPayload> {
        val vouchers = voucherRepository.findVoucherSummariesByTenantId(currentTenantId())
        
        return vouchers.map { voucher ->
            VoucherSummaryPayload(
                id = voucher.id,
                number = voucher.getDisplayNumber(),
                date = voucher.date,
                description = voucher.description,
                postingCount = voucher.postings.size
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findVoucherById(id: Long): Voucher? {
        return voucherRepository.findByIdAndTenantIdWithPostings(id, currentTenantId())
    }

    // -------------------------------
    // Private Helper Methods
    // -------------------------------

    private fun generateNextVoucherNumber(tenantId: Long, date: LocalDate): Short {
        val maxNumber = voucherRepository.findMaxVoucherNumberForYear(tenantId, date.year)
        return (maxNumber + 1).toShort()
    }
    
    private fun validatePostingCommands(postings: List<CreatePostingPayload>) {
        if (postings.isEmpty()) throw EmptyPostingsException()
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
    
    private fun createVoucherEntity(payload: CreateVoucherPayload, tenantId: Long, voucherNumber: Short): Voucher {
        val voucher = Voucher()
        voucher.number = voucherNumber
        voucher.date = payload.date
        voucher.description = payload.description
        voucher.tenantId = tenantId
        return voucher
    }
    
    private fun createPostingEntity(
        postingData: CreatePostingPayload,
        tenantId: Long,
        voucherId: Long
    ): Posting {
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
        posting.voucherId = voucherId
        
        return posting
    }
}