package com.respiroc.webapp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.respiroc.util.context.UserContext
import com.respiroc.util.context.SpringUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

open class BaseController {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    fun json(obj: Any?): String? {
        if (obj == null) return null
        return objectMapper.writeValueAsString(obj)
    }

    fun springUser(): SpringUser {
        return SecurityContextHolder.getContext().authentication.principal as SpringUser
    }

    fun user(): UserContext {
        return springUser().ctx
    }
}