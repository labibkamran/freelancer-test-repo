package com.respiroc.webapp.service

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.VoucherInternalApi
import com.respiroc.ledger.api.payload.CreatePostingPayload
import com.respiroc.ledger.api.payload.CreateVoucherPayload
import com.respiroc.util.context.UserContext
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.request.CreateVoucherRequest
import com.respiroc.webapp.controller.request.PostingLine
import com.respiroc.webapp.controller.response.Callout
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class VoucherWebService(
    private val voucherApi: VoucherInternalApi,
    private val vatApi: VatInternalApi,
    private val currencyService: CurrencyService
) {

    companion object {
        private const val VAT_ACCOUNT_NUMBER = "2710"
    }

    fun processVoucherRequest(
        request: CreateVoucherRequest,
        userContext: UserContext,
        companyCurrencyCode: String
    ): Callout {
        return try {
            val validPostingLines = request.getValidPostingLines()
            val postingCommands = convertToPostingCommands(validPostingLines, companyCurrencyCode)

            val voucherPayload = CreateVoucherPayload(
                date = request.voucherDate,
                description = request.voucherDescription,
                postings = postingCommands
            )

            val result = voucherApi.createVoucher(voucherPayload)

            Callout.Success(
                message = "New voucher ${result.number} created successfully!",
                link = "/voucher/${result.id}?tenantId=${userContext.currentTenant!!.id}"
            )
        } catch (e: Exception) {
            Callout.Error(message = "Failed to save voucher: ${e.message}")
        }
    }

    fun updateVoucherWithPostings(
        voucherId: Long,
        request: CreateVoucherRequest,
        userContext: UserContext,
        companyCurrencyCode: String
    ): Callout {
        return try {
            val validPostingLines = request.getValidPostingLines()
            val postingCommands = convertToPostingCommands(validPostingLines, companyCurrencyCode)

            val result = voucherApi.updateVoucherWithPostings(voucherId, postingCommands)

            Callout.Success(
                message = "Voucher ${result.number} updated successfully!"
            )
        } catch (e: Exception) {
            Callout.Error(message = "Failed to update voucher: ${e.message}")
        }
    }

    private fun convertToPostingCommands(
        postingLines: List<PostingLine>,
        companyCurrency: String
    ): List<CreatePostingPayload> {
        return postingLines.flatMap { line ->
            val originalAmount = line.amount!!
            val originalCurrency = line.currency

            val hasDebit = line.debitAccount.isNotBlank()
            val hasCredit = line.creditAccount.isNotBlank()

            when {
                hasDebit && hasCredit -> {
                    // Both debit and credit filled - create two postings
                    val debitLine = line.copy(creditAccount = "", creditVatCode = null)
                    val creditLine = line.copy(debitAccount = "", debitVatCode = null)

                    val debitCommands =
                        createCommandsForLine(debitLine, originalAmount, originalCurrency, companyCurrency)
                    val creditCommands =
                        createCommandsForLine(creditLine, originalAmount, originalCurrency, companyCurrency)

                    debitCommands + creditCommands
                }

                hasDebit || hasCredit -> {
                    // Only one side filled
                    createCommandsForLine(line, originalAmount, originalCurrency, companyCurrency)
                }

                else -> {
                    emptyList()
                }
            }
        }
    }

    private fun createCommandsForLine(
        line: PostingLine,
        originalAmount: BigDecimal,
        originalCurrency: String,
        companyCurrency: String
    ): List<CreatePostingPayload> {
        val vatCode = line.getVatCode()

        return if (vatCode != null) {
            // Create two postings: one for base amount and one for VAT
            createVatPostings(line, originalAmount, originalCurrency, companyCurrency, vatCode)
        } else {
            // Create single posting without VAT
            listOf(createSinglePosting(line, originalAmount, originalCurrency, companyCurrency, null))
        }
    }

    private fun createVatPostings(
        line: PostingLine,
        originalAmount: BigDecimal,
        originalCurrency: String,
        companyCurrency: String,
        vatCode: String
    ): List<CreatePostingPayload> {
        val vatCodeEntity = vatApi.findVatCodeByCode(vatCode)
            ?: throw IllegalArgumentException("Invalid VAT code: $vatCode")

        val totalSignedAmount = calculateConvertedSignedAmount(line, originalCurrency, companyCurrency)
        val totalAbsAmount = totalSignedAmount.abs()

        val baseAmount = vatApi.calculateBaseAmountFromVatInclusive(totalAbsAmount, vatCodeEntity)
        val vatAmount = vatApi.calculateVatAmount(baseAmount, vatCodeEntity)

        val signedBaseAmount = if (totalSignedAmount < BigDecimal.ZERO) baseAmount.negate() else baseAmount
        val signedVatAmount = if (totalSignedAmount < BigDecimal.ZERO) vatAmount.negate() else vatAmount

        // Calculate original amounts if currency conversion is needed
        val (originalBaseAmount, originalVatAmount) = if (originalCurrency != companyCurrency) {
            val originalAbsAmount = originalAmount.abs()
            val originalBase = vatApi.calculateBaseAmountFromVatInclusive(originalAbsAmount, vatCodeEntity)
            val originalVat = vatApi.calculateVatAmount(originalBase, vatCodeEntity)

            val signedOriginalBase = if (originalAmount < BigDecimal.ZERO) originalBase.negate() else originalBase
            val signedOriginalVat = if (originalAmount < BigDecimal.ZERO) originalVat.negate() else originalVat

            Pair(signedOriginalBase, signedOriginalVat)
        } else {
            Pair(null, null)
        }

        val basePosting = CreatePostingPayload(
            accountNumber = line.getAccountNumber(),
            amount = signedBaseAmount,
            currency = companyCurrency,
            postingDate = line.postingDate,
            description = line.description,
            originalAmount = originalBaseAmount,
            originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
            vatCode = null
        )

        val vatPosting = CreatePostingPayload(
            accountNumber = VAT_ACCOUNT_NUMBER, // 2710 hard-coded
            amount = signedVatAmount,
            currency = companyCurrency,
            postingDate = line.postingDate,
            description = line.description,
            originalAmount = originalVatAmount,
            originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
            vatCode = vatCode
        )

        return listOf(basePosting, vatPosting)
    }

    private fun createSinglePosting(
        line: PostingLine,
        originalAmount: BigDecimal,
        originalCurrency: String,
        companyCurrency: String,
        vatCode: String?
    ): CreatePostingPayload {
        return CreatePostingPayload(
            accountNumber = line.getAccountNumber(),
            amount = calculateConvertedSignedAmount(line, originalCurrency, companyCurrency),
            currency = companyCurrency,
            postingDate = line.postingDate,
            description = line.description,
            originalAmount = if (originalCurrency != companyCurrency) originalAmount else null,
            originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null,
            vatCode = vatCode
        )
    }

    private fun calculateConvertedSignedAmount(
        line: PostingLine,
        originalCurrency: String,
        companyCurrency: String
    ): BigDecimal {
        val signedAmount = line.getSignedAmount()
        return if (originalCurrency == companyCurrency) {
            signedAmount
        } else {
            currencyService.convertCurrency(signedAmount, originalCurrency, companyCurrency)
        }
    }
} 
