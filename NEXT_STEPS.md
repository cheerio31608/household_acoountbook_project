# 🚀 Household Accountbook Project: Future Roadmap

이 가이드는 프로젝트를 실무 금융 서비스 수준으로 끌어올리기 위한 구체적인 단계별 로드맵입니다.

## 1단계: 데이터 API 연동 (Data Ingestion) 🏗️
AI가 유의미한 분석을 하려면 '풍부한 재료'가 필요합니다.
*   **Mock 데이터 생성 API**: 대량의 거래 내역을 자동으로 생성하여 DB에 주입하는 관리자용 API 구현.
*   **CSV/Excel 업로드**: 사용자의 기존 소비 내역 파일(CSV)을 읽어 `TransactionService.createTransaction`을 통해 일괄 저장하는 기능.
*   **통계 최적화 (QueryDSL)**: 기간별, 카테고리별 통계를 효율적으로 추출하기 위한 쿼리 레이어 구축.

## 2단계: Gemini API 연동 (AI Analysis) 🤖
준비된 데이터를 요리하여 가치 있는 인사이트를 도출합니다.
*   **프롬프트 엔지니어링**: 단순 내역 나열이 아닌, 1단계에서 가공한 통계 데이터(예: "식비 비중 20% 증가")를 포함한 고도화된 프롬프트 설계.
*   **비동기 처리 (@Async)**: 외부 API 응답 대기 시간 동안 사용자가 멈춰있지 않도록 비동기 처리 도입.
*   **리포트 저장 및 재사용**: 생성된 분석 결과를 `AI_Reports` 테이블에 저장하여 중복 비용 방지 및 히스토리 제공.

## 🏗️ 브랜치 전략
*   `feature/transaction-api`: 데이터 대량 주입 및 조회 로직 완성.
*   `feature/ai-integration`: Gemini 연동 및 AI 분석 리포트 기능 구현.

---
**💡 현재 상태**: 서비스의 핵심 뼈대(Service + Exception + Test)가 완성되었으므로, 이제 데이터를 채우는 1단계부터 진행하는 것을 권장합니다.
