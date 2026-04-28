package com.finance.budget_buddy.service;

import com.finance.budget_buddy.entity.Transaction;
import com.finance.budget_buddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * 모든 거래 내역을 조회합니다.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * 특정 사용자의 총 지출액을 계산합니다.
     */
    public java.math.BigDecimal getTotalExpenseByUserId(Long userId) {
        return transactionRepository.findByUserIdAndTransactionType(userId, "EXPENSE")
                .stream()
                .map(Transaction::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
