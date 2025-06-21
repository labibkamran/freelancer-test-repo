package com.respiroc.webapp.controller

import com.respiroc.util.context.UserContext
import com.respiroc.util.context.SpringUser
import org.springframework.security.core.context.SecurityContextHolder

open class BaseController {

    fun springUser(): SpringUser {
        return SecurityContextHolder.getContext().authentication.principal as SpringUser
    }

    fun user(): UserContext {
        return springUser().ctx
    }
}