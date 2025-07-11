package com.respiroc.webapp.controller.web

import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.tenant.api.TenantInternalApi
import com.respiroc.user.api.UserInternalApi
import com.respiroc.util.constant.TenantRoleCode
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/tenant"])
class TenantWebController(
    private val tenantApi: TenantInternalApi,
    private val userApi: UserInternalApi
) : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        addCommonAttributes(model, "Create Company")
        model.addAttribute("createCompanyRequest", CreateCompanyRequest("", "", "NO"))
        return "tenant/create"
    }

    @PostMapping("/create")
    fun createTenant(
        @Valid @ModelAttribute createCompanyRequest: CreateCompanyRequest,
        bindingResult: BindingResult,
        model: Model
    ): Any {
        addCommonAttributes(model, "Create Company")
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                calloutAttributeNames, Callout.Error(
                    message = "Please fill in all required fields correctly."
                )
            )
            return "tenant/create"
        }

        try {
            val command = CreateCompanyCommand(
                name = createCompanyRequest.name,
                organizationNumber = createCompanyRequest.organizationNumber,
                countryCode = createCompanyRequest.countryCode
            )
             // TODO: check for exist user tenant company
            val tenant = tenantApi.createNewTenant(command)
            val tenantRole = tenantApi.findTenantRoleByCode(TenantRoleCode.OWNER)
            userApi.addUserTenantRole(tenant, tenantRole, user())

            val headers = HttpHeaders()
            headers.add("HX-Redirect", "/dashboard?tenantId=${tenant.id}")
            return ResponseEntity<Void>(headers, HttpStatus.OK)

        } catch (e: Exception) {
            model.addAttribute(
                calloutAttributeNames, Callout.Error(
                    message = "Failed to create company: ${e.message}"
                )
            )
            return "tenant/create"
        }
    }
}