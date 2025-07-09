package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.domain.model.AccountType
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
@RequestMapping(value = ["/report"])
class ReportWebController(
    private val postingApi: PostingInternalApi,
    private val companyApi: CompanyInternalApi
) : BaseController() {

    private val logger = LoggerFactory.getLogger(ReportWebController::class.java)

    @GetMapping(value = ["/trial-balance"])
    fun trialBalance(
        @RequestParam(name = "startDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        endDate: LocalDate?,
        model: Model
    ): String {
        val springUser = springUser()
        
        // Default to current month if no dates provided
        val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        
        val trialBalanceData = postingApi.getTrialBalance(effectiveStartDate, effectiveEndDate)
        val companies = companyApi.findAllCompany()
        val currentCompany = companies.find { it.tenantId == tenantId() }
        
        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("currentCompany", currentCompany)
        model.addAttribute("title", "Trial Balance")
        model.addAttribute("trialBalanceData", trialBalanceData)
        model.addAttribute("startDate", effectiveStartDate)
        model.addAttribute("endDate", effectiveEndDate)
        
        return "report/trial-balance"
    }
    
    @GetMapping(value = ["/trial-balance"], headers = ["HX-Request"])
    fun trialBalanceHtmx(
        @RequestParam(name = "startDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        endDate: LocalDate?,
        model: Model
    ): String {
        return try {
            val springUser = springUser()
            
            // Default to current month if no dates provided
            val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            
            val trialBalanceData = postingApi.getTrialBalance(effectiveStartDate, effectiveEndDate)
            
            model.addAttribute("user", springUser)
            model.addAttribute("trialBalanceData", trialBalanceData)
            model.addAttribute("startDate", effectiveStartDate)
            model.addAttribute("endDate", effectiveEndDate)
            
            "report/trial-balance :: tableContent"
        } catch (e: Exception) {
            logger.error("Error loading trial balance data via HTMX", e)
            model.addAttribute("callout", Callout("Error loading trial balance: ${e.message}", MessageType.ERROR))
            return "report/trial-balance :: error-message"
        }
    }

    @GetMapping(value = ["/profit-loss"])
    fun profitLoss(
        @RequestParam(name = "startDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,
        model: Model
    ): String {
        val springUser = springUser()

        val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

        val assetPostings = postingApi.getPostingsForProfitLoss(AccountType.ASSET, effectiveStartDate, effectiveEndDate)
        val revenuePostings = postingApi.getPostingsForProfitLoss(AccountType.REVENUE, effectiveStartDate, effectiveEndDate)
        val operatingCostPostings = postingApi.getPostingsForProfitLoss(AccountType.EXPENSE, effectiveStartDate, effectiveEndDate)


        model.addAttribute("user", springUser)
        model.addAttribute("startDate", effectiveStartDate)
        model.addAttribute("endDate", effectiveEndDate)
        model.addAttribute("assetPostings", assetPostings)
        model.addAttribute("revenuePostings", revenuePostings)
        model.addAttribute("operatingCostPostings", operatingCostPostings)

        return "report/profit-loss"
    }

    @GetMapping(value = ["/profit-loss"], headers = ["HX-Request"])
    fun profitLossHtmx(
        @RequestParam(name = "startDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,
        model: Model
    ): String {
        return try {
        val springUser = springUser()

        val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())


        val assetPostings = postingApi.getPostingsForProfitLoss(AccountType.ASSET, effectiveStartDate, effectiveEndDate)
        val revenuePostings = postingApi.getPostingsForProfitLoss(AccountType.REVENUE, effectiveStartDate, effectiveEndDate)
        val operatingCostPostings = postingApi.getPostingsForProfitLoss(AccountType.EXPENSE, effectiveStartDate, effectiveEndDate)


        model.addAttribute("user", springUser)
        model.addAttribute("startDate", effectiveStartDate)
        model.addAttribute("endDate", effectiveEndDate)
        model.addAttribute("assetPostings", assetPostings)
        model.addAttribute("revenuePostings", revenuePostings)
        model.addAttribute("operatingCostPostings", operatingCostPostings)

         "report/profit-loss :: tableContent"
        } catch (e: Exception) {
            logger.error("Error loading profit loss data via HTMX", e)
            model.addAttribute("callout", Callout("Error loading profit loss: ${e.message}", MessageType.ERROR))
            return "report/profit-loss :: error-message"
        }
    }
}