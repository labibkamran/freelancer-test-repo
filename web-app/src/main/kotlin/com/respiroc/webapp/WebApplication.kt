package com.respiroc.webapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.respiroc"], exclude = [UserDetailsServiceAutoConfiguration::class])
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}