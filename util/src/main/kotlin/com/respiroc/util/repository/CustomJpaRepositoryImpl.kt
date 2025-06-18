package com.respiroc.util.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

class CustomJpaRepositoryImpl<T : Any, ID : Serializable?>(entityInformation: JpaEntityInformation<T, ID>, private val entityManager: EntityManager)
    : SimpleJpaRepository<T, ID>(entityInformation, entityManager), CustomJpaRepository<T, ID>
{
    @Transactional
    override fun saveAndFlushAndRefresh(entity: T) : T {
        return refresh(saveAndFlush(entity))
    }

    @Transactional
    override fun saveAndFlushAndRefresh(entity: T, lockModeType: LockModeType) : T {
        return refresh(saveAndFlush(entity), lockModeType)
    }

    @Transactional
    override fun saveAndRefresh(entity: T) : T {
        return refresh(save(entity))
    }

    @Transactional
    override fun saveAndRefresh(entity: T, lockModeType: LockModeType) : T {
        return refresh(save(entity), lockModeType)
    }

    @Transactional
    override fun refresh(entity: T) : T {
        entityManager.refresh(entity)

        return entity
    }

    @Transactional
    override fun refresh(entity: T, lockModeType: LockModeType) : T {
        entityManager.refresh(entity, lockModeType)

        return entity
    }
}