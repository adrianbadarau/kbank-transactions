package com.adrianbadarau.bank.transactions.domain

import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "account_id", nullable = false)
    var accountId: String? = null,

    @get: NotNull
    @Column(name = "value", precision = 21, scale = 2, nullable = false)
    var value: BigDecimal? = null,

    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: Instant? = null,

    @get: NotNull
    @Column(name = "details", nullable = false)
    var details: String? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transaction) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Transaction{" +
        "id=$id" +
        ", accountId='$accountId'" +
        ", value=$value" +
        ", date='$date'" +
        ", details='$details'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
