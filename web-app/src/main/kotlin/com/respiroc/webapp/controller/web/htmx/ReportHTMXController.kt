package com.respiroc.webapp.controller.web.htmx

import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.response.Callout
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
            val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

            val trialBalanceData = postingApi.getTrialBalance(effectiveStartDate, effectiveEndDate)

            model.addAttribute(userAttributeName, springUser())
            model.addAttribute("trialBalanceData", trialBalanceData)
            model.addAttribute("startDate", effectiveStartDate)
            model.addAttribute("endDate", effectiveEndDate)

            "report/trial-balance :: tableContent"
        } catch (e: Exception) {
            model.addAttribute(calloutAttributeName, Callout.Error("Error loading trial balance: ${e.message}"))
            "report/trial-balance :: error-message"
        }
    }
}