package com.respiroc.webapp.service

import com.respiroc.webapp.model.VoucherDocument
import com.respiroc.webapp.repository.VoucherDocumentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VoucherReceptionService(
    private val voucherDocumentRepository: VoucherDocumentRepository
) {
    
    fun saveDocument(document: VoucherDocument): VoucherDocument {
        return voucherDocumentRepository.save(document)
    }
}