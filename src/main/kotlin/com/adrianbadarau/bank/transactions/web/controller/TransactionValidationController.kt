package com.adrianbadarau.bank.transactions.web.controller

import com.adrianbadarau.bank.transactions.domain.ValidationResponse
import com.adrianbadarau.bank.transactions.service.TransactionValidationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/")
class TransactionValidationController(private val transactionValidationService: TransactionValidationService) {
    @PostMapping("transactions/validate")
    fun validateTransactions(@RequestBody file: MultipartFile): ResponseEntity<ValidationResponse> {
        return ResponseEntity.ok(transactionValidationService.validateTransactionUpload(file))
    }
}
