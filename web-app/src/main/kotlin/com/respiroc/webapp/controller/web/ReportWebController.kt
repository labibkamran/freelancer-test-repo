package com.respiroc.webapp.controller.web

import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.payload.ProfitLossPayload
import com.respiroc.ledger.domain.model.AccountType
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.response.Callout
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
import java.time.LocalDate

@Controller
@RequestMapping(value = ["/report"])
class ReportWebController(private val postingApi: PostingInternalApi) : BaseController() {

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
        // Default to current month if no dates provided
        val defaultStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val defaultEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        val trialBalanceData = postingApi.getTrialBalance(defaultStartDate, defaultEndDate)

        addCommonAttributes(model, "Trial Balance")
        model.addAttribute("trialBalanceData", trialBalanceData)
        model.addAttribute("startDate", defaultStartDate)
        model.addAttribute("endDate", defaultEndDate)

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
            // Default to current month if no dates provided
            val defaultStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val defaultEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

            val trialBalanceData = postingApi.getTrialBalance(defaultStartDate, defaultEndDate)

            model.addAttribute(userAttributeName, springUser())
            model.addAttribute("trialBalanceData", trialBalanceData)
            model.addAttribute("startDate", defaultStartDate)
            model.addAttribute("endDate", defaultEndDate)

            "report/trial-balance :: tableContent"
        } catch (e: Exception) {
            logger.error("Error loading trial balance data via HTMX", e)
            model.addAttribute(calloutAttributeName, Callout.Error("Error loading trial balance: ${e.message}"))
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

        val defaultStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val defaultEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

        val postingsForProfitLoss = postingApi.getPostingsForProfitLoss(defaultStartDate, defaultEndDate)

        model.addAttribute("user", springUser)
        model.addAttribute("startDate", defaultStartDate)
        model.addAttribute("endDate", defaultEndDate)
        model.addAttribute("assetPostings", postingsForProfitLoss[AccountType.ASSET] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))
        model.addAttribute("revenuePostings", postingsForProfitLoss[AccountType.REVENUE] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))
        model.addAttribute("operatingCostPostings", postingsForProfitLoss[AccountType.EXPENSE] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))

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

        val defaultStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val defaultEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

        val postingsForProfitLoss = postingApi.getPostingsForProfitLoss(defaultStartDate, defaultEndDate)

        model.addAttribute("user", springUser)
        model.addAttribute("startDate", defaultStartDate)
        model.addAttribute("endDate", defaultEndDate)
        model.addAttribute("assetPostings", postingsForProfitLoss[AccountType.ASSET] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))
        model.addAttribute("revenuePostings", postingsForProfitLoss[AccountType.REVENUE] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))
        model.addAttribute("operatingCostPostings", postingsForProfitLoss[AccountType.EXPENSE] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))

         "report/profit-loss :: tableContent"
        } catch (e: Exception) {
            logger.error("Error loading profit loss data via HTMX", e)
            model.addAttribute(calloutAttributeName, Callout.Error("Error loading profit loss: ${e.message}"))
            return "report/profit-loss :: error-message"
        }
    }
}