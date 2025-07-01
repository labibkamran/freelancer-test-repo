package com.respiroc.webapp.config

import com.respiroc.user.api.UserInternalApi
import com.respiroc.util.context.SpringUser
import com.respiroc.webapp.filter.TenantIdFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Autowired
    lateinit var userApi: UserInternalApi

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/auth/login").permitAll()
                    .requestMatchers("/assets/**").permitAll()
                    .requestMatchers("/auth/signup").permitAll()
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/api/auth/signup").permitAll()
                    .requestMatchers("/api/company-lookup/**").permitAll()
                    .requestMatchers("/test/**").permitAll()
                    .requestMatchers("/error/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .cors { }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .addFilterBefore(
                BearerTokenAuthenticationFilter(userApi),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterAfter(
                TenantIdFilter(),
                BearerTokenAuthenticationFilter::class.java
            )
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.applyPermitDefaultValues()
        configuration.allowedMethods = listOf(
            HttpMethod.GET.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.POST.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name()
        )
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    class BearerTokenAuthenticationFilter(private val userApi: UserInternalApi) : OncePerRequestFilter() {
        override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
            if (SecurityContextHolder.getContext().authentication == null) {
                var token = ""

                // Check Authorization header first (for API calls)
                val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
                if (StringUtils.isNotEmpty(authorizationHeader)) {
                    token = StringUtils.removeStart(authorizationHeader, "Bearer").trim()
                }

                // If no token in header, check JWT cookie (for web app)
                if (StringUtils.isEmpty(token)) {
                    val cookies = request.cookies
                    if (cookies != null) {
                        val jwtCookie = cookies.find { it.name == "jwt_token" }
                        if (jwtCookie != null && StringUtils.isNotEmpty(jwtCookie.value)) {
                            token = jwtCookie.value
                        }
                    }
                }

                if (StringUtils.isNotEmpty(token)) {
                    val user = userApi.findByToken(token)
                    if (user != null) {
                        val userDetails: UserDetails = SpringUser(user)
                        val usernamePasswordAuthenticationToken =
                            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                        usernamePasswordAuthenticationToken.details =
                            WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
                    }
                }
            }
            chain.doFilter(request, response)
        }
    }
} 