package com.respiroc.webapp.model

import com.respiroc.company.domain.model.Company
import com.respiroc.tenant.domain.model.Tenant
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "voucher_documents")
class VoucherDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    lateinit var company: Company
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    lateinit var tenant: Tenant
    
    @Column(nullable = false)
    lateinit var filename: String
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data", columnDefinition = "BYTEA")
    var fileData: ByteArray? = null
    
    @Column(name = "mime_type")
    var mimeType: String? = null
    
    @Column(name = "file_size")
    var fileSize: Long? = null
    
    @Column(name = "sender_email")
    var senderEmail: String? = null
    
    @CreationTimestamp
    @Column(name = "received_at", nullable = false)
    var receivedAt: Instant? = null
    
    @Column(name = "attached_voucher_id")
    var attachedVoucherId: Long? = null
}