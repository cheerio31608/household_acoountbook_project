# 💰 Project Summary: Budget Buddy (AI Household Accountbook)

다른 LLM이나 개발자에게 프로젝트의 현재 상태를 공유하고 조언을 구하기 위한 요약 문서입니다.

## 1. 프로젝트 개요
*   **목표**: 단순한 가계부 기록을 넘어, AI(Gemini)를 활용하여 사용자의 소비 패턴을 분석하고 금융 조언을 제공하는 지능형 가계부 서비스.
*   **핵심 가치**: 데이터 무결성(금융 IT 역량), 아키텍처 확장성, AI 기반 인사이트 제공.

## 2. 기술 스택
*   **Backend**: Java 17, Spring Boot 3.x
*   **Data**: Spring Data JPA, H2 Database (개발용) / PostgreSQL (운영 고려)
*   **AI**: Google Gemini API (연동 예정)
*   **Build Tool**: Gradle

## 3. 데이터베이스 설계 (Core Schema)
프로젝트는 SQLD 수준의 정교한 관계형 데이터베이스 구조를 지향합니다.
*   **Users**: 회원 정보 관리
*   **Categories**: 수입/지출 카테고리 (사용자별 커스텀 지원)
*   **Transactions**: 거래 내역 (금액, 전 잔액, 유형, 가맹점 정보 포함)
*   **AI Reports**: AI가 생성한 분석 결과 저장

## 4. 진행 타임라인 및 현재 상황
*   **📅 2026-04-28 (오늘)**
    *   ✅ **아키텍처 고도화**: Controller -> Service -> Repository 3계층 구조 확립.
    *   ✅ **데이터 무결성 강화**: `Transaction` 테이블에 `balance_after` 스냅샷 도입 및 `User.balance` 원자적 갱신 로직 구현.
    *   ✅ **전역 예외 처리**: `@RestControllerAdvice`와 커스텀 `BusinessException`을 이용한 에러 핸들링 체계 구축.
    *   ✅ **품질 보증**: Mockito를 활용한 `TransactionService` 단위 테스트 완료 (비즈니스 로직 검증).

## 5. 향후 과제 및 고민사항 (Next Steps)
1.  **데이터 수집 자동화 (Data Ingestion)**: 대량의 Mock 데이터 생성 API 또는 CSV 파일 업로드 기능 구현.
2.  **통계 쿼리 최적화**: QueryDSL을 도입하여 AI 분석을 위한 기간별/카테고리별 요약 데이터 추출.
3.  **AI Integration**: Gemini API 연동 및 비동기(`@Async`) 리포트 생성 로직 구현.

## 6. 조언이 필요한 부분
*   금융 데이터의 무결성을 보장하기 위한 Service 레이어 설계 베스트 프랙티스.
*   효과적인 지출 분석을 위한 Gemini API 프롬프트 엔지니어링 전략.
*   대량의 거래 데이터 처리 시 성능 최적화 방안.
