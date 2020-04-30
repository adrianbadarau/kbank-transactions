package com.adrianbadarau.bank.transactions.web.rest

import com.adrianbadarau.bank.transactions.TransactionsApp
import com.adrianbadarau.bank.transactions.client.ClientAccountsFeignClient
import com.adrianbadarau.bank.transactions.domain.Transaction
import com.adrianbadarau.bank.transactions.products_api.ClientAccount
import com.adrianbadarau.bank.transactions.repository.TransactionRepository
import com.adrianbadarau.bank.transactions.service.TransactionService
import com.adrianbadarau.bank.transactions.web.rest.errors.ExceptionTranslator
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [TransactionResource] REST controller.
 *
 * @see TransactionResource
 */
@SpringBootTest(classes = [TransactionsApp::class])
class TransactionResourceIT {

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var transactionService: TransactionService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restTransactionMockMvc: MockMvc

    private lateinit var transaction: Transaction

    @MockBean
    private lateinit var clientAccountsFeignClient: ClientAccountsFeignClient

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val transactionResource = TransactionResource(transactionService)
        this.restTransactionMockMvc = MockMvcBuilders.standaloneSetup(transactionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()

        val clientAccount = ClientAccount(id = DEFAULT_ACCOUNT_ID)
        given(clientAccountsFeignClient.getCustomerAccount(any())).willReturn(clientAccount)
        given(clientAccountsFeignClient.updateAccountBalance(any())).willReturn(clientAccount)
    }

    @BeforeEach
    fun initTest() {
        transaction = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTransaction() {
        val databaseSizeBeforeCreate = transactionRepository.findAll().size

        // Create the Transaction
        restTransactionMockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isCreated)

        // Validate the Transaction in the database
        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1)
        val testTransaction = transactionList[transactionList.size - 1]
        assertThat(testTransaction.accountId).isEqualTo(DEFAULT_ACCOUNT_ID)
        assertThat(testTransaction.value).isEqualTo(DEFAULT_VALUE)
        assertThat(testTransaction.date).isEqualTo(DEFAULT_DATE)
        assertThat(testTransaction.details).isEqualTo(DEFAULT_DETAILS)
    }

    @Test
    @Transactional
    fun createTransactionWithExistingId() {
        val databaseSizeBeforeCreate = transactionRepository.findAll().size

        // Create the Transaction with an existing ID
        transaction.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isBadRequest)

        // Validate the Transaction in the database
        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkAccountIdIsRequired() {
        val databaseSizeBeforeTest = transactionRepository.findAll().size
        // set the field null
        transaction.accountId = null

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isBadRequest)

        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkValueIsRequired() {
        val databaseSizeBeforeTest = transactionRepository.findAll().size
        // set the field null
        transaction.value = null

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isBadRequest)

        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = transactionRepository.findAll().size
        // set the field null
        transaction.date = null

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isBadRequest)

        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDetailsIsRequired() {
        val databaseSizeBeforeTest = transactionRepository.findAll().size
        // set the field null
        transaction.details = null

        // Create the Transaction, which fails.

        restTransactionMockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isBadRequest)

        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllTransactions() {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction)
        val accounts = listOf(ClientAccount(id = DEFAULT_ACCOUNT_ID))
        given(clientAccountsFeignClient.getAllClientAccounts()).willReturn(accounts)

        // Get all the transactionList
        restTransactionMockMvc.perform(get("/api/transactions?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.id?.toInt())))
            .andExpect(jsonPath("$.[*].accountId").value(hasItem(DEFAULT_ACCOUNT_ID)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
    }

    @Test
    @Transactional
    fun getTransaction() {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction)

        val id = transaction.id
        assertNotNull(id)

        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.accountId").value(DEFAULT_ACCOUNT_ID))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toInt()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
    }

    @Test
    @Transactional
    fun getNonExistingTransaction() {
        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateTransaction() {
        val clientAccount = ClientAccount(id = UPDATED_ACCOUNT_ID)
        given(clientAccountsFeignClient.getCustomerAccount(UPDATED_ACCOUNT_ID)).willReturn(clientAccount)
        given(clientAccountsFeignClient.updateAccountBalance(ClientAccount(id = UPDATED_ACCOUNT_ID, ballance = DEFAULT_VALUE + UPDATED_VALUE))).willReturn(clientAccount)
        // Initialize the database
        transactionService.save(transaction)

        val databaseSizeBeforeUpdate = transactionRepository.findAll().size

        // Update the transaction
        val id = transaction.id
        assertNotNull(id)
        val updatedTransaction = transactionRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction)
        updatedTransaction.accountId = UPDATED_ACCOUNT_ID
        updatedTransaction.value = UPDATED_VALUE
        updatedTransaction.date = UPDATED_DATE
        updatedTransaction.details = UPDATED_DETAILS

        restTransactionMockMvc.perform(
            put("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedTransaction))
        ).andExpect(status().isOk)

        // Validate the Transaction in the database
        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate)
        val testTransaction = transactionList[transactionList.size - 1]
        assertThat(testTransaction.accountId).isEqualTo(UPDATED_ACCOUNT_ID)
        assertThat(testTransaction.value).isEqualTo(UPDATED_VALUE)
        assertThat(testTransaction.date).isEqualTo(UPDATED_DATE)
        assertThat(testTransaction.details).isEqualTo(UPDATED_DETAILS)
    }

    @Test
    @Transactional
    fun updateNonExistingTransaction() {
        val databaseSizeBeforeUpdate = transactionRepository.findAll().size

        // Create the Transaction

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc.perform(
            put("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(transaction))
        ).andExpect(status().isBadRequest)

        // Validate the Transaction in the database
        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteTransaction() {
        // Initialize the database
        transactionService.save(transaction)

        val databaseSizeBeforeDelete = transactionRepository.findAll().size

        val id = transaction.id
        assertNotNull(id)

        // Delete the transaction
        restTransactionMockMvc.perform(
            delete("/api/transactions/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val transactionList = transactionRepository.findAll()
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_ACCOUNT_ID = "AAAAAAAAAA"
        private const val UPDATED_ACCOUNT_ID = "BBBBBBBBBB"

        private val DEFAULT_VALUE: BigDecimal = BigDecimal(1)
        private val UPDATED_VALUE: BigDecimal = BigDecimal(2)

        private val DEFAULT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_DETAILS = "AAAAAAAAAA"
        private const val UPDATED_DETAILS = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Transaction {
            val transaction = Transaction(
                accountId = DEFAULT_ACCOUNT_ID,
                value = DEFAULT_VALUE,
                date = DEFAULT_DATE,
                details = DEFAULT_DETAILS
            )

            return transaction
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Transaction {
            val transaction = Transaction(
                accountId = UPDATED_ACCOUNT_ID,
                value = UPDATED_VALUE,
                date = UPDATED_DATE,
                details = UPDATED_DETAILS
            )

            return transaction
        }
    }
}
