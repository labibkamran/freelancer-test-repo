package com.respiroc.webapp.controller.web

import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.ledger.api.payload.ProfitLossPayload
import com.respiroc.ledger.domain.model.AccountType
import com.respiroc.webapp.controller.BaseController
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
class ReportWebController(
    private val postingApi: PostingInternalApi
) : BaseController() {

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
        val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
        val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        val trialBalanceData = postingApi.getTrialBalance(effectiveStartDate, effectiveEndDate)

        addCommonAttributes(model, "Trial Balance")
        model.addAttribute("trialBalanceData", trialBalanceData)
        model.addAttribute("startDate", effectiveStartDate)
        model.addAttribute("endDate", effectiveEndDate)

        return "report/trial-balance"
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
} 