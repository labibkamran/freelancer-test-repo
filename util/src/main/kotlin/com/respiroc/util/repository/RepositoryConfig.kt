package com.respiroc.util.repository

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl::class, value = ["com.respiroc"])
@EntityScan(value = ["com.respiroc"])
class RepositoryConfig