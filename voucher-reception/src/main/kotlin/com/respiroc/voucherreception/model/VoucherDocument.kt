package com.respiroc.voucherreception.model

import com.respiroc.company.domain.model.Company
import com.respiroc.tenant.domain.model.Tenant
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "voucher_documents")
data class VoucherDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    val company: Company,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    val tenant: Tenant,
    
    @Column(nullable = false)
    val filename: String,
    
    @Lob
    @Column(name = "file_data")
    val fileData: ByteArray? = null,
    
    @Column(name = "mime_type")
    val mimeType: String? = null,
    
    @Column(name = "file_size")
    val fileSize: Long? = null,
    
    @Column(name = "sender_email")
    val senderEmail: String? = null,
    
    @Column(name = "received_at", nullable = false)
    val receivedAt: LocalDateTime = LocalDateTime.now(),
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: VoucherDocumentStatus = VoucherDocumentStatus.PENDING,
    
    @Column(name = "attached_voucher_id")
    val attachedVoucherId: Long? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null
)

enum class VoucherDocumentStatus {
    PENDING,
    ATTACHED,
    DELETED,
    FAILED
}