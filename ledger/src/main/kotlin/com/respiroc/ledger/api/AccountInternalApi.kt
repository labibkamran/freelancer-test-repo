package com.respiroc.ledger.api

import com.respiroc.ledger.domain.model.Account
import com.respiroc.ledger.domain.model.AccountType

interface AccountInternalApi {
    fun findAccountByNumber(noAccountNumber: String): Account?
    fun findAllAccounts(): Collection<Account>
    fun findAccountsByType(accountType: AccountType): List<Account>
    fun searchAccountsByName(searchTerm: String): List<Account>
    fun accountExists(noAccountNumber: String): Boolean
    fun findAssetAccounts(): List<Account>
    fun findLiabilityAccounts(): List<Account>
    fun findEquityAccounts(): List<Account>
    fun findRevenueAccounts(): List<Account>
    fun findExpenseAccounts(): List<Account>
    fun findCostOfGoodsSoldAccounts(): List<Account>
}