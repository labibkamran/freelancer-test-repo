package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.VoucherInternalApi
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/voucher"])
class VoucherWebController(
    private val accountApi: AccountInternalApi,
    private val companyApi: CompanyInternalApi,
    private val currencyService: CurrencyService,
    private val vatApi: VatInternalApi,
    private val voucherApi: VoucherInternalApi
) : BaseController() {

    @GetMapping(value = [])
    fun voucher(): String {
        return "redirect:/voucher/overview?tenantId=${tenantId()}"
    }

    @GetMapping(value = ["/overview"])
    fun overview(model: Model): String {
        val vouchers = voucherApi.findAllVoucherSummaries()
        addCommonAttributes(model, companyApi, "Voucher Overview")
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
        addCommonAttributes(model, companyApi, "Voucher ${voucher.getDisplayNumber()}")
        model.addAttribute("voucher", voucher)

        return "voucher/view"
    }

    @GetMapping(value = ["/new-advanced-voucher"])
    fun new(model: Model): String {
        tenantId()
        setupModelAttributes(model)
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

        addCommonAttributes(model, companyApi, "General Ledger")
        model.addAttribute("accounts", accounts)
        model.addAttribute("vatCodes", vatCodes)
        model.addAttribute("companyCurrency", companyCurrency)
        model.addAttribute("supportedCurrencies", supportedCurrencies)
    }
} 