package com.respiroc.webapp.controller

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
    val tenantsAttributeNames: String = "tenants"
    val currentTenantAttributeNames: String = "currentTenant"
    val calloutAttributeNames: String = "callout"

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
        title: String,
        useCurrentCompanyAsTitlePrefix: Boolean = false
    ) {
        val springUser = springUser()
        model.addAttribute(userAttributeName, springUser)

        model.addAttribute(tenantsAttributeNames, springUser.ctx.tenants)

        val currentTenant = springUser.ctx.currentTenant
        if (currentTenant != null)
            model.addAttribute(currentTenantAttributeNames, currentTenant)
        if (useCurrentCompanyAsTitlePrefix && currentTenant != null)
            model.addAttribute(titleAttributeName, "${currentTenant.companyName} - ${title}")
        else
            model.addAttribute(titleAttributeName, title)
    }
}