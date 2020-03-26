package com.adrianbadarau.bank.transactions.repository

import com.adrianbadarau.bank.transactions.domain.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Transaction] entity.
 */
@Suppress("unused")
@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findAllByAccountIdEquals(accountId: String, pageable: Pageable): Page<Transaction>
    fun findAllByAccountIdIn(accountId: List<String>, pageable: Pageable): Page<Transaction>
}
