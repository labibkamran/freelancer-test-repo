package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.util.currency.CurrencyService
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
import org.springframework.http.ResponseEntity
import jakarta.validation.Valid
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDate
import java.math.BigDecimal

@Controller
@RequestMapping(value = ["/voucher", "/vouchers"])
class VoucherWebController(
    private val postingApi: PostingInternalApi,
    private val accountApi: AccountInternalApi,
    private val companyApi: CompanyInternalApi,
    private val currencyService: CurrencyService
) : BaseController() {

    @GetMapping(value = [])
    fun voucher(): String {
        return "redirect:/new-advanced-voucher"
    }

    @GetMapping(value = ["/new-advanced-voucher"])
    fun new(model: Model): String {
        val springUser = springUser()
        model.addAttribute("user", springUser)

        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        model.addAttribute("companies", companies)

        model.addAttribute("title", "General Ledger")
        
        // Get all accounts for dropdown
        val accounts = accountApi.findAllAccounts()
        model.addAttribute("accounts", accounts)
        
        // Get company currency based on country (assume NO for now)
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        model.addAttribute("companyCurrency", companyCurrency)
        
        // Get supported currencies
        val supportedCurrencies = currencyService.getSupportedCurrencies()
        model.addAttribute("supportedCurrencies", supportedCurrencies)
        
        // Create empty form object for legacy single posting
        val createRequest = CreatePostingRequest(
            accountNumber = "",
            amount = BigDecimal.ZERO,
            currency = companyCurrency,
            postingDate = LocalDate.now(),
            description = null
        )
        model.addAttribute("createPostingRequest", createRequest)

        // Create empty form object for batch posting
        val batchRequest = CreateBatchPostingRequest(
            postingLines = listOf(),
            entries = listOf()
        )
        model.addAttribute("createBatchPostingRequest", batchRequest)

        return "voucher/index"
    }

    @GetMapping("/currency-rates")
    @ResponseBody
    fun getCurrencyRates(@RequestParam currencies: List<String>): ResponseEntity<Map<String, BigDecimal>> {
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val rates = currencies.associate { currency ->
            currency to if (currency == companyCurrency) BigDecimal.ONE 
                        else currencyService.convertCurrency(BigDecimal.ONE, currency, companyCurrency)
        }
        return ResponseEntity.ok(rates)
    }

    @GetMapping("/convert-amount")
    @ResponseBody
    fun convertAmount(
        @RequestParam amount: BigDecimal,
        @RequestParam fromCurrency: String,
        @RequestParam(required = false) toCurrency: String?
    ): ResponseEntity<Map<String, Any>> {
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val targetCurrency = toCurrency ?: companyCurrency
        
        val convertedAmount = if (fromCurrency == targetCurrency) {
            amount
        } else {
            currencyService.convertCurrency(amount, fromCurrency, targetCurrency)
        }
        
        val response = mapOf(
            "originalAmount" to amount,
            "originalCurrency" to fromCurrency,
            "convertedAmount" to convertedAmount,
            "convertedCurrency" to targetCurrency,
            "rate" to if (fromCurrency == targetCurrency) BigDecimal.ONE 
                     else currencyService.convertCurrency(BigDecimal.ONE, fromCurrency, targetCurrency)
        )
        
        return ResponseEntity.ok(response)
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

        // Get company currency
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        
        // Get all posting entries from both journal entries and legacy entries
        val allPostingEntries = createBatchPostingRequest.getAllPostingEntries()

        // Enhanced backend validation - no global date validation needed

        // Validate that we have at least one valid entry
        if (allPostingEntries.isEmpty()) {
            bindingResult.reject("entries.empty", "At least one posting entry is required")
        }

        // Enhanced validation for each entry
        allPostingEntries.forEachIndexed { index: Int, entry: PostingEntry ->
            if (entry.accountNumber.isBlank()) {
                bindingResult.rejectValue("entries[$index].accountNumber", "error.accountNumber", "Account number is required")
            }

            if (entry.amount == null || entry.amount <= BigDecimal.ZERO) {
                bindingResult.rejectValue("entries[$index].amount", "error.amount", "Amount must be greater than zero")
            }

            if (!currencyService.isCurrencySupported(entry.currency)) {
                bindingResult.rejectValue("entries[$index].currency", "error.currency", "Unsupported currency: ${entry.currency}")
            }
        }

        if (!createBatchPostingRequest.validate()) {
            bindingResult.reject("batch.invalid", "Debit and credit amounts must be equal")
        }

        if (bindingResult.hasErrors()) {
            if (isHtmx) {
                val errorMessages = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
                model.addAttribute("errorMessage", errorMessages)
                return "voucher/fragments :: messages"
            } else {
                model.addAttribute("title", "General Ledger")
                val accounts = accountApi.findAllAccounts()
                model.addAttribute("accounts", accounts)
                return "voucher/index"
            }
        }

        // Validate accounts exist
        allPostingEntries.forEach { entry ->
            if (!accountApi.accountExists(entry.accountNumber)) {
                bindingResult.rejectValue("entries", "error.accountNumber", "Account ${entry.accountNumber} not found")
                if (isHtmx) {
                    model.addAttribute("errorMessage", "Account ${entry.accountNumber} not found")
                    return "voucher/fragments :: messages"
                } else {
                    model.addAttribute("title", "General Ledger")
                    val accounts = accountApi.findAllAccounts()
                    model.addAttribute("accounts", accounts)
                    return "voucher/index"
                }
            }
        }

        try {
            // Convert to PostingCommand objects with currency conversion
            val postingDataList = allPostingEntries.map { entry ->
                val originalAmount = entry.amount!!
                val originalCurrency = entry.currency
                
                // For PostingLine entries, get the posting date from the posting line
                val postingLines = createBatchPostingRequest.postingLines.filterNotNull()
                val matchingPostingLine = postingLines.find { line -> 
                    line.getAccountNumber() == entry.accountNumber && 
                    line.amount == entry.amount &&
                    line.getAccountType() == entry.type
                }
                val postingDate = matchingPostingLine?.postingDate ?: LocalDate.now()
                
                // Convert to company currency if different
                val convertedAmount = if (originalCurrency == companyCurrency) {
                    originalAmount
                } else {
                    currencyService.convertCurrency(originalAmount, originalCurrency, companyCurrency)
                }
                
                CreatePostingCommand(
                    accountNumber = entry.accountNumber,
                    amount = entry.getSignedAmount().let { 
                        if (originalCurrency == companyCurrency) it 
                        else currencyService.convertCurrency(it, originalCurrency, companyCurrency)
                    },
                    currency = companyCurrency,
                    postingDate = postingDate,
                    description = entry.description,
                    originalAmount = if (originalCurrency != companyCurrency) originalAmount else null,
                    originalCurrency = if (originalCurrency != companyCurrency) originalCurrency else null
                )
            }

            postingApi.createBatchPostings(postingDataList, springUser.ctx)

            if (isHtmx) {
                model.addAttribute("successMessage", "Journal entry saved successfully!")
                return "voucher/fragments :: messages-and-refresh"
            } else {
                redirectAttributes?.addFlashAttribute("successMessage", "Batch postings created successfully!")
            }
        } catch (e: Exception) {
            if (isHtmx) {
                model.addAttribute("errorMessage", "Failed to save journal entry: ${e.message}")
                return "voucher/fragments :: messages"
            } else {
                redirectAttributes?.addFlashAttribute("errorMessage", "Failed to create batch postings: ${e.message}")
            }
        }

        return if (isHtmx) "voucher/fragments :: messages" else "redirect:/voucher/"
    }
} 