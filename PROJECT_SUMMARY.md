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

## 4. 현재 진행 상황
*   ✅ **Base Implementation**: Spring Boot 프로젝트 초기 설정 및 `Transaction` 엔티티/리포지토리 구현 완료.
*   ✅ **Database Design**: 전체 스키마(DDL) 및 샘플 데이터(DML) 구축 완료.
*   ✅ **Sample Analysis**: 수동으로 분석된 지출 리포트 샘플(`analysis_report.md`) 존재 (식비 58.5% 집중 등의 인사이트 도출).
*   ⚠️ **Architecture**: 현재 Controller가 Repository를 직접 호출하는 2계층 구조. (Service 레이어 도입 필요)

## 5. 향후 과제 및 고민사항 (Next Steps)
1.  **Service 레이어 도입**: 비즈니스 로직(거래 시 잔액 검증, 기간별 합계 등) 분리.
2.  **AI Integration**: Gemini API를 연동하여 가계부 데이터를 프롬프트로 변환하고 분석 리포트를 자동 생성하는 로직 구현.
3.  **Global Exception Handling**: 금융 데이터 특성을 고려한 예외 처리 시스템 구축.
4.  **Security**: 사용자 인증 및 인가(Spring Security) 도입 예정.

## 6. 조언이 필요한 부분
*   금융 데이터의 무결성을 보장하기 위한 Service 레이어 설계 베스트 프랙티스.
*   효과적인 지출 분석을 위한 Gemini API 프롬프트 엔지니어링 전략.
*   대량의 거래 데이터 처리 시 성능 최적화 방안.
