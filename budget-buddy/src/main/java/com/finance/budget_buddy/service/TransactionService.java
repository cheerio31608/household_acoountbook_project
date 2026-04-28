package com.finance.budget_buddy.service;

import com.finance.budget_buddy.dto.TransactionCreateRequest;
import com.finance.budget_buddy.entity.Category;
import com.finance.budget_buddy.entity.Transaction;
import com.finance.budget_buddy.entity.User;
import com.finance.budget_buddy.exception.BusinessException;
import com.finance.budget_buddy.exception.ErrorCode;
import com.finance.budget_buddy.repository.CategoryRepository;
import com.finance.budget_buddy.repository.TransactionRepository;
import com.finance.budget_buddy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, 
                              UserRepository userRepository, 
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 새로운 거래를 생성하고 사용자의 잔액을 원자적으로 업데이트합니다.
     */
    @Transactional
    public Transaction createTransaction(TransactionCreateRequest request) {
        // 1. 사용자 존재 여부 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 카테고리 유효성 검사
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND)); // 적절한 에러 코드로 대체 가능

        // 3. 미래 날짜 검증
        if (request.getTransactionAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 4. 잔액 부족 검사 (지출인 경우만)
        if ("EXPENSE".equals(request.getTransactionType()) && 
            user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 5. 거래 내역 저장
        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = "INCOME".equals(request.getTransactionType()) ? 
                balanceBefore.add(request.getAmount()) : 
                balanceBefore.subtract(request.getAmount());

        Transaction transaction = Transaction.builder()
                .userId(user.getUserId())
                .categoryId(category.getCategoryId())
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .transactionType(request.getTransactionType())
                .description(request.getDescription())
                .transactionAt(request.getTransactionAt())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // 6. 사용자 잔액 업데이트 (더티 체킹 활용)
        user.updateBalance(request.getAmount(), request.getTransactionType());

        return savedTransaction;
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
