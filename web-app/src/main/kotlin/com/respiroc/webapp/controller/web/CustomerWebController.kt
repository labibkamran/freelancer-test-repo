package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.customer.api.CustomerInternalApi
import com.respiroc.customer.api.payload.NewCustomerPayload
import com.respiroc.customer.domain.model.Customer
import com.respiroc.customer.domain.model.CustomerType
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCustomerRequest
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

        if ("true".equals(hxRequest, true))
            return "fragments/customer/customer-table"
        else
            return "customer/customer"
    }

    @GetMapping("/new")
    fun showCreateForm(model: Model): String {
        model.addAttribute("customer", Customer())
        addCommonAttributes(model, companyApi, "New Customer")
        return "customer/form"
    }

    @PostMapping
    fun createCustomer(@ModelAttribute customer: CreateCustomerRequest): String {
        val payload = NewCustomerPayload(
            name = customer.name,
            organizationNumber = customer.organizationNumber,
            type = CustomerType.valueOf(customer.type)
        )
        customerService.createNewCustomer(payload, user().currentTenant!!.id)
        return "redirect:/customers"
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    fun deleteCustomer(@PathVariable(name = "id") id: Long) {
        val userTenantId = user().currentTenant!!.id
        customerService.deleteByIdAndTenantId(id, userTenantId)
    }
}
