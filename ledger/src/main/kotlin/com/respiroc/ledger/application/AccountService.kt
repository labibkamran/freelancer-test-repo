package com.respiroc.ledger.application

import com.respiroc.ledger.api.AccountInternalApi
import com.respiroc.ledger.domain.model.Account
import com.respiroc.ledger.domain.model.AccountType
import jakarta.annotation.PostConstruct
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.Yaml

@Service
class AccountService : AccountInternalApi {

    private lateinit var accounts: Map<String, Account>

    @PostConstruct
    fun loadChartOfAccounts() {
        val yaml = Yaml()
        val inputStream = ClassPathResource("data/chart-of-accounts.yaml").inputStream

        @Suppress("UNCHECKED_CAST")
        val data = yaml.load<Map<String, Any>>(inputStream)
        val accountsList = data["accounts"] as List<Map<String, Any>>

        accounts = accountsList.associate { accountData ->
            val noAccountNumber = accountData["noAccountNumber"] as String
            val account = Account(
                noAccountNumber = noAccountNumber,
                accountName = accountData["accountName"] as String,
                accountDescription = accountData["accountDescription"] as String,
                accountType = AccountType.valueOf(accountData["accountType"] as String)
            )
            noAccountNumber to account
        }
    }

    override fun findAccountByNumber(noAccountNumber: String): Account? {
        return accounts[noAccountNumber]
    }

    override fun findAllAccounts(): Collection<Account> {
        return accounts.values
    }

    override fun findAccountsByType(accountType: AccountType): List<Account> {
        return accounts.values.filter { it.accountType == accountType }
    }

    override fun searchAccountsByName(searchTerm: String): List<Account> {
        return accounts.values.filter {
            it.accountName.contains(searchTerm, ignoreCase = true) ||
                    it.accountDescription?.contains(searchTerm, ignoreCase = true) == true
        }
    }

    override fun accountExists(noAccountNumber: String): Boolean {
        return accounts.containsKey(noAccountNumber)
    }

    override fun findAssetAccounts(): List<Account> = findAccountsByType(AccountType.ASSET)

    override fun findLiabilityAccounts(): List<Account> = findAccountsByType(AccountType.LIABILITY)

    override fun findEquityAccounts(): List<Account> = findAccountsByType(AccountType.EQUITY)

    override fun findRevenueAccounts(): List<Account> = findAccountsByType(AccountType.REVENUE)

    override fun findExpenseAccounts(): List<Account> = findAccountsByType(AccountType.EXPENSE)

    override fun findCostOfGoodsSoldAccounts(): List<Account> = findAccountsByType(AccountType.COST_OF_GOODS_SOLD)
}