# 📝 Code Change Review & Commit Reference

이 문서는 테스트 데이터 생성 기능 구현을 위해 변경된 사항들을 정리한 리뷰 가이드입니다.

## 1. 변경 요약
*   **기능 추가**: 대량의 테스트용 거래 데이터 자동 생성 API 구현.
*   **리팩토링**: 데이터 생성을 용이하게 하기 위해 기존 DTO에 빌더 패턴 적용.
*   **데이터 무결성**: 지출 데이터 생성 시 발생할 수 있는 '잔액 부족' 예외를 방지하기 위한 초기 충전 로직 포함.

## 2. 상세 변경 내역

### 📂 DTO: `TransactionCreateRequest.java` (수정)
*   **변경 내용**: `@AllArgsConstructor`, `@Builder` 어노테이션 추가.
*   **사유**: `TestDataService`에서 다양한 필드 조합으로 요청 객체를 생성해야 하므로, 생성자 대신 가독성이 좋은 빌더 패턴을 도입함.

### 📂 Service: `TestDataService.java` (신규)
*   **주요 로직**:
    *   **잔액 확보**: 본격적인 데이터 생성 전, `INCOME` 타입으로 1,000만 원을 먼저 생성하여 `INSUFFICIENT_BALANCE` 에러 방지.
    *   **랜덤 데이터**: 최근 90일 내의 날짜, 랜덤 카테고리, 현실적인 금액(수입은 백만 단위, 지출은 천~만 단위)을 조합하여 생성.
    *   **재사용성**: 기존 `TransactionService.createTransaction`을 호출하여 비즈니스 유효성 검사 및 잔액 업데이트 로직을 그대로 유지함.

### 📂 Controller: `TestDataController.java` (신규)
*   **엔드포인트**: `POST /api/test/data/generate`
*   **파라미터**: `userId` (필수), `count` (기본값 30).
*   **용도**: 프론트엔드 개발 전이나 AI 프롬프트 테스트를 위한 빠른 데이터 셋업 도구.

## 3. 기술적 결정 사항 (ADR)
*   **Transactional**: `TestDataService`에 `@Transactional`을 적용하여 데이터 생성 중 오류 발생 시 전체 롤백되도록 함.
*   **Validation Bypass**: 별도의 우회 로직을 만드는 대신, '초기 고액 수입'을 먼저 생성하는 방식을 선택하여 기존의 엄격한 잔액 검증 로직을 유지하면서도 목적을 달성함.

## 5. 추가 작업: Gemini API 연동 및 AI 분석 기능 구현

### 1) 변경 요약
*   **Gemini API 통합**: Spring Boot 3의 `RestClient`를 활용하여 Google Gemini API 연동 클라이언트 구현.
*   **AI 리포트 기능**: 사용자의 최근 30일 소비 데이터를 분석하여 맞춤형 금융 조언을 제공하는 서비스 추가.

### 2) 상세 변경 내역

#### ⚙️ 설정 (`application.properties`)
*   `gemini.api.key`: 환경 변수(`GEMINI_API_KEY`)를 통해 안전하게 주입받도록 설정.
*   `gemini.api.url`: Gemini 1.5 Flash 모델 엔드포인트 등록.

#### 📂 DTO: `GeminiRequest.java`, `GeminiResponse.java`
*   Google AI API 스펙에 맞춘 Request/Response 객체 생성. 내부 클래스와 팩토리 메서드를 활용하여 구조를 단순화.

#### 📂 Client: `GeminiClient.java`
*   `RestClient`를 사용하여 HTTP POST 요청 처리.
*   의존성을 최소화하기 위해 무거운 외부 SDK 대신 내장 클라이언트 활용.

#### 📂 Repository: `TransactionRepository.java`
*   `findByUserIdAndTransactionAtAfterOrderByTransactionAtDesc`: 특정 기간(최근 30일)의 거래 내역을 최신순으로 조회하는 쿼리 메서드 추가.

#### 📂 Service: `AiReportService.java`
*   **데이터 파이프라인**: 조회된 거래 내역을 프롬프트 주입용 텍스트로 변환.
*   **프롬프트 엔지니어링**: AI에게 '전문 금융 분석 비서' 역할을 부여하고, 소비 패턴 요약, 절약 조언, 금융 건강도 점수를 도출하도록 지시.

#### 📂 Controller: `AiReportController.java`
*   `GET /api/ai/report/monthly`: 사용자별 월간 AI 분석 보고서 생성 API.

### 3) 기술적 결정 사항 (ADR)
*   **Lightweight Client**: Spring 생태계와의 일관성과 가벼운 의존성을 위해 `RestTemplate`이나 `WebClient` 대신 최신 `RestClient` 채택.
*   **Prompt Design**: 응답의 일관성을 높이기 위해 AI의 역할(Persona), 입력 데이터 포맷, 출력 요구사항을 명확히 분리하여 프롬프트 작성.
