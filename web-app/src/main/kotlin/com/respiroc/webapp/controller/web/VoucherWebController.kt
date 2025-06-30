package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.VatInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.util.context.SpringUser
import com.respiroc.util.currency.CurrencyService
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreatePostingRequest
import com.respiroc.webapp.controller.request.CreateBatchPostingRequest
import com.respiroc.webapp.service.BatchPostingProcessingService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import jakarta.validation.Valid
import java.time.LocalDate
import java.math.BigDecimal

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
        return "voucher/index"
    }

    @PostMapping("/batch-postings")
    fun createBatchPostings(
        @Valid @ModelAttribute createBatchPostingRequest: CreateBatchPostingRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        return handleBatchPostingSubmission(createBatchPostingRequest, bindingResult, redirectAttributes, model, false)
    }

    @PostMapping("/batch-postings", headers = ["HX-Request"])
    fun createBatchPostingsHtmx(
        @Valid @ModelAttribute createBatchPostingRequest: CreateBatchPostingRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        return handleBatchPostingSubmission(createBatchPostingRequest, bindingResult, null, model, true)
    }

    // -------------------------------
    // Private Helper Methods
    // -------------------------------

    private fun setupModelAttributes(model: Model, springUser: SpringUser) {
        val companies = companyApi.findAllCompanyByUser((springUser.ctx))
        val accounts = accountApi.findAllAccounts()
        val vatCodes = vatApi.findAllVatCodes()
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val supportedCurrencies = currencyService.getSupportedCurrencies()

        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("title", "General Ledger")
        model.addAttribute("accounts", accounts)
        model.addAttribute("vatCodes", vatCodes)
        model.addAttribute("companyCurrency", companyCurrency)
        model.addAttribute("supportedCurrencies", supportedCurrencies)

        // Form objects
        model.addAttribute(
            "createPostingRequest", CreatePostingRequest(
                accountNumber = "",
                amount = BigDecimal.ZERO,
                currency = companyCurrency,
                postingDate = LocalDate.now(),
                description = null
            )
        )

        model.addAttribute(
            "createBatchPostingRequest", CreateBatchPostingRequest(postingLines = listOf())
        )
    }

    private fun handleBatchPostingSubmission(
        createBatchPostingRequest: CreateBatchPostingRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes?,
        model: Model,
        isHtmx: Boolean
    ): String {
        val springUser = springUser()
        model.addAttribute("user", springUser)

        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult, model, isHtmx)
        }

        val result = batchPostingProcessingService.processBatchPostingRequest(
            createBatchPostingRequest,
            springUser.ctx
        )

        return if (result.isSuccess) {
            handleSuccess(result.message, redirectAttributes, model, isHtmx)
        } else {
            handleError(result.message, model, isHtmx)
        }
    }

    private fun handleValidationErrors(
        bindingResult: BindingResult,
        model: Model,
        isHtmx: Boolean
    ): String {
        return if (isHtmx) {
            val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            model.addAttribute("errorMessage", errorMessages)
            "voucher/fragments :: messages"
        } else {
            val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            model.addAttribute("errorMessage", errorMessages)
            setupModelAttributes(model, springUser())
            "voucher/index"
        }
    }

    private fun handleSuccess(
        message: String,
        redirectAttributes: RedirectAttributes?,
        model: Model,
        isHtmx: Boolean
    ): String {
        return if (isHtmx) {
            model.addAttribute("successMessage", message)
            "voucher/fragments :: messages-and-refresh"
        } else {
            redirectAttributes?.addFlashAttribute("successMessage", message)
            "redirect:/voucher/new-advanced-voucher?tenantId=${TenantContextHolder.getTenantId()}"
        }
    }

    private fun handleError(
        message: String,
        model: Model,
        isHtmx: Boolean
    ): String {
        return if (isHtmx) {
            model.addAttribute("errorMessage", message)
            "voucher/fragments :: messages"
        } else {
            model.addAttribute("errorMessage", message)
            setupModelAttributes(model, springUser())
            "voucher/index"
        }
    }
} 