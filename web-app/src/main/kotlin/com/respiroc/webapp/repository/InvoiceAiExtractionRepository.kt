package com.respiroc.webapp.repository

import com.respiroc.webapp.domain.model.InvoiceAiExtraction
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceAiExtractionRepository : CustomJpaRepository<InvoiceAiExtraction, Long> {
    fun findByVoucherReceptionDocumentId(documentId: Long): InvoiceAiExtraction?
}
