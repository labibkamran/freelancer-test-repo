package com.respiroc.webapp.controller.web

import com.respiroc.common.payload.NewCustomerSupplierPayload
import com.respiroc.customer.application.CustomerService
import com.respiroc.customer.domain.model.Customer
import com.respiroc.customer.domain.model.CustomerType
import com.respiroc.supplier.application.SupplierService
import com.respiroc.supplier.domain.model.Supplier
import com.respiroc.webapp.controller.BaseController
import com.respiroc.webapp.controller.request.CreateCustomerRequest
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping(value = ["/contact"])
class ContactWebController(
    private val customerService: CustomerService,
    private val supplierService: SupplierService,
) : BaseController() {

    @GetMapping("/customer")
    fun getCustomers(model: Model): String {
        val tenantId = user().currentTenant!!.id
        val customers = customerService.findAllCustomerByTenantId(tenantId)
        model.addAttribute("contacts", customers)
        model.addAttribute("searchUrl", "/htmx/contact/customer/search")
        model.addAttribute("type", "customer")
        addCommonAttributes(model, "Customer")
        return "contact/contact"
    }

    @GetMapping("/new")
    fun getForm(model: Model): String {
        model.addAttribute(
            "customer",
            CreateCustomerRequest("", "", CustomerType.CUSTOMER.name, false)
        )
        addCommonAttributes(model, "New Customer")
        return "contact/contact-form"
    }

    @DeleteMapping("/customer/{id}")
    @ResponseBody
    fun deleteCustomer(@PathVariable(name = "id") id: Long) {
        val userTenantId = user().currentTenant!!.id
        customerService.deleteByIdAndTenantId(id, userTenantId)
    }

    @GetMapping("/supplier")
    fun getSupplier(model: Model): String {
        val tenantId = user().currentTenant!!.id
        val suppliers = supplierService.findAllSupplierByTenantId(tenantId)
        model.addAttribute("contacts", suppliers)
        model.addAttribute("searchUrl", "/htmx/contact/supplier/search")
        model.addAttribute("type", "supplier")
        addCommonAttributes(model, "Supplier")
        return "contact/contact"
    }

    @DeleteMapping("/supplier/{id}")
    @ResponseBody
    fun deleteSupplier(@PathVariable(name = "id") id: Long) {
        val userTenantId = user().currentTenant!!.id
        supplierService.deleteByIdAndTenantId(id, userTenantId)
    }
}

@Controller
@RequestMapping(value = ["/htmx/contact"])
class ContactHTMxController(
    private val customerService: CustomerService,
    private val supplierService: SupplierService,
) : BaseController() {

    @GetMapping("/customer/search")
    fun searchCustomers(@RequestParam("name") name: String, model: Model): String {
        val tenantId = user().currentTenant!!.id
        val isExistName = !(name.isBlank())
        val customers: List<Customer> = if (isExistName)
            customerService.findByNameContainingAndTenantId(name, tenantId)
        else
            customerService.findAllCustomerByTenantId(tenantId)
        model.addAttribute("contacts", customers)
        model.addAttribute("type", "customer")
        model.addAttribute(currentTenantAttributeName, user().currentTenant)
        return "fragments/contact-table"
    }

    @GetMapping("/supplier/search")
    fun searchSuppliers(@RequestParam("name") name: String, model: Model): String {
        val tenantId = user().currentTenant!!.id
        val isExistName = !(name.isBlank())
        val suppliers: List<Supplier> = if (isExistName)
            supplierService.findByNameContainingAndTenantId(name, tenantId)
        else
            supplierService.findAllSupplierByTenantId(tenantId)
        model.addAttribute("contacts", suppliers)
        model.addAttribute("type", "supplier")
        model.addAttribute(currentTenantAttributeName, user().currentTenant)
        return "fragments/contact-table"
    }

    @GetMapping("/new")
    @HxRequest
    fun getForm(
        model: Model,
        @RequestParam("privateCustomer", required = false) privateCustomer: Boolean = false,
    ): String {
        model.addAttribute(
            "customer",
            CreateCustomerRequest("", "", CustomerType.CUSTOMER.name, privateCustomer)
        )
        return "contact/contact-form :: customerFormFields"
    }

    @PostMapping
    fun createCustomer(@ModelAttribute customer: CreateCustomerRequest): String {
        val payload = NewCustomerSupplierPayload(
            name = customer.name,
            organizationNumber = customer.organizationNumber,
            type = CustomerType.valueOf(customer.type.uppercase()),
            privateCustomer = customer.privateCustomer,
            countryCode = customer.countryCode,
            postalCode = customer.postalCode,
            addressPart1 = customer.addressPart1,
            addressPart2 = customer.addressPart2,
            city = customer.city,
            administrativeDivisionCode = customer.administrativeDivisionCode
        )
        val tenantId = user().currentTenant!!.id
        // TODO: add controller adviser
        // TODO: Fix callout fragment to display error messages without refresh page
        var targetPage = "customer"
        when (payload.type) {
            CustomerType.CUSTOMER -> {
                customerService.createNewCustomer(payload, tenantId)
                targetPage = "customer"
            }

            CustomerType.SUPPLIER -> {
                supplierService.createNewSupplier(payload, tenantId)
                targetPage = "supplier"
            }

            CustomerType.CUSTOMER_SUPPLIER -> {
                // TODO: Handle it if any of these throws an error.
                customerService.createNewCustomer(payload, tenantId)
                supplierService.createNewSupplier(payload, tenantId)
            }
        }
        return "redirect:htmx:/contact/${targetPage}?tenantId=${tenantId}"
    }
}
