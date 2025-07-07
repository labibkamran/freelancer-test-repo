package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.ledger.api.VoucherInternalApi
import com.respiroc.util.context.SpringUser
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateVoucherRequest
import com.respiroc.webapp.controller.response.Callout
import com.respiroc.webapp.controller.response.MessageType
import com.respiroc.webapp.service.VoucherWebService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

import jakarta.validation.Valid

@Controller
@RequestMapping(value = ["/voucher"])
class VoucherWebController(
    private val accountApi: AccountInternalApi,
    private val companyApi: CompanyInternalApi,
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
        val springUser = springUser()
        val vouchers = voucherApi.findAllVoucherSummaries()
        val companies = companyApi.findAllCompany()
        val currentCompany = companies.find { it.tenantId == tenantId() }
        
        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("currentCompany", currentCompany)
        model.addAttribute("vouchers", vouchers)
        model.addAttribute("title", "Voucher Overview")
        
        return "voucher/overview"
    }

    @GetMapping(value = ["/{voucherId}"])
    fun viewVoucher(@PathVariable voucherId: Long, model: Model): String {
        val springUser = springUser()
        val voucher = voucherApi.findVoucherById(voucherId)
        
        if (voucher == null) {
            model.addAttribute("errorMessage", "Voucher not found")
            return "error/404"
        }

        val companies = companyApi.findAllCompany()
        val currentCompany = companies.find { it.tenantId == tenantId() }

        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("currentCompany", currentCompany)
        model.addAttribute("voucher", voucher)
        model.addAttribute("title", "Voucher #${voucher.number}")
        
        return "voucher/view"
    }

    @GetMapping(value = ["/new-advanced-voucher"])
    fun new(model: Model): String {
        tenantId()
        val springUser = springUser()
        setupModelAttributes(model, springUser)
        return "voucher/advanced-voucher"
    }

    @PostMapping("/create-voucher")
    fun createVoucher(
        @Valid @ModelAttribute createVoucherRequest: CreateVoucherRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        val springUser = springUser()
        setupModelAttributes(model, springUser)

        if (bindingResult.hasErrors()) {
            val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            model.addAttribute("callout", Callout(errorMessages, MessageType.ERROR))
            return "voucher/advanced-voucher"
        }

        val callout = voucherWebService.processVoucherRequest(
            createVoucherRequest,
            springUser.ctx
        )

        model.addAttribute("callout", callout)
        if (callout.type == MessageType.SUCCESS) {
            model.addAttribute("clearForm", true)
        }
        return "voucher/advanced-voucher"
    }

    // -------------------------------
    // Private Helper Methods
    // -------------------------------

    private fun setupModelAttributes(model: Model, springUser: SpringUser) {
        val companies = companyApi.findAllCompany()
        val currentCompany = companies.find { it.tenantId == tenantId() }
        val accounts = accountApi.findAllAccounts()
        val vatCodes = vatApi.findAllVatCodes()
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val supportedCurrencies = currencyService.getSupportedCurrencies()

        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("currentCompany", currentCompany)
        model.addAttribute("title", "General Ledger")
        model.addAttribute("accounts", accounts)
        model.addAttribute("vatCodes", vatCodes)
        model.addAttribute("companyCurrency", companyCurrency)
        model.addAttribute("supportedCurrencies", supportedCurrencies)

        // Form objects
        model.addAttribute(
            "createVoucherRequest", CreateVoucherRequest(postingLines = listOf())
        )
    }
} 