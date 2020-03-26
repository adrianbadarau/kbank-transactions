package com.adrianbadarau.bank.transactions.products_api

import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A ClientAccount.
 */
data class ClientAccount(
    var id: String? = null,
    var iban: String? = null,
    var name: String? = null,
    var ballance: BigDecimal = BigDecimal.ZERO,
    var user: String? = null,
    var type: Any? = null,
    var initialCredit: BigDecimal? = null
)
