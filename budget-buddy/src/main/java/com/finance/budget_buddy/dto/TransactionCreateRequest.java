package com.finance.budget_buddy.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TransactionCreateRequest {
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private String transactionType; // INCOME / EXPENSE
    private String description;
    private LocalDateTime transactionAt;
}
