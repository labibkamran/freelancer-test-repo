package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.customer.api.CustomerInternalApi
import com.respiroc.customer.api.payload.NewCustomerPayload
import com.respiroc.customer.domain.model.Customer
import com.respiroc.customer.domain.model.CustomerType
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCustomerRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping(value = ["/customer"])
class CustomerWebController(
    private val customerService: CustomerInternalApi,
    private val companyApi: CompanyInternalApi
) : BaseController() {

    @GetMapping("")
    fun getCustomers(
        @RequestHeader(value = "HX-Request", required = false) hxRequest: String?,
        @RequestParam("name", required = false) name: String?,
        model: Model
    ): String {
        val isExistName = !(name == null || name.isBlank())
        val tenantId = user().currentTenant!!.id
        val customers: List<Customer> = if (isExistName)
            customerService.findByNameContainingAndTenantId(name, tenantId)
        else
            customerService.findAllCustomerByTenantId(tenantId)
        model.addAttribute("customers", customers)
        addCommonAttributes(model, companyApi, "Customer")

        return if ("true".equals(hxRequest, true))
            "fragments/customer-table"
        else
            "customer/customer"
    }

    @GetMapping("/new")
    fun showCreateForm(
        model: Model,
        @RequestParam("privateCustomer", required = false) privateCustomer: Boolean = false,
        @RequestHeader(value = "HX-Request", required = false) hxRequest: String?
    ): String {
        model.addAttribute("customer", CreateCustomerRequest("", "", CustomerType.CUSTOMER.name, privateCustomer))
        addCommonAttributes(model, companyApi, "New Customer")

        return if ("true".equals(hxRequest, true))
            "customer/form :: customerFormFields"
        else
            "customer/form"
    }

    @PostMapping
    fun createCustomer(@ModelAttribute customer: CreateCustomerRequest): ResponseEntity<Any?> {
        val payload = NewCustomerPayload(
            name = customer.name,
            organizationNumber = customer.organizationNumber,
            type = CustomerType.valueOf(customer.type.uppercase()),
            privateCustomer = customer.privateCustomer
        )
        val tenantId = user().currentTenant!!.id
        customerService.createNewCustomer(payload, tenantId)

        val headers = HttpHeaders()
        headers.set("HX-Redirect", "/customer?tenantId=${tenantId}")
        return ResponseEntity<Any?>(headers, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    fun deleteCustomer(@PathVariable(name = "id") id: Long) {
        val userTenantId = user().currentTenant!!.id
        customerService.deleteByIdAndTenantId(id, userTenantId)
    }
}
