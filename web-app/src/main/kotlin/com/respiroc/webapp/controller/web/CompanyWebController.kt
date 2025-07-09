package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import com.respiroc.webapp.controller.response.Callout
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(value = ["/company"])
class CompanyWebController(
    private val companyApi: CompanyInternalApi,
    private val companyLookupApi: CompanyLookupInternalApi
) : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        addCommonAttributes(model, companyApi, "Create Company")
        model.addAttribute("createCompanyRequest", CreateCompanyRequest("", "", "NO"))
        return "company/create"
    }

    @PostMapping("/create")
    fun createCompany(
        @Valid @ModelAttribute createCompanyRequest: CreateCompanyRequest,
        bindingResult: BindingResult,
        model: Model
    ): Any {
        addCommonAttributes(model, companyApi, "Create Company")
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                calloutAttributeName, Callout.Error(
                    message = "Please fill in all required fields correctly."
                )
            )
            return "company/create"
        }

        try {
            val command = CreateCompanyCommand(
                name = createCompanyRequest.name,
                organizationNumber = createCompanyRequest.organizationNumber,
                countryCode = createCompanyRequest.countryCode
            )

            val company = companyApi.createNewCompany(command)

            val headers = HttpHeaders()
            headers.add("HX-Redirect", "/dashboard?tenantId=${company.tenantId}")

            return ResponseEntity<Void>(headers, HttpStatus.OK)

        } catch (e: Exception) {
            model.addAttribute(
                calloutAttributeName, Callout.Error(
                    message = "Failed to create company: ${e.message}"
                )
            )
            return "company/create"
        }
    }

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