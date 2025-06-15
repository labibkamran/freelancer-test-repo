package com.respiroc.webapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.respiroc.companylookup.infrastructure.config.CompanyLookupConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(CompanyLookupConfig::class)
class ImportConfig