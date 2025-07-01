package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping(value = ["/company"])
class CompanyWebController(
    private val companyApi: CompanyInternalApi,
    private val companyLookupApi: CompanyLookupInternalApi
) : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        addCommonAttributes(model, companyApi, "Create Company")
        model.addAttribute(
            "createCompanyRequest",
            CreateCompanyRequest("", "", "NO")
        )
        return "company/create"
    }

    @PostMapping("/create")
    fun createCompanySubmit(
        @Valid @ModelAttribute createCompanyRequest: CreateCompanyRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model, companyApi, "Create Company")
            model.addAttribute("error", "Please fill in all required fields correctly.")
            return "company/create"
        }

        try {
            val springUser = springUser()
            val command = CreateCompanyCommand(
                name = createCompanyRequest.name,
                organizationNumber = createCompanyRequest.organizationNumber,
                countryCode = createCompanyRequest.countryCode
            )

            val company = companyApi.createNewCompany(command, springUser.ctx)

            redirectAttributes.addFlashAttribute(
                "success",
                "Company '${company.name}' has been created successfully!"
            )
            return "redirect:/dashboard?tenantId=${company.tenantId}"

        } catch (e: Exception) {
            addCommonAttributes(model, companyApi, "Create Company")
            model.addAttribute("error", "Failed to create company: ${e.message}")
            return "company/create"
        }
    }

    @PostMapping("/create", headers = ["HX-Request"])
    fun createCompanyHtmx(
        @Valid @ModelAttribute createCompanyRequest: CreateCompanyRequest,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Please fill in all required fields correctly.")
            return "fragments/alert :: error"
        }

        try {
            val springUser = springUser()
            val command = CreateCompanyCommand(
                name = createCompanyRequest.name,
                organizationNumber = createCompanyRequest.organizationNumber,
                countryCode = createCompanyRequest.countryCode
            )

            val company = companyApi.createNewCompany(command, springUser.ctx)

            model.addAttribute(
                "success",
                "Company '${company.name}' has been created successfully! Redirecting to dashboard..."
            )
            model.addAttribute("redirectUrl", "/dashboard?tenantId=${company.tenantId}")
            return "fragments/alert :: success-with-redirect"

        } catch (e: Exception) {
            model.addAttribute("error", "Failed to create company: ${e.message}")
            return "fragments/alert :: error"
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

    @GetMapping("/select")
    fun selectTenant(model: Model): String {
        addCommonAttributes(model, companyApi, "Select Company")
        return "company/select"
    }
}