package com.respiroc.webapp.controller.web.htmx

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/htmx/company")
class CompanyHTMXController(
    private val companyApi: CompanyInternalApi,
    private val companyLookupApi: CompanyLookupInternalApi
) : BaseController() {

    @PostMapping("/create")
    @HxRequest
    fun createCompanyHTMX(
        @Valid @ModelAttribute createCompanyRequest: CreateCompanyRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                errorMessageAttributeName,
                "Please fill in all required fields correctly."
            )
            return "fragments/error-message"
        }

        try {
            val command = CreateCompanyCommand(
                name = createCompanyRequest.name,
                organizationNumber = createCompanyRequest.organizationNumber,
                countryCode = createCompanyRequest.countryCode
            )

            val company = companyApi.createNewCompany(command)
            return "redirect:htmx:/dashboard?tenantId=${company.tenantId}"

        } catch (e: Exception) {
            model.addAttribute(
                errorMessageAttributeName,
                "Failed to create company: ${e.message}"
            )
            return "fragments/error-message"
        }
    }

    @GetMapping("/search")
    @HxRequest
    fun searchCompaniesHTMX(
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