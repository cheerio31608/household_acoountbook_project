package com.finance.budget_buddy.repository;

import com.finance.budget_buddy.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdAndTransactionType(Long userId, String transactionType);
}