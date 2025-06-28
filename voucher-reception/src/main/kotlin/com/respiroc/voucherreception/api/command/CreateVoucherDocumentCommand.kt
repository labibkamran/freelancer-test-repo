package com.respiroc.voucherreception.api.command

data class CreateVoucherDocumentCommand(
    val companyId: Long,
    val tenantId: Long,
    val filename: String,
    val fileData: ByteArray? = null,
    val mimeType: String? = null,
    val fileSize: Long? = null,
    val senderEmail: String? = null
)