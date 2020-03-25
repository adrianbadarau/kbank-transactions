package com.adrianbadarau.bank.transactions.web.rest

import com.adrianbadarau.bank.transactions.domain.Transaction
import com.adrianbadarau.bank.transactions.service.TransactionService
import com.adrianbadarau.bank.transactions.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "transactionsTransaction"

/**
 * REST controller for managing [com.adrianbadarau.bank.transactions.domain.Transaction].
 */
@RestController
@RequestMapping("/api")
class TransactionResource(
    private val transactionService: TransactionService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /transactions` : Create a new transaction.
     *
     * @param transaction the transaction to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new transaction, or with status `400 (Bad Request)` if the transaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transactions")
    fun createTransaction(@Valid @RequestBody transaction: Transaction): ResponseEntity<Transaction> {
        log.debug("REST request to save Transaction : {}", transaction)
        if (transaction.id != null) {
            throw BadRequestAlertException(
                "A new transaction cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = transactionService.save(transaction)
        return ResponseEntity.created(URI("/api/transactions/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /transactions` : Updates an existing transaction.
     *
     * @param transaction the transaction to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated transaction,
     * or with status `400 (Bad Request)` if the transaction is not valid,
     * or with status `500 (Internal Server Error)` if the transaction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transactions")
    fun updateTransaction(@Valid @RequestBody transaction: Transaction): ResponseEntity<Transaction> {
        log.debug("REST request to update Transaction : {}", transaction)
        if (transaction.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = transactionService.save(transaction)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    transaction.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /transactions` : get all the transactions.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of transactions in body.
     */
    @GetMapping("/transactions")
    fun getAllTransactions(
        @RequestParam(required = false, name = "accountId") accountId: String?,
        pageable: Pageable
    ): ResponseEntity<MutableList<Transaction>> {
        log.debug("REST request to get a page of Transactions")
        val page = transactionService.findAll(pageable, accountId)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /transactions/:id` : get the "id" transaction.
     *
     * @param id the id of the transaction to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the transaction, or with status `404 (Not Found)`.
     */
    @GetMapping("/transactions/{id}")
    fun getTransaction(@PathVariable id: Long): ResponseEntity<Transaction> {
        log.debug("REST request to get Transaction : {}", id)
        val transaction = transactionService.findOne(id)
        return ResponseUtil.wrapOrNotFound(transaction)
    }

    /**
     *  `DELETE  /transactions/:id` : delete the "id" transaction.
     *
     * @param id the id of the transaction to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/transactions/{id}")
    fun deleteTransaction(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Transaction : {}", id)
        transactionService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
