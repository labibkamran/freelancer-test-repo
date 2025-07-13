package com.respiroc.ledger.api

import com.respiroc.ledger.api.payload.CreatePostingPayload
import com.respiroc.ledger.api.payload.CreateVoucherPayload
import com.respiroc.ledger.api.payload.VoucherPayload
import com.respiroc.ledger.api.payload.VoucherSummaryPayload
import com.respiroc.ledger.domain.model.Voucher
import com.respiroc.util.context.ContextAwareApi

interface VoucherInternalApi : ContextAwareApi {
    fun createVoucher(payload: CreateVoucherPayload): VoucherPayload
    fun updateVoucherWithPostings(voucherId: Long, postings: List<CreatePostingPayload>): VoucherPayload
    fun findAllVoucherSummaries(): List<VoucherSummaryPayload>
    fun findVoucherById(id: Long): Voucher?
}
