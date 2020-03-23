package com.adrianbadarau.bank.transactions.service
import com.adrianbadarau.bank.transactions.domain.Transaction
import com.adrianbadarau.bank.transactions.repository.TransactionRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Transaction].
 */
@Service
@Transactional
class TransactionService(
    private val transactionRepository: TransactionRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a transaction.
     *
     * @param transaction the entity to save.
     * @return the persisted entity.
     */
    fun save(transaction: Transaction): Transaction {
        log.debug("Request to save Transaction : {}", transaction)
        return transactionRepository.save(transaction)
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Transaction> {
        log.debug("Request to get all Transactions")
        return transactionRepository.findAll(pageable)
    }

    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Transaction> {
        log.debug("Request to get Transaction : {}", id)
        return transactionRepository.findById(id)
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Transaction : {}", id)

        transactionRepository.deleteById(id)
    }
}
