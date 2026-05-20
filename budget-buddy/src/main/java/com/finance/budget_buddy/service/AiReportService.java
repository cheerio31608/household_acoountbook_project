package com.finance.budget_buddy.service;

import com.finance.budget_buddy.entity.Transaction;
import com.finance.budget_buddy.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiReportService {

    private final GeminiClient geminiClient;
    private final TransactionRepository transactionRepository;

    /**
     * 사용자의 최근 한 달간 소비 패턴을 분석하여 보고서를 생성합니다.
     */
    public String generateMonthlyReport(Long userId) {
        // 1. 데이터 수집 (최근 30일)
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTransactionAtAfterOrderByTransactionAtDesc(userId, oneMonthAgo);

        if (transactions.isEmpty()) {
            return "최근 30일간의 거래 내역이 없어 분석을 진행할 수 없습니다.";
        }

        // 2. 프롬프트 생성 (데이터 가공)
        String transactionSummary = transactions.stream()
                .map(t -> String.format("- %s: [%s] %s %s원 (%s)",
                        t.getTransactionAt().toLocalDate(),
                        t.getTransactionType(),
                        t.getDescription(),
                        t.getAmount().toPlainString(),
                        t.getCategoryId())) // 카테고리 ID 대신 이름을 넘기면 더 좋으나 일단 ID로 진행
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
                당신은 전문적인 금융 분석 비서 'Budget Buddy AI'입니다.
                아래는 사용자의 최근 30일간 가계부 거래 내역입니다.
                
                ### 거래 내역:
                %s
                
                ### 요청 사항:
                1. 소비 패턴 요약 (어느 카테고리에서 지출이 가장 많은지 등)
                2. 절약을 위한 구체적인 조언 3가지
                3. 사용자의 금융 건강도 점수 (100점 만점)와 짧은 총평
                
                친절하고 전문적인 말투로 답변해 주세요. 한국어로 작성해 주세요.
                """, transactionSummary);

        // 3. Gemini API 호출 및 결과 반환
        return geminiClient.generateContent(prompt);
    }
}
