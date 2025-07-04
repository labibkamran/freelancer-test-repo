package com.respiroc.ledger.api

import com.respiroc.ledger.api.payload.CreateVoucherPayload
import com.respiroc.ledger.api.payload.VoucherPayload
import com.respiroc.ledger.api.payload.VoucherSummaryPayload
import com.respiroc.ledger.domain.model.Voucher
import com.respiroc.util.context.UserContext

interface VoucherInternalApi {
    fun createVoucher(payload: CreateVoucherPayload, user: UserContext): VoucherPayload
    fun findAllVoucherSummaries(user: UserContext): List<VoucherSummaryPayload>
    fun findVoucherById(id: Long, user: UserContext): Voucher?
}