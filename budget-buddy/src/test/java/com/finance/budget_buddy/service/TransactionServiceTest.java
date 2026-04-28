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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .balance(new BigDecimal("10000.00"))
                .build();

        category = Category.builder()
                .categoryId(1L)
                .name("식비")
                .type("EXPENSE")
                .build();
    }

    @Test
    @DisplayName("정상적인 지출 거래 생성 시 잔액이 차감되고 내역이 저장된다")
    void createTransaction_Success_Expense() {
        // given
        TransactionCreateRequest request = createRequest(1000L, "EXPENSE");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Transaction result = transactionService.createTransaction(request);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo("1000.00");
        assertThat(result.getBalanceAfter()).isEqualByComparingTo("9000.00");
        assertThat(user.getBalance()).isEqualByComparingTo("9000.00");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("잔액보다 큰 금액을 출금하려 하면 INSUFFICIENT_BALANCE 예외가 발생한다")
    void createTransaction_Fail_InsufficientBalance() {
        // given
        TransactionCreateRequest request = createRequest(20000L, "EXPENSE");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // when & then
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_BALANCE);
    }

    @Test
    @DisplayName("미래 날짜로 거래를 생성하려 하면 INVALID_INPUT_VALUE 예외가 발생한다")
    void createTransaction_Fail_FutureDate() {
        // given
        TransactionCreateRequest request = createRequest(1000L, "EXPENSE", LocalDateTime.now().plusDays(1));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // when & then
        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
    }

    private TransactionCreateRequest createRequest(long amount, String type) {
        return createRequest(amount, type, LocalDateTime.now());
    }

    private TransactionCreateRequest createRequest(long amount, String type, LocalDateTime time) {
        TransactionCreateRequest request = new TransactionCreateRequest();
        ReflectionTestUtils.setField(request, "userId", 1L);
        ReflectionTestUtils.setField(request, "categoryId", 1L);
        ReflectionTestUtils.setField(request, "amount", new BigDecimal(amount));
        ReflectionTestUtils.setField(request, "transactionType", type);
        ReflectionTestUtils.setField(request, "transactionAt", time);
        return request;
    }
}
