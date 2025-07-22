package com.respiroc.webapp.domain.model

import com.respiroc.webapp.VoucherReceptionDocument
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "invoice_ai_extractions")
class InvoiceAiExtraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_reception_document_id", nullable = false)
    lateinit var voucherReceptionDocument: VoucherReceptionDocument
    
    @Column(name = "extraction_data", columnDefinition = "JSONB", nullable = false)
    lateinit var extractionData: String // JSON string
    
    @Enumerated(EnumType.STRING)
    @Column(name = "extraction_status", nullable = false)
    var status: ExtractionStatus = ExtractionStatus.PENDING
    
    @Column(name = "extraction_date")
    var extractionDate: Instant? = null
    
    @Column(name = "processing_errors")
    var processingErrors: String? = null
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null
}

enum class ExtractionStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, CONVERTED_TO_VOUCHER
}
