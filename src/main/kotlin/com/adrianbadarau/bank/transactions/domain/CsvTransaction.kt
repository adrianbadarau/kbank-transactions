package com.adrianbadarau.bank.transactions.domain

import com.opencsv.bean.CsvBindByName

data class CsvTransaction(
    @CsvBindByName(column = "Reference")
    var reference: String? = null,
    @CsvBindByName(column = "Account Number")
    var accountNumber: String? = null,
    @CsvBindByName(column = "Description")
    var description: String? = null,
    @CsvBindByName(column = "Start Balance", locale = "nl-NL")
    var startBalance: String? = null,
    @CsvBindByName(column = "Mutation", locale = "nl-NL")
    var mutation: String? = null,
    @CsvBindByName(column = "End Balance", locale = "nl-NL")
    var endBalance: String? = null
)
