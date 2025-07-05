package com.respiroc.webapp.repository

import com.respiroc.util.repository.CustomJpaRepository
import com.respiroc.webapp.model.VoucherDocument
import org.springframework.stereotype.Repository

@Repository
interface VoucherDocumentRepository : CustomJpaRepository<VoucherDocument, Long>