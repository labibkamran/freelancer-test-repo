package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.tenant.infrastructure.context.TenantContextHolder
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreatePostingRequest
import com.respiroc.webapp.controller.request.CreateBatchPostingRequest
import com.respiroc.webapp.controller.request.PostingEntry
import com.respiroc.ledger.api.command.CreatePostingCommand
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import jakarta.validation.Valid
import java.time.LocalDate
import java.math.BigDecimal

@Controller
@RequestMapping("/dashboard/ledger")
class WebLedgerController(
    private val postingApi: PostingInternalApi,
    private val accountApi: AccountInternalApi,
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping(value = [])
    fun ledger(model: Model): String {
        val springUser = springUser()
        model.addAttribute("user", springUser)

        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        model.addAttribute("companies", companies)

        model.addAttribute("title", "General Ledger")
        
        // Get all accounts for dropdown
        val accounts = accountApi.findAllAccounts()
        model.addAttribute("accounts", accounts)
        
        // Create empty form object for legacy single posting
        val createRequest = CreatePostingRequest(
            accountNumber = "",
            amount = BigDecimal.ZERO,
            currency = "NOK",
            postingDate = LocalDate.now(),
            description = null
        )
        model.addAttribute("createPostingRequest", createRequest)

        // Create empty form object for batch posting
        val batchRequest = CreateBatchPostingRequest(
            postingDate = LocalDate.now(),
            currency = "NOK",
            description = null,
            entries = listOf()
        )
        model.addAttribute("createBatchPostingRequest", batchRequest)

        return "ledger/index"
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
    // Private Helper
    // -------------------------------

    private fun handleBatchPostingSubmission(
        createBatchPostingRequest: CreateBatchPostingRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes?,
        model: Model,
        isHtmx: Boolean
    ): String {
        val springUser = springUser()
        model.addAttribute("user", springUser)

        // Filter valid entries first
        val validEntries = createBatchPostingRequest.entries.filterNotNull().filter { it.isValid() }

        // Enhanced backend validation
        if (createBatchPostingRequest.postingDate == null) {
            bindingResult.rejectValue("postingDate", "error.postingDate", "Posting date is required")
        }

        if (createBatchPostingRequest.currency.isBlank()) {
            bindingResult.rejectValue("currency", "error.currency", "Currency is required")
        }

        // Validate that we have at least one valid entry
        if (validEntries.isEmpty()) {
            bindingResult.reject("entries.empty", "At least one posting entry is required")
        }

        // Enhanced validation for each entry
        validEntries.forEachIndexed { index: Int, entry: PostingEntry ->
            if (entry.accountNumber.isBlank()) {
                bindingResult.rejectValue("entries[$index].accountNumber", "error.accountNumber", "Account number is required")
            }

            val hasDebit = entry.debitAmount != null && entry.debitAmount > BigDecimal.ZERO
            val hasCredit = entry.creditAmount != null && entry.creditAmount > BigDecimal.ZERO

            if (!hasDebit && !hasCredit) {
                bindingResult.rejectValue("entries[$index]", "error.amount", "Either debit or credit amount is required")
            }

            if (hasDebit && hasCredit) {
                bindingResult.rejectValue("entries[$index]", "error.both", "Cannot have both debit and credit amounts")
            }
        }

        if (!createBatchPostingRequest.validate()) {
            bindingResult.reject("batch.invalid", "Debit and credit amounts must be equal")
        }

        if (bindingResult.hasErrors()) {
            if (isHtmx) {
                val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
                model.addAttribute("errorMessage", errorMessages)
                return "ledger/fragments :: messages"
            } else {
                model.addAttribute("title", "General Ledger")
                val accounts = accountApi.findAllAccounts()
                model.addAttribute("accounts", accounts)
                return "ledger/index"
            }
        }

        // Validate accounts exist

        validEntries.forEach { entry ->
            if (!accountApi.accountExists(entry.accountNumber)) {
                bindingResult.rejectValue("entries", "error.accountNumber", "Account ${entry.accountNumber} not found")
                if (isHtmx) {
                    model.addAttribute("errorMessage", "Account ${entry.accountNumber} not found")
                    return "ledger/fragments :: messages"
                } else {
                    model.addAttribute("title", "General Ledger")
                    val accounts = accountApi.findAllAccounts()
                    model.addAttribute("accounts", accounts)
                    return "ledger/index"
                }
            }
        }

        try {
            // Convert to PostingCommand objects
            val postingDataList = validEntries.map { entry ->
                CreatePostingCommand(
                    accountNumber = entry.accountNumber,
                    amount = entry.getAmount(),
                    currency = createBatchPostingRequest.currency,
                    postingDate = createBatchPostingRequest.postingDate!!,
                    description = entry.description
                )
            }

            postingApi.createBatchPostings(postingDataList, springUser.ctx)

            if (isHtmx) {
                model.addAttribute("successMessage", "Journal entry saved successfully!")
                return "ledger/fragments :: messages-and-refresh"
            } else {
                redirectAttributes?.addFlashAttribute("successMessage", "Batch postings created successfully!")
            }
        } catch (e: Exception) {
            if (isHtmx) {
                model.addAttribute("errorMessage", "Failed to save journal entry: ${e.message}")
                return "ledger/fragments :: messages"
            } else {
                redirectAttributes?.addFlashAttribute("errorMessage", "Failed to create batch postings: ${e.message}")
            }
        }

        return if (isHtmx) "ledger/fragments :: messages" else "redirect:/dashboard/ledger"
    }
} 