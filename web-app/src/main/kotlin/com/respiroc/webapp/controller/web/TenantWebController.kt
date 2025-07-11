package com.respiroc.webapp.controller.web

import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(value = ["/tenant"])
class TenantWebController : BaseController() {

    @GetMapping("/create")
    fun createCompany(model: Model): String {
        addCommonAttributes(model, "Create Company")
        model.addAttribute("createCompanyRequest", CreateCompanyRequest("", "", "NO"))
        return "tenant/create"
    }
}