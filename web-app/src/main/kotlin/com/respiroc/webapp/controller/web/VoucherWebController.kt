package com.respiroc.webapp.controller.web

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.VoucherInternalApi
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateVoucherRequest
import com.respiroc.webapp.controller.response.Callout
import com.respiroc.webapp.service.VoucherWebService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(value = ["/voucher"])
class VoucherWebController(
    private val accountApi: AccountInternalApi,
    private val currencyService: CurrencyService,
    private val vatApi: VatInternalApi,
    private val voucherApi: VoucherInternalApi,
    private val voucherWebService: VoucherWebService
) : BaseController() {

    @GetMapping(value = [])
    fun voucher(): String {
        return "redirect:/voucher/overview?tenantId=${tenantId()}"
    }

    @GetMapping(value = ["/overview"])
    fun overview(model: Model): String {
        val vouchers = voucherApi.findAllVoucherSummaries()
        addCommonAttributes(model, "Voucher Overview")
        model.addAttribute("vouchers", vouchers)
        return "voucher/overview"
    }

    @GetMapping(value = ["/{voucherId}"])
    fun viewVoucher(@PathVariable voucherId: Long, model: Model): String {
        val voucher = voucherApi.findVoucherById(voucherId)
        if (voucher == null) {
            model.addAttribute("errorMessage", "Voucher not found")
            return "error/404"
        }
        addCommonAttributes(model, "Voucher ${voucher.getDisplayNumber()}")
        model.addAttribute("voucher", voucher)

        return "voucher/view"
    }

    @GetMapping(value = ["/new-advanced-voucher"])
    fun new(model: Model): String {
        tenantId()
        setupModelAttributes(model)
        return "voucher/advanced-voucher"
    }

    @PostMapping("/create-voucher")
    fun createVoucher(
        @Valid @ModelAttribute createVoucherRequest: CreateVoucherRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        setupModelAttributes(model)

        if (bindingResult.hasErrors()) {
            val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            model.addAttribute(calloutAttributeName, Callout.Error(errorMessages))
            return "voucher/advanced-voucher"
        }

        val callout = voucherWebService.processVoucherRequest(
            createVoucherRequest,
            user()
        )

        model.addAttribute(calloutAttributeName, callout)
        if (callout is Callout.Success)
            model.addAttribute("clearForm", true)
        return "voucher/advanced-voucher"
    }

    // -------------------------------
    // Private Helper Methods
    // -------------------------------

    private fun setupModelAttributes(model: Model) {
        val accounts = accountApi.findAllAccounts()
        val vatCodes = vatApi.findAllVatCodes()
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val supportedCurrencies = currencyService.getSupportedCurrencies()

        addCommonAttributes(model, "General Ledger")
        model.addAttribute("accounts", accounts)
        model.addAttribute("vatCodes", vatCodes)
        model.addAttribute("companyCurrency", companyCurrency)
        model.addAttribute("supportedCurrencies", supportedCurrencies)
    }
} 