package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/company"])
class CompanyWebController(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        addCommonAttributes(model, companyApi, "Create Company")
        model.addAttribute("createCompanyRequest", CreateCompanyRequest("", "", "NO"))
        return "company/create"
    }
}