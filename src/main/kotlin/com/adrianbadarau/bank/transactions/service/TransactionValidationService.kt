package com.adrianbadarau.bank.transactions.service

import com.adrianbadarau.bank.transactions.domain.CsvTransaction
import com.adrianbadarau.bank.transactions.domain.TransactionDTO
import com.adrianbadarau.bank.transactions.domain.ValidationResponse
import com.adrianbadarau.bank.transactions.utils.CsvTransactionMapper
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStreamReader
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

/**
 * Validation service for transactions, will check if there are any transactions in the CSV file that have duplicate
 * references or balance < 0
 */
@Service
class TransactionValidationService {
    /**
     * Main method to be used for validation, pass to this the file from the FE, and you will get a response DTO
     */
    fun validateTransactionUpload(csv: MultipartFile): ValidationResponse {
        val validationResponse = ValidationResponse()
        validationResponse.success = true
        val transactionReferences = ArrayList<Int>()
        val invalidTransactions = readCsv(csv).filter { isInvalidTransaction(it, transactionReferences) }
        if (invalidTransactions.isNotEmpty()) {
            validationResponse.failedItems = invalidTransactions
            validationResponse.success = false
        }
        return validationResponse
    }

    private fun readCsv(csv: MultipartFile): List<TransactionDTO> {
        InputStreamReader(csv.inputStream).use { reader ->
            return CsvToBeanBuilder<CsvTransaction>(reader).withType(CsvTransaction::class.java).build().parse()
                .stream().map(CsvTransactionMapper::convertToDTO).collect(Collectors.toList())
        }
    }

    private fun isInvalidTransaction(transaction: TransactionDTO, transactionReferences: ArrayList<Int>): Boolean {
        if (transactionReferences.contains(transaction?.reference)) {
            return true
        }
        transactionReferences.add(transaction.reference!!)
        return transaction.endBalance?.compareTo(BigDecimal.ZERO)!! < 0
    }
}
