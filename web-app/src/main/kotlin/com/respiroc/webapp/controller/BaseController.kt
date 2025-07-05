package com.respiroc.webapp.controller

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.util.context.SpringUser
import com.respiroc.util.context.UserContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model
import kotlin.reflect.full.memberProperties

open class BaseController {

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

    fun tenantId(): Long {
        return user().currentTenant?.id ?: throw IllegalStateException("No current tenant is set for the user")
    }

    inline fun <reified T : Any> toMap(obj: T): Map<String, Any?> {
        return T::class.memberProperties.associate { prop ->
            prop.name to prop.get(obj)
        }
    }

    open fun addCommonAttributes(model: Model, title: String) {
        model.addAttribute(titleAttributeName, title)
    }

    fun addCommonAttributes(model: Model, companyApi: CompanyInternalApi, title: String) {
        addCommonAttributes(model, title)
        val springUser = springUser()
        model.addAttribute(userAttributeName, springUser)
        val companies = companyApi.findAllCompany()
        model.addAttribute(companiesAttributeNames, companies)
        val tenantId = springUser.ctx.currentTenant?.id
        if (tenantId != null) {
            val currentCompany = companies.find { it.tenantId == tenantId }
            model.addAttribute(currentCompanyAttributeNames, currentCompany)
        }
    }
}