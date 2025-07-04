package com.respiroc.webapp.controller

import com.respiroc.util.context.UserContext
import com.respiroc.util.context.SpringUser
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.reflect.full.memberProperties

open class BaseController {

    fun springUser(): SpringUser {
        return SecurityContextHolder.getContext().authentication.principal as SpringUser
    }

    fun user(): UserContext {
        return springUser().ctx
    }

    inline fun <reified T : Any> toMap(obj: T): Map<String, Any?> {
        return T::class.memberProperties.associate { prop ->
            prop.name to prop.get(obj)
        }
    }
}