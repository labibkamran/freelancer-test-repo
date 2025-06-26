package com.respiroc.webapp.config

import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus

@Configuration
class ErrorPageConfig {

    @Bean
    fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        return WebServerFactoryCustomizer { factory ->
            factory.addErrorPages(
                ErrorPage(HttpStatus.NOT_FOUND, "/error/404"),
                ErrorPage(HttpStatus.FORBIDDEN, "/error/403"),
                ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"),
                ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500")
            )
        }
    }
} 