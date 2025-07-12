package com.respiroc.webapp.controller.web.htmx

import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.companylookup.api.CompanyLookupInternalApi
import com.respiroc.tenant.api.TenantInternalApi
import com.respiroc.user.api.UserInternalApi
import com.respiroc.util.constant.TenantRoleCode
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/htmx/tenant")
class TenantHTMXController(
    private val tenantApi: TenantInternalApi,
    private val userApi: UserInternalApi,
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
            val companyInfo =
                companyLookupApi.getInfo(createCompanyRequest.organizationNumber, createCompanyRequest.countryCode)
            val companyAddress = companyInfo.address
            val command = CreateCompanyCommand(
                name = companyInfo.name,
                organizationNumber = companyInfo.registrationNumber!!,
                countryCode = companyInfo.countryCode,
                addressCountryCode = companyAddress?.countryCode,
                postalCode = companyAddress?.postalCode,
                city = companyAddress?.city,
                primaryAddress = companyAddress?.address,
                secondaryAddress = null,
                administrativeDivisionCode = null
            )

            // TODO: check for exist user tenant company
            val tenant = tenantApi.createNewTenant(command)
            val tenantRole = tenantApi.findTenantRoleByCode(TenantRoleCode.OWNER)
            userApi.addUserTenantRole(tenant, tenantRole, user())

            return "redirect:htmx:/dashboard?tenantId=${tenant.id}"

        } catch (e: Exception) {
            model.addAttribute(
                errorMessageAttributeName,
                "Failed to create company: ${e.message}"
            )
            return "fragments/error-message"
        }
    }
}