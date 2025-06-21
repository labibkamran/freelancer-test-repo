package com.respiroc.user.application.jwt

import io.jsonwebtoken.Claims
import java.util.Date
import java.util.function.Function

interface JwtUtils {
    fun extractSubject(token: String): String
    fun extractAudiences(token: String): Set<String>
    fun extractAudience(token: String): String?
    fun extractExpiration(token: String): Date
    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T
    fun extractAllClaims(token: String): Claims
    fun isTokenExpired(token: String): Boolean
    fun generateToken(subject: String, period: Long): String
    fun generateToken(subject: String, audience: String, period: Long): String
    fun generateToken(issuer: String, subject: String, audience: String, period: Long): String
    fun isTokenValid(token: String, subject: String): Boolean
}