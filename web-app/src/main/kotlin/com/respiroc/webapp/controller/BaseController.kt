package com.respiroc.webapp.controller

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.domain.model.Company
import com.respiroc.util.context.SpringUser
import com.respiroc.util.context.UserContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model
import kotlin.reflect.full.memberProperties

open class BaseController {

    val titleAttributeName: String = "title"
    val successMessageAttributeName: String = "success"
    val errorMessageAttributeName: String = "error"
    val userAttributeName: String = "user"
    val companiesAttributeName: String = "companies"
    val currentCompanyAttributeName: String = "currentCompany"
    val calloutAttributeName: String = "callout"

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

    fun addCommonAttributes(
        model: Model,
        companyApi: CompanyInternalApi,
        title: String,
        useCurrentCompanyAsTitlePrefix: Boolean = false
    ) {

        val springUser = springUser()
        model.addAttribute(userAttributeName, springUser)

        val companies = companyApi.findAllCompany()
        model.addAttribute(companiesAttributeName, companies)

        val tenantId = springUser.ctx.currentTenant?.id
        var currentCompany: Company? = null
        if (tenantId != null) {
            currentCompany = companies.find { it.tenantId == tenantId }
            model.addAttribute(currentCompanyAttributeName, currentCompany)
        }
        if (useCurrentCompanyAsTitlePrefix && currentCompany != null)
            model.addAttribute(titleAttributeName, "${currentCompany.name} - ${title}")
        else
            model.addAttribute(titleAttributeName, title)
    }
}