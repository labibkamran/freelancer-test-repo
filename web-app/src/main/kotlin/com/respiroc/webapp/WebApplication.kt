package com.respiroc.webapp

import com.respiroc.companylookup.infrastructure.config.CompanyLookupConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(CompanyLookupConfig::class)
class WebApplication

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}