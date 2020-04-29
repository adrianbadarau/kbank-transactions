package com.adrianbadarau.bank.transactions.domain

data class ValidationResponse(
    var failedItems: List<TransactionDTO>? = null,
    var success: Boolean = false
)
