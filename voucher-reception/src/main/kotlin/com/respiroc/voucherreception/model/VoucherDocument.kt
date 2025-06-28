package com.respiroc.voucherreception.model

import com.respiroc.company.domain.model.Company
import com.respiroc.tenant.domain.model.Tenant
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
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
    @Column(name = "file_data")
    var fileData: ByteArray? = null
    
    @Column(name = "mime_type")
    var mimeType: String? = null
    
    @Column(name = "file_size")
    var fileSize: Long? = null
    
    @Column(name = "sender_email")
    var senderEmail: String? = null
    
    @Column(name = "received_at", nullable = false)
    var receivedAt: Instant = Instant.now()
    
    @Column(name = "attached_voucher_id")
    var attachedVoucherId: Long? = null
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null
    
    val isPending: Boolean
        get() = attachedVoucherId == null
        
    val isAttached: Boolean
        get() = attachedVoucherId != null
}