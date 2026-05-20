package com.finance.budget_buddy.service;

import com.finance.budget_buddy.dto.TransactionCreateRequest;
import com.finance.budget_buddy.entity.Category;
import com.finance.budget_buddy.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TestDataService {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;
    private final Random random = new Random();

    /**
     * 특정 사용자를 위해 랜덤한 거래 데이터를 생성합니다.
     */
    @Transactional
    public void generateRandomTransactions(Long userId, int count) {
        List<Category> categories = categoryRepository.findAll();
        
        // 1. 잔액 부족 방지를 위한 초기 고액 수입 생성 (1,000만원)
        transactionService.createTransaction(TransactionCreateRequest.builder()
                .userId(userId)
                .categoryId(1L) // '수입' 카테고리 (Seed 데이터 기준 1번)
                .amount(BigDecimal.valueOf(10000000))
                .transactionType("INCOME")
                .description("테스트용 초기 잔액 충전")
                .transactionAt(LocalDateTime.now().minusDays(91)) // 가장 오래된 데이터로 설정
                .build());

        // 2. 랜덤 데이터 생성
        for (int i = 0; i < count; i++) {
            Category category = categories.get(random.nextInt(categories.size()));
            String type = category.getType();
            BigDecimal amount = generateRandomAmount(type);
            LocalDateTime date = LocalDateTime.now().minusDays(random.nextInt(90))
                    .minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60));

            TransactionCreateRequest request = TransactionCreateRequest.builder()
                    .userId(userId)
                    .categoryId(category.getCategoryId())
                    .amount(amount)
                    .transactionType(type)
                    .description(category.getName() + " - 테스트 데이터 " + (i + 1))
                    .transactionAt(date)
                    .build();

            transactionService.createTransaction(request);
        }
    }

    private BigDecimal generateRandomAmount(String type) {
        if ("INCOME".equals(type)) {
            // 수입은 100만원 ~ 300만원 사이 (만원 단위)
            int val = 100 + random.nextInt(201);
            return BigDecimal.valueOf(val * 10000);
        } else {
            // 지출은 1,000원 ~ 100,000원 사이 (백원 단위)
            int val = 10 + random.nextInt(991);
            return BigDecimal.valueOf(val * 100);
        }
    }
}
