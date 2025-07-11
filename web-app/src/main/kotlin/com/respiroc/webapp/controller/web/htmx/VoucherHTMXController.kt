package com.respiroc.webapp.controller.web.htmx

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.util.currency.CurrencyService
import com.respiroc.util.exception.BaseException
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateVoucherRequest
import com.respiroc.webapp.controller.response.Callout
import com.respiroc.webapp.service.VoucherWebService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@Controller
@RequestMapping("/htmx/voucher")
class VoucherHTMXController(
    private val accountApi: AccountInternalApi,
    private val currencyService: CurrencyService,
    private val vatApi: VatInternalApi,
    private val companyApi: CompanyInternalApi,
    private val voucherWebService: VoucherWebService
) : BaseController() {

    @PostMapping("/create")
    @HxRequest
    fun createVoucherHTMX(
        @Valid @ModelAttribute createVoucherRequest: CreateVoucherRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            model.addAttribute(calloutAttributeName, Callout.Error(errorMessages))
            return "fragments/callout-message"
        }

        try {
            val callout = voucherWebService.processVoucherRequest(
                createVoucherRequest,
                user(),
                countryCode()
            )

            model.addAttribute(calloutAttributeName, callout)

            // Add clearForm attribute to trigger form reset in JavaScript for success case
            if (callout is Callout.Success) {
                model.addAttribute("clearForm", true)
            }

            return "fragments/callout-message"
        } catch (e: Exception) {
            model.addAttribute(calloutAttributeName, Callout.Error("Failed to create voucher: ${e.message}"))
            return "fragments/callout-message"
        }
    }

    @GetMapping("/add-posting-line")
    @HxRequest
    fun addPostingLineHTMX(
        @RequestParam(defaultValue = "0") rowCounter: Int,
        model: Model
    ): String {
        val accounts = accountApi.findAllAccounts()
        val vatCodes = vatApi.findAllVatCodes()
        val supportedCurrencies = currencyService.getSupportedCurrencies()
        val initialDate = LocalDate.now()

        model.addAttribute("rowCounter", rowCounter)
        model.addAttribute("accounts", accounts)
        model.addAttribute("vatCodes", vatCodes)
        model.addAttribute("companyCurrencyCode", countryCode())
        model.addAttribute("supportedCurrencies", supportedCurrencies)
        model.addAttribute("initialDate", initialDate)

        return "fragments/posting-line-row"
    }

    @PostMapping("/update-balance")
    @HxRequest
    fun updateBalanceHTMX(
        @ModelAttribute createVoucherRequest: CreateVoucherRequest,
        model: Model
    ): String {
        try {
            val companyCurrency = countryCode()

            // Calculate balance using the voucher request
            val (totalDebit, totalCredit, balance, isBalanced, hasValidEntries) = calculateBalance(
                createVoucherRequest,
                companyCurrency
            )

            // Simple balance calculation result
            model.addAttribute("totalDebit", "%.2f %s".format(totalDebit, companyCurrency))
            model.addAttribute("totalCredit", "%.2f %s".format(totalCredit, companyCurrency))
            model.addAttribute("balance", "%.2f %s".format(balance, companyCurrency))
            model.addAttribute("isBalanced", isBalanced)
            model.addAttribute("hasValidEntries", hasValidEntries)

            // Return simple balance row fragment
            return "fragments/balance-row-simple"
        } catch (_: Exception) {
            // Return original balance on error - use company currency or fallback
            val fallbackCurrency = try {
                countryCode()
            } catch (_: Exception) {
                "NOK"
            }
            model.addAttribute("totalDebit", "0.00 $fallbackCurrency")
            model.addAttribute("totalCredit", "0.00 $fallbackCurrency")
            model.addAttribute("balance", "0.00 $fallbackCurrency")
            return "fragments/balance-row-simple"
        }
    }

    // -------------------------------
    // Private Helper
    // -------------------------------

    private fun calculateBalance(createVoucherRequest: CreateVoucherRequest, companyCurrency: String): BalanceResult {
        var totalDebit = BigDecimal.ZERO
        var totalCredit = BigDecimal.ZERO
        var hasValidEntries = false

        // Use the posting lines from the request
        createVoucherRequest.postingLines.filterNotNull().forEach { posting ->
            // Check if this posting has valid data
            if (posting.amount != null && posting.amount > BigDecimal.ZERO &&
                (posting.debitAccount.isNotBlank() || posting.creditAccount.isNotBlank())
            ) {

                // Convert to company currency if needed
                val convertedAmount = if (posting.currency != companyCurrency) {
                    currencyService.convertCurrency(posting.amount, posting.currency, companyCurrency)
                } else {
                    posting.amount
                }

                // Add to totals based on account selection
                when {
                    posting.debitAccount.isNotBlank() && posting.creditAccount.isNotBlank() -> {
                        // Both debit and credit - self-balanced entry
                        totalDebit = totalDebit.add(convertedAmount)
                        totalCredit = totalCredit.add(convertedAmount)
                        hasValidEntries = true
                    }

                    posting.debitAccount.isNotBlank() -> {
                        // Only debit
                        totalDebit = totalDebit.add(convertedAmount)
                        hasValidEntries = true
                    }

                    posting.creditAccount.isNotBlank() -> {
                        // Only credit
                        totalCredit = totalCredit.add(convertedAmount)
                        hasValidEntries = true
                    }
                }
            }
        }

        val balance = totalDebit.subtract(totalCredit)
        val isBalanced = balance.abs() < BigDecimal("0.01")

        return BalanceResult(totalDebit, totalCredit, balance, isBalanced, hasValidEntries)
    }

    data class BalanceResult(
        val totalDebit: BigDecimal,
        val totalCredit: BigDecimal,
        val balance: BigDecimal,
        val isBalanced: Boolean,
        val hasValidEntries: Boolean
    )
} 