package com.respiroc.voucherreception.api.command

data class CreateVoucherDocumentCommand(
    val companyId: Long,
    val tenantId: Long,
    val filename: String,
    val fileData: ByteArray? = null,
    val mimeType: String? = null,
    val fileSize: Long? = null,
    val senderEmail: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateVoucherDocumentCommand

        if (companyId != other.companyId) return false
        if (tenantId != other.tenantId) return false
        if (filename != other.filename) return false
        if (fileData != null) {
            if (other.fileData == null) return false
            if (!fileData.contentEquals(other.fileData)) return false
        } else if (other.fileData != null) return false
        if (mimeType != other.mimeType) return false
        if (fileSize != other.fileSize) return false
        if (senderEmail != other.senderEmail) return false

        return true
    }

    override fun hashCode(): Int {
        var result = companyId.hashCode()
        result = 31 * result + tenantId.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + (fileData?.contentHashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + (fileSize?.hashCode() ?: 0)
        result = 31 * result + (senderEmail?.hashCode() ?: 0)
        return result
    }
}