package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import com.respiroc.webapp.controller.response.Callout
import com.respiroc.webapp.controller.response.MessageType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@Controller
@RequestMapping(value = ["/company"])
class CompanyWebController(
    private val companyApi: CompanyInternalApi,
    private val companyLookupApi: CompanyLookupInternalApi
) : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        val springUser = springUser()
        val companies = companyApi.findAllCompany()

        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("title", "Create Company")
        model.addAttribute("createCompanyRequest", CreateCompanyRequest("", "", "NO"))

        return "company/create"
    }

    @PostMapping("/create")
    fun createCompany(
        @Valid @ModelAttribute createCompanyRequest: CreateCompanyRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        val springUser = springUser()
        val companies = companyApi.findAllCompany()

        model.addAttribute("user", springUser)
        model.addAttribute("companies", companies)
        model.addAttribute("title", "Create Company")

        if (bindingResult.hasErrors()) {
            model.addAttribute("callout", Callout(
                message = "Please fill in all required fields correctly.",
                type = MessageType.ERROR
            ))
            return "company/create"
        }

        try {
            val command = CreateCompanyCommand(
                name = createCompanyRequest.name,
                organizationNumber = createCompanyRequest.organizationNumber,
                countryCode = createCompanyRequest.countryCode
            )
            
            val company = companyApi.createNewCompany(command)
            
            model.addAttribute("callout", Callout(
                message = "Company '${company.name}' has been created successfully! Click here to go to dashboard.",
                type = MessageType.SUCCESS,
                link = "/dashboard?tenantId=${company.tenantId}"
            ))
            
            // Reset form
            model.addAttribute("createCompanyRequest", CreateCompanyRequest("", "", "NO"))
            
            return "company/create"
            
        } catch (e: Exception) {
            model.addAttribute("callout", Callout(
                message = "Failed to create company: ${e.message}",
                type = MessageType.ERROR
            ))
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
            model.addAttribute("error", "Search failed: ${e.message}")
            return "fragments/company-search :: error"
        }
    }
}