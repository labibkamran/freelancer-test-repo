package com.respiroc.util.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface CustomJpaRepository<T, ID : Serializable?> : JpaRepository<T, ID> {
    fun refresh(entity: T): T
    fun refresh(entity: T, lockModeType: LockModeType): T

    fun saveAndRefresh(entity: T): T
    fun saveAndRefresh(entity: T, lockModeType: LockModeType): T

    fun saveAndFlushAndRefresh(entity: T): T
    fun saveAndFlushAndRefresh(entity: T, lockModeType: LockModeType): T
}