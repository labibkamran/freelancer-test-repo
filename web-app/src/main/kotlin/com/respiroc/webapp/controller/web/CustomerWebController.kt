package com.respiroc.webapp.controller.web

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.customer.api.CustomerInternalApi
import com.respiroc.customer.api.command.CreateCustomerCommand
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
//      not implemented yet
//    @PostMapping
//    fun createCustomer(@ModelAttribute customer: CreateCustomerRequest): String {
//        val command = CreateCustomerCommand(
//            name = customer.name,
//            organizationNumber = customer.organizationNumber,
//            type = CustomerType.valueOf(customer.type)
//        )
//        customerService.createNewCustomer(command, user())
//        return "redirect:/customers"
//    }
//
//    @GetMapping("/{id}/edit")
//    fun showEditForm(
//        @PathVariable id: Long?,
//        @ModelAttribute customer: CreateCustomerRequest,
//        model: Model
//    ): String {
////        val customer: Customer? = customerService.findAllCustomerByTenantId(id)
////        model.addAttribute("customer", customer)
//        return "customer/form"
//    }
//
//    @PostMapping("/{id}")
//    fun updateCustomer(@PathVariable id: Long, @ModelAttribute customer: CreateCustomerRequest): String {
//        val command = CreateCustomerCommand(
//            name = customer.name,
//            organizationNumber = customer.organizationNumber,
//            type = CustomerType.valueOf(customer.type)
//        )
//        customerService.editCustomer(id, command, user())
//        return "redirect:/customers"
//    }
//
//    @PostMapping("/{id}/delete")
//    fun deleteCustomer(@PathVariable id: Long?): String {
////        customerService.deleteById(id)
//        return "redirect:/customers"
//    }


}
