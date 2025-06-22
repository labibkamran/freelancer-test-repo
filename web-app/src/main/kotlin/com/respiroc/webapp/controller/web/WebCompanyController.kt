package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.webapp.controller.BaseController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/companies"])
class WebCompanyController(
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        val user = user()
        model.addAttribute("user", user)

        val companies = companyApi.findAllCompanyByUser(user)
        model.addAttribute("companies", companies)

        return "company/create"
    }
}