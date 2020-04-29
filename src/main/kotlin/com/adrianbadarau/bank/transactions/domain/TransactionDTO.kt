package com.adrianbadarau.bank.transactions.domain

import java.math.BigDecimal

data class TransactionDTO(
    var reference: Int? = null,
    var accountNumber: String? = null,
    var description: String? = null,
    var startBalance: BigDecimal? = null,
    var mutation: BigDecimal? = null,
    var endBalance: BigDecimal? = null
)
