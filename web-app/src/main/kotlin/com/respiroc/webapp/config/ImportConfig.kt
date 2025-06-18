package com.respiroc.webapp.config

import com.respiroc.companylookup.infrastructure.config.CompanyLookupConfig
import com.respiroc.util.repository.RepositoryConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(CompanyLookupConfig::class, RepositoryConfig::class)
class ImportConfig