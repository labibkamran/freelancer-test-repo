package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.application.PostingService
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.response.MessageType
import com.respiroc.webapp.controller.response.Callout
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
@RequestMapping(value = ["/ledger"])
class LedgerWebController(
    private val postingApi: PostingInternalApi,
    private val accountApi: AccountInternalApi,
    private val companyApi: CompanyInternalApi
) : BaseController() {

    private val logger = LoggerFactory.getLogger(LedgerWebController::class.java)

    @GetMapping(value = ["/general"])
    fun generalLedger(
        @RequestParam(name = "startDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        endDate: LocalDate?,
        @RequestParam(name = "accountNumber", required = false) 
        accountNumber: String?,
        model: Model
    ): String {
        val springUser = springUser()
        
        // Default to current month if no dates provided
        val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        
        val generalLedgerData = postingApi.getGeneralLedger(effectiveStartDate, effectiveEndDate, accountNumber)
        val accounts = accountApi.findAllAccounts().sortedBy { it.noAccountNumber }
        val companies = companyApi.findAllCompany()
        val currentCompany = companies.find { it.tenantId == tenantId() }
        
        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("currentCompany", currentCompany)
        model.addAttribute("title", "General Ledger")
        model.addAttribute("generalLedgerData", generalLedgerData)
        model.addAttribute("accounts", accounts)
        model.addAttribute("startDate", effectiveStartDate)
        model.addAttribute("endDate", effectiveEndDate)
        model.addAttribute("selectedAccountNumber", accountNumber)
        
        return "ledger/general"
    }
    
    @GetMapping(value = ["/general"], headers = ["HX-Request"])
    fun generalLedgerHtmx(
        @RequestParam(name = "startDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        endDate: LocalDate?,
        @RequestParam(name = "accountNumber", required = false) 
        accountNumber: String?,
        model: Model
    ): String {
        return try {
            val springUser = springUser()
            
            // Default to current month if no dates provided
            val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            
            val generalLedgerData = postingApi.getGeneralLedger(effectiveStartDate, effectiveEndDate, accountNumber)
            
            model.addAttribute("user", springUser)
            model.addAttribute("generalLedgerData", generalLedgerData)
            model.addAttribute("startDate", effectiveStartDate)
            model.addAttribute("endDate", effectiveEndDate)
            model.addAttribute("selectedAccountNumber", accountNumber)
            
            "ledger/general :: tableContent"
        } catch (e: Exception) {
            logger.error("Error loading general ledger data via HTMX", e)
            model.addAttribute("callout", Callout("Error loading general ledger: ${e.message}", MessageType.ERROR))
            return "ledger/general :: error-message"
        }
    }
} 