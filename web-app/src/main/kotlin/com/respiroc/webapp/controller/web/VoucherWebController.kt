package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.util.context.SpringUser
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateBatchPostingRequest
import com.respiroc.webapp.service.BatchPostingProcessingService
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
    private val batchPostingProcessingService: BatchPostingProcessingService
) : BaseController() {

    @GetMapping(value = [])
    fun voucher(): String {
        return "redirect:/voucher/new-advanced-voucher?tenantId=${TenantContextHolder.getTenantId()}"
    }

    @GetMapping(value = ["/new-advanced-voucher"])
    fun new(model: Model): String {
        TenantContextHolder.getTenantId()
        val springUser = springUser()
        setupModelAttributes(model, springUser)
        return "voucher/advanced-voucher"
    }

    @PostMapping("/batch-postings")
    fun createBatchPostings(
        @Valid @ModelAttribute createBatchPostingRequest: CreateBatchPostingRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        val springUser = springUser()
        model.addAttribute("user", springUser)

        if (bindingResult.hasErrors()) {
            val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            model.addAttribute("errorMessage", errorMessages)
            return "voucher/fragments :: messages"
        }

        val result = batchPostingProcessingService.processBatchPostingRequest(
            createBatchPostingRequest,
            springUser.ctx
        )

        return if (result.isSuccess) {
            model.addAttribute("successMessage", result.message)
            "voucher/fragments :: messages-and-refresh"
        } else {
            model.addAttribute("errorMessage", result.message)
            "voucher/fragments :: messages"
        }
    }

    // -------------------------------
    // Private Helper Methods
    // -------------------------------

    private fun setupModelAttributes(model: Model, springUser: SpringUser) {
        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        val currentCompany = companies.find { it.tenantId == TenantContextHolder.getTenantId() }
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
            "createBatchPostingRequest", CreateBatchPostingRequest(postingLines = listOf())
        )
    }
} 