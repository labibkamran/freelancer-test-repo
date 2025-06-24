package com.respiroc.webapp.controller.rest

import com.respiroc.account.api.AccountInternalApi
import com.respiroc.account.domain.model.Account
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chart-of-accounts")
class ChartOfAccountController(
    private val accountApi: AccountInternalApi
) {

    @GetMapping(value = [""])
    fun getAllAccounts(): ResponseEntity<List<Account>> {
        return ResponseEntity.ok(accountApi.findAllAccounts().toList())
    }
}