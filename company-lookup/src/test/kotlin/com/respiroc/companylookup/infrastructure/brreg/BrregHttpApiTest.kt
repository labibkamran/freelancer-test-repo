package com.respiroc.companylookup.infrastructure.brreg

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BrregHttpApiTest {

    private val brregHttpApi: BrregHttpApi

    init {
        val restClient = RestClient.builder()
            .baseUrl("https://data.brreg.no/enhetsregisteret")
            .build()
        
        val httpServiceProxyFactory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()
        
        brregHttpApi = httpServiceProxyFactory.createClient(BrregHttpApi::class.java)
    }

    @Test
    fun `test search entities`() {
        // When
        val response = brregHttpApi.searchEntities(
            name = "Sesam stasjon",
            nameSearchMethod = null,
            size = 20,
            page = 0
        )

        println(response)

        // Then
        // assertNotNull(response)
        // assertNotNull(response.embedded)
        // assertTrue(response.embedded?.entities?.isNotEmpty() ?: false)
    }

    @Test
    fun `test get entity by organization number`() {
        // When
        val response = brregHttpApi.getEntity("974760673")  // Using a known organization number

        println(response)

        // Then
        // assertNotNull(response)
        // assertNotNull(response.organizationNumber)
        // assertNotNull(response.name)
    }

    @Test
    fun `test search sub entities`() {
        // When
        val response = brregHttpApi.searchSubEntities(
            name = "Test",
            nameSearchMethod = "FORTLOEPENDE",
            size = 20,
            page = 0
        )

        println(response)

        // Then
        // assertNotNull(response)
        // assertNotNull(response.embedded)
    }

    @Test
    fun `test get sub entity by organization number`() {
        // When
        val response = brregHttpApi.getSubEntity("974760673")  // Using a known organization number

        println(response)

        // Then
        // assertNotNull(response)
        // assertNotNull(response.organizationNumber)
        // assertNotNull(response.name)
    }
} 