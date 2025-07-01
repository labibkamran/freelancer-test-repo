package com.respiroc.webapp.controller

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.util.context.SpringUser
import com.respiroc.util.context.UserContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model

open class BaseController() {

    private val titleAttributeName: String = "title"
    private val userAttributeName: String = "user"
    private val companiesAttributeNames: String = "companies"
    private val currentCompanyAttributeNames: String = "currentCompany"

    fun springUser(): SpringUser {
        return SecurityContextHolder.getContext().authentication.principal as SpringUser
    }

    fun user(): UserContext {
        return springUser().ctx
    }

    open fun addCommonAttributes(model: Model, title: String) {
        model.addAttribute(titleAttributeName, title)
    }

    fun addCommonAttributes(model: Model, companyApi: CompanyInternalApi, title: String) {
        addCommonAttributes(model, title)
        val springUser = springUser()
        val tenantId = springUser.ctx.currentTenant!!.id
        model.addAttribute(userAttributeName, springUser)
        val companies = companyApi.findAllCompanyByUser(springUser.ctx)
        model.addAttribute(companiesAttributeNames, companies)
        val currentCompany = companies.find { it.tenantId == tenantId }
        model.addAttribute(currentCompanyAttributeNames, currentCompany)
    }
}