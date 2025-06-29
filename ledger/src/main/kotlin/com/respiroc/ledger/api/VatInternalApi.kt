package com.respiroc.ledger.api

import com.respiroc.ledger.domain.model.VatCode
import com.respiroc.ledger.domain.model.VatType
import com.respiroc.ledger.domain.model.VatCategory
import java.math.BigDecimal

interface VatInternalApi {
    fun findVatCodeByCode(code: String): VatCode?
    fun findAllVatCodes(): Collection<VatCode>
    fun findVatCodesByType(vatType: VatType): List<VatCode>
    fun findVatCodesByCategory(vatCategory: VatCategory): List<VatCode>
    fun searchVatCodesByDescription(searchTerm: String): List<VatCode>
    fun vatCodeExists(code: String): Boolean
    fun findInputVatCodes(): List<VatCode>
    fun findOutputVatCodes(): List<VatCode>
    fun findExemptVatCodes(): List<VatCode>
    fun calculateVatAmount(baseAmount: BigDecimal, vatCode: VatCode): BigDecimal
    fun calculateBaseAmountFromVatInclusive(vatInclusiveAmount: BigDecimal, vatCode: VatCode): BigDecimal
} 