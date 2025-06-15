package com.respiroc.webapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.respiroc"])
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}