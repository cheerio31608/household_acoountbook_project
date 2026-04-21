package com.finance.budget_buddy.controller;

import com.finance.budget_buddy.entity.Transaction;
import com.finance.budget_buddy.repository.TransactionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TestController {

    private final TransactionRepository transactionRepository;

    public TestController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/test-db")
    public List<Transaction> testDb() {
        return transactionRepository.findAll();
    }
}