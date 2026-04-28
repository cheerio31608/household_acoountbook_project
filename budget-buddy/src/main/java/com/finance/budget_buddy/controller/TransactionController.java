package com.finance.budget_buddy.controller;

import com.finance.budget_buddy.dto.TransactionCreateRequest;
import com.finance.budget_buddy.entity.Transaction;
import com.finance.budget_buddy.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 새로운 거래 내역을 생성합니다.
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionCreateRequest request) {
        Transaction transaction = transactionService.createTransaction(request);
        return ResponseEntity.ok(transaction);
    }
}
