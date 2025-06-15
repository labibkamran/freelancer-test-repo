package com.respiroc.user.application.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

@Component
class JwtUtilsImpl: JwtUtils {

    override fun extractSubject(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    override fun extractAudiences(token: String): Set<String> {
        return extractClaim(token, Claims::getAudience)
    }

    override fun extractAudience(token: String): String? {
        return try {
            extractClaim(token, Claims::getAudience).first()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override fun extractExpiration(token: String): Date {
        return extractClaim(token, Function { obj: Claims -> obj.expiration })
    }

    override fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    override fun extractAllClaims(token: String): Claims {
        try {
            val claims = Jwts
                .parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .payload
            return claims
        } catch (e: Exception) {
            throw RuntimeException("Can not extract claims from token")
        }
    }

    override fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    override fun generateToken(subject: String, period: Long): String {
        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + period))
            .signWith(secretKey())
            .compact()
    }

    override fun generateToken(subject: String, audience: String, period: Long): String {
        return Jwts
            .builder()
            .subject(subject)
            .audience().add(audience).and()
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + period))
            .signWith(secretKey())
            .compact()
    }

    override fun generateToken(issuer: String, subject: String, audience: String, period: Long
    ): String {
        return Jwts
            .builder()
            .issuer(issuer)
            .subject(subject)
            .audience().add(audience).and()
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + period))
            .signWith(secretKey())
            .compact()
    }

    override fun isTokenValid(token: String, username: String): Boolean {
        val subject = extractSubject(token)
        return subject == username && !isTokenExpired(token)
    }

    private fun secretKey(): SecretKey {
        val keyByte = Decoders.BASE64.decode("SECRET_KEY")
        return Keys.hmacShaKeyFor(keyByte)
    }
}