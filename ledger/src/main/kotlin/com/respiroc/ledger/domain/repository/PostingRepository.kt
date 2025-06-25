package com.respiroc.ledger.domain.repository

import com.respiroc.ledger.domain.model.Posting
import com.respiroc.util.repository.CustomJpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostingRepository : CustomJpaRepository<Posting, Long> {
}