package com.respiroc.webapp.controller

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.company.domain.model.Company
import com.respiroc.webapp.controller.request.CreateCompanyRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/companies"])
class CompanyController(private val companyApi: CompanyInternalApi) : BaseController() {

    @PostMapping(value = [""])
    fun create(@RequestBody request: CreateCompanyRequest): ResponseEntity<Company> {
        val result = companyApi.createNewCompany(
            CreateCompanyCommand(request.name, request.organizationNumber, request.countryCode),
            user())
        return ResponseEntity.ok(result)
    }

    @GetMapping(value = [""])
    fun getAllCompany(): ResponseEntity<List<Company>> {
        val result = companyApi.findAllCompanyByUser(user())
        return ResponseEntity.ok(result)
    }
}