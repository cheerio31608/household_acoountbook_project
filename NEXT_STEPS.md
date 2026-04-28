# 🚀 Household Accountbook Project: Future Roadmap

이 파일은 프로젝트의 완성도를 높이고 금융 IT 서비스로서의 역량을 강화하기 위한 단계별 가이드라인입니다.

## 1. 아키텍처 개선: Service 레이어 도입
*   **목표**: `Controller` -> `Service` -> `Repository` 계층 구조 확립 (관심사 분리)
*   **주요 작업**:
    *   비즈니스 로직(금액 계산, 데이터 검증 등)을 처리하는 `Service` 클래스 생성
    *   특정 기간 지출 합계 계산 로직 구현
    *   거래 전/후 잔액 무결성 검증 로직 추가

## 2. AI 역량 강화: LLM (Gemini API) 연동
*   **목표**: 가계부 데이터를 기반으로 한 AI 금융 분석 리포트 자동 생성
*   **주요 작업**:
    *   Google AI Studio에서 Gemini API Key 발급 및 보안 설정 (`.env` 또는 로컬 설정)
    *   `WebClient` 또는 `RestTemplate`을 사용한 API 클라이언트 구현
    *   최근 거래 내역을 프롬프트로 변환하여 AI 분석 결과 수신 및 저장

## 3. 시스템 안정성: 전역 예외 처리 (Exception Handling)
*   **목표**: 에러 발생 시 사용자에게 일관되고 정돈된 응답 제공
*   **주요 작업**:
    *   `@RestControllerAdvice`를 이용한 Global Exception Handler 구현
    *   커스텀 에러 객체(`ErrorResponse`) 정의
    *   금융 데이터 특성에 맞는 예외 상황(잔액 부족, 잘못된 카테고리 등) 처리

## 4. 문서화 및 포트폴리오 최적화
*   **목표**: 프로젝트의 가치와 기술적 역량을 시각적으로 전달
*   **주요 작업**:
    *   `README.md` 작성: 목적, 기술 스택, 핵심 기능 요약
    *   ERD 설계도 이미지 첨부 (SQLD 역량 어필)
    *   API 명세서 작성

---
**💡 실행 팁**: 작업을 시작할 때 `feature/service-layer`와 같이 브랜치를 나누어 진행하고, 완료 후 `main`에 머지하는 습관을 들이세요.
