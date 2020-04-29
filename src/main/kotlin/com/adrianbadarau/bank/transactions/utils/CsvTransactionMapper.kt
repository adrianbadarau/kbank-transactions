package com.adrianbadarau.bank.transactions.utils

import com.adrianbadarau.bank.transactions.domain.CsvTransaction
import com.adrianbadarau.bank.transactions.domain.TransactionDTO
import java.math.BigDecimal

object CsvTransactionMapper {
    fun convertToDTO(transaction: CsvTransaction): TransactionDTO {
        val dto = TransactionDTO()
        dto.reference = Integer.valueOf(transaction.reference)
        dto.description = transaction.description
        dto.accountNumber = transaction.accountNumber
        dto.startBalance = BigDecimal(transaction.startBalance)
        dto.mutation = BigDecimal(transaction.mutation)
        dto.endBalance = BigDecimal(transaction.endBalance)
        return dto
    }
}
