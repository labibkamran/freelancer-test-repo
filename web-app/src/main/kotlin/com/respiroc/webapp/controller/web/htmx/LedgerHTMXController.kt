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
@RequestMapping("/htmx/ledger")
class LedgerHTMXController(
    private val postingApi: PostingInternalApi
) : BaseController() {

    @GetMapping("/general")
    @HxRequest
    fun generalLedgerHTMX(
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
            val effectiveStartDate = startDate ?: LocalDate.now().withDayOfMonth(1)
            val effectiveEndDate = endDate ?: LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

            val generalLedgerData = postingApi.getGeneralLedger(effectiveStartDate, effectiveEndDate, accountNumber)

            model.addAttribute("generalLedgerData", generalLedgerData)
            model.addAttribute("startDate", effectiveStartDate)
            model.addAttribute("endDate", effectiveEndDate)
            model.addAttribute("selectedAccountNumber", accountNumber)
            model.addAttribute(userAttributeName, springUser())
            model.addAttribute("companyCurrency", countryCode())

            "ledger/general :: tableContent"
        } catch (e: Exception) {
            model.addAttribute(calloutAttributeName, Callout.Error("Error loading general ledger: ${e.message}"))
            "ledger/general :: error-message"
        }
    }
} 