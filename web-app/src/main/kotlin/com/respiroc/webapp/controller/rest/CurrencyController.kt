package com.respiroc.webapp.controller.rest

import com.respiroc.util.currency.CurrencyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/currency")
class CurrencyController(
    private val currencyService: CurrencyService
) {

    @GetMapping("/convert")
    fun convertAmount(
        @RequestParam amount: BigDecimal,
        @RequestParam fromCurrency: String,
        @RequestParam(required = false) toCurrency: String?
    ): ResponseEntity<Map<String, Any>> {
        val companyCurrency = currencyService.getCompanyCurrency("NO")
        val targetCurrency = toCurrency ?: companyCurrency
        
        val convertedAmount = if (fromCurrency == targetCurrency) {
            amount
        } else {
            currencyService.convertCurrency(amount, fromCurrency, targetCurrency)
        }
        
        val response = mapOf(
            "originalAmount" to amount,
            "originalCurrency" to fromCurrency,
            "convertedAmount" to convertedAmount,
            "convertedCurrency" to targetCurrency,
            "rate" to if (fromCurrency == targetCurrency) BigDecimal.ONE 
                     else currencyService.convertCurrency(BigDecimal.ONE, fromCurrency, targetCurrency)
        )
        
        return ResponseEntity.ok(response)
    }
} 