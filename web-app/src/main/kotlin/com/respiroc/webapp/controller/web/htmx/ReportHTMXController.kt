package com.respiroc.webapp.controller.web.htmx

import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.payload.ProfitLossPayload
import com.respiroc.ledger.domain.model.AccountType
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.response.Callout
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal
import java.time.LocalDate

@Controller
@RequestMapping("/htmx/report")
class ReportHTMXController(
    private val postingApi: PostingInternalApi
) : BaseController() {

    @GetMapping("/trial-balance")
    @HxRequest
    fun trialBalanceHTMX(
        @RequestParam(name = "startDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,
        model: Model
    ): String {
        return try {
            val defaultStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val defaultEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

            val trialBalanceData = postingApi.getTrialBalance(defaultStartDate, defaultEndDate)

            model.addAttribute(userAttributeName, springUser())
            model.addAttribute("trialBalanceData", trialBalanceData)
            model.addAttribute("startDate", defaultStartDate)
            model.addAttribute("endDate", defaultEndDate)

            "report/trial-balance :: tableContent"
        } catch (e: Exception) {
            model.addAttribute(calloutAttributeName, Callout.Error("Error loading trial balance: ${e.message}"))
            "report/trial-balance :: error-message"
        }
    }

    @GetMapping(value = ["/profit-loss"])
    @HxRequest
    fun profitLossHTMX(
        @RequestParam(name = "startDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate?,
        @RequestParam(name = "endDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate?,
        model: Model
    ): String {
        return try {
            val defaultStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val defaultEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

            val postingsForProfitLoss = postingApi.getPostingsForProfitLoss(defaultStartDate, defaultEndDate)

            model.addAttribute(userAttributeName, springUser())
            model.addAttribute("startDate", defaultStartDate)
            model.addAttribute("endDate", defaultEndDate)
            model.addAttribute("assetPostings", postingsForProfitLoss[AccountType.ASSET] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))
            model.addAttribute("revenuePostings", postingsForProfitLoss[AccountType.REVENUE] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))
            model.addAttribute("operatingCostPostings", postingsForProfitLoss[AccountType.EXPENSE] ?: ProfitLossPayload(emptyList(), BigDecimal.ZERO))

            "report/profit-loss :: tableContent"
        } catch (e: Exception) {
            model.addAttribute(calloutAttributeName, Callout.Error("Error loading profit loss: ${e.message}"))
            return "report/profit-loss :: error-message"
        }
    }
}