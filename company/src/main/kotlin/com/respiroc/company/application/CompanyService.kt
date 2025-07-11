package com.respiroc.company.application

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.company.domain.model.Company
import com.respiroc.company.domain.repository.CompanyRepository
import com.respiroc.tenant.api.TenantInternalApi
import com.respiroc.user.api.UserInternalApi
import com.respiroc.util.constant.TenantRoleCode
import com.respiroc.util.currency.CurrencyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val tenantApi: TenantInternalApi,
    private val userApi: UserInternalApi,
    private val currencyService: CurrencyService
) : CompanyInternalApi {

    override fun createNewCompany(
        command: CreateCompanyCommand
    ): Company {
        val tenant = tenantApi.createNewTenant(command.name)
        val tenantRole = tenantApi.findTenantRoleByCode(TenantRoleCode.OWNER)
        userApi.addUserTenantRole(tenant, tenantRole, user())

        val company = Company()
        company.name = command.name
        company.tenant = tenant
        company.tenantId = tenant.id
        company.organizationNumber = command.organizationNumber
        company.countryCode = command.countryCode

        val savedCompany = companyRepository.save(company)
        savedCompany.currencyCode = currencyService.getCompanyCurrency(savedCompany.countryCode)
        return savedCompany
    }

    override fun findAllCompany(): List<Company> {
        val companies = companyRepository.findByTenantIdIn(user().tenants.map { it.id })
        return companies.map { company ->
            company.currencyCode = currencyService.getCompanyCurrency(company.countryCode)
            company
        }
    }

    override fun findCurrentCompany(): Company? {
        return try {
            val company = companyRepository.findByTenantId(currentTenantId())
            company?.let {
                it.currencyCode = currencyService.getCompanyCurrency(it.countryCode)
                it
            }
        } catch (_: Exception) {
            null
        }
    }
}