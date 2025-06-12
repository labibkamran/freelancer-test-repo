package com.respiroc.companylookup.infrastructure.brreg

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class BrregClientConfig {

    @Bean
    fun brregApiClient(): BrregHttpApi {
        val restClient = RestClient.builder().baseUrl("https://data.brreg.no/enhetsregisteret").build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(BrregHttpApi::class.java)
    }
}