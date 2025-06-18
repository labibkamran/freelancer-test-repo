package com.respiroc.util.repository

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl::class, basePackages = ["com.respiroc"])
class RepositoryConfig