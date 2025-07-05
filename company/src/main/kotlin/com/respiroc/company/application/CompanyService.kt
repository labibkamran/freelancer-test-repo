package com.respiroc.company.application

import com.respiroc.company.api.CompanyInternalApi
import com.respiroc.company.api.command.CreateCompanyCommand
import com.respiroc.company.domain.model.Company
import com.respiroc.company.domain.repository.CompanyRepository
import com.respiroc.tenant.api.TenantInternalApi
import com.respiroc.user.api.UserInternalApi
import com.respiroc.util.constant.TenantRoleCode
import com.respiroc.util.context.UserContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val tenantApi: TenantInternalApi,
    private val userApi: UserInternalApi
) : CompanyInternalApi {

    override fun createNewCompany(
        command: CreateCompanyCommand,
        user: UserContext
    ): Company {
        val tenant = tenantApi.createNewTenant(command.name)
        val tenantRole = tenantApi.findTenantRoleByCode(TenantRoleCode.OWNER)
        userApi.addUserTenantRole(tenant, tenantRole, user)

        val company = Company()
        company.name = command.name
        company.tenant = tenant
        company.tenantId = tenant.id
        company.organizationNumber = command.organizationNumber
        company.countryCode = command.countryCode

        return companyRepository.save(company)
    }

    override fun findAllCompanyByUser(user: UserContext): List<Company> {
        return companyRepository.findByTenantIdIn(user.tenants.map { it.id })
    }

    override fun findCurrentCompanyByUser(user: UserContext): Company? {
        if (user.currentTenant == null) return null
        return companyRepository.findByTenantId(user.currentTenant!!.id)
    }

    override fun findCompanyByUserAndTenantId(
        user: UserContext,
        tenantId: Long
    ): Company? {
        TODO("Not yet implemented")
    }
    
    override fun findCompanyById(id: Long): Company? {
        return companyRepository.findById(id).orElse(null)
    }
    
    override fun findCompanyBySlug(slug: String): Company? {
        return companyRepository.findBySlug(slug)
    }
}