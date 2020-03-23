package com.adrianbadarau.bank.transactions.domain

import com.adrianbadarau.bank.transactions.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TransactionTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Transaction::class)
        val transaction1 = Transaction()
        transaction1.id = 1L
        val transaction2 = Transaction()
        transaction2.id = transaction1.id
        assertThat(transaction1).isEqualTo(transaction2)
        transaction2.id = 2L
        assertThat(transaction1).isNotEqualTo(transaction2)
        transaction1.id = null
        assertThat(transaction1).isNotEqualTo(transaction2)
    }
}
