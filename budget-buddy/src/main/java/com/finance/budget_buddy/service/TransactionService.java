package com.finance.budget_buddy.service;

import com.finance.budget_buddy.entity.Transaction;
import com.finance.budget_buddy.exception.BusinessException;
import com.finance.budget_buddy.exception.ErrorCode;
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
        // 사용자 검증 로직 (예시: userId가 0 이하일 수 없음)
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionType(userId, "EXPENSE");
        
        if (transactions.isEmpty()) {
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
