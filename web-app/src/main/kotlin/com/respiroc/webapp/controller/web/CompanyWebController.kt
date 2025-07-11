package com.respiroc.webapp.controller.web

import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(value = ["/company"])
class CompanyWebController(private val companyLookupApi: CompanyLookupInternalApi) : BaseController() {

    @GetMapping("/search")
    fun searchCompanies(
        @RequestParam name: String,
        @RequestParam(defaultValue = "NO") countryCode: String,
        model: Model
    ): String {
        val query = name.trim()

        if (query.length < 2) {
            return "fragments/company-search :: empty"
        }

        try {
            val searchResult = companyLookupApi.search(query, countryCode)
            model.addAttribute("companies", searchResult.companies.take(10))
            return "fragments/company-search :: results"
        } catch (e: Exception) {
            model.addAttribute(errorMessageAttributeName, "Search failed: ${e.message}")
            return "fragments/company-search :: error"
        }
    }
}