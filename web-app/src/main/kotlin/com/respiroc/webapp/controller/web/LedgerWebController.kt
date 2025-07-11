package com.respiroc.webapp.controller.web

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.api.PostingInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.response.Callout
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
@RequestMapping(value = ["/ledger"])
class LedgerWebController(
    private val postingApi: PostingInternalApi,
    private val accountApi: AccountInternalApi
) : BaseController() {

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
        @RequestHeader(name = "HX-Request", required = false)
        isHtmxRequest: String?,
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

            if (isHtmxRequest != null) {
                model.addAttribute(userAttributeName, springUser())
                "ledger/general :: tableContent"
            } else {
                addCommonAttributes(model, "General Ledger")
                val accounts = accountApi.findAllAccounts().sortedBy { it.noAccountNumber }
                model.addAttribute("accounts", accounts)

                "ledger/general"
            }
        } catch (e: Exception) {
            if (isHtmxRequest != null) {
                model.addAttribute(calloutAttributeNames, Callout.Error("Error loading general ledger: ${e.message}"))
                "ledger/general :: error-message"
            } else {
                model.addAttribute(calloutAttributeNames, Callout.Error("Error loading general ledger: ${e.message}"))
                "ledger/general"
            }
        }
    }
} 