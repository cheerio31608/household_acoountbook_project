/*******************************************************************************
 * 가계부 서비스 데이터베이스 설계 (Household Account Book)
 * 
 * [설계 원칙]
 * 1. 데이터 무결성: 모든 핵심 필드에 제약 조건(NOT NULL, CHECK 등) 적용
 * 2. 확장성: 향후 분석 기능을 고려한 거래 일시 및 카테고리 구조 설계
 * 3. 가독성: 테이블 및 컬럼별 한글 주석(COMMENT) 상세 명시
 *******************************************************************************/

-- =============================================================================
-- 1. 회원 관리 (Users)
-- =============================================================================
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS '사용자 기본 정보 테이블';
COMMENT ON COLUMN users.user_id IS '사용자 고유 ID (PK)';
COMMENT ON COLUMN users.email IS '로그인 이메일 (계정명)';
COMMENT ON COLUMN users.password_hash IS '암호화된 비밀번호 해시값';
COMMENT ON COLUMN users.created_at IS '가입 일시';


-- =============================================================================
-- 2. 카테고리 관리 (Categories)
-- =============================================================================
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- INCOME / EXPENSE

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_category_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

COMMENT ON TABLE categories IS '수입/지출 분류 카테고리';
COMMENT ON COLUMN categories.category_id IS '카테고리 ID (PK)';
COMMENT ON COLUMN categories.user_id IS '소유자 ID (FK)';
COMMENT ON COLUMN categories.name IS '카테고리명 (예: 식비, 월급)';
COMMENT ON COLUMN categories.type IS '거래 유형 (INCOME: 수입, EXPENSE: 지출)';
COMMENT ON COLUMN categories.created_at IS '생성 일시';


-- =============================================================================
-- 3. 거래 내역 (Transactions)
-- =============================================================================

-- 3.1. 테이블 기본 구조
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    amount DECIMAL(15,2) NOT NULL, -- 금액
    balance_before DECIMAL(15,2) NOT NULL, -- 거래 전 잔액

    transaction_type VARCHAR(20) NOT NULL, -- INCOME / EXPENSE
    description TEXT,

    transaction_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- 3.2. 속성 추가 및 비즈니스 제약 조건
ALTER TABLE transactions
ADD COLUMN vendor_name VARCHAR(255),
ADD COLUMN location VARCHAR(255);

ALTER TABLE transactions
ADD CONSTRAINT chk_amount_positive
CHECK (amount > 0);

ALTER TABLE transactions
ADD CONSTRAINT chk_balance_before_non_negative
CHECK (balance_before >= 0);

-- 3.3. 성능 최적화를 위한 인덱스
-- (사용자별 최신 거래 내역 조회가 빈번함)
CREATE INDEX idx_transactions_user_time
ON transactions (user_id, transaction_at DESC);

-- 3.4. 거래 내역 상세 코멘트
COMMENT ON TABLE transactions IS '사용자의 수입 및 지출 거래 내역 테이블';
COMMENT ON COLUMN transactions.transaction_id IS '거래 고유 ID (PK)';
COMMENT ON COLUMN transactions.user_id IS '사용자 ID (FK)';
COMMENT ON COLUMN transactions.category_id IS '카테고리 ID (FK)';
COMMENT ON COLUMN transactions.amount IS '거래 금액 (항상 양수)';
COMMENT ON COLUMN transactions.balance_before IS '거래 발생 직전 잔액';
COMMENT ON COLUMN transactions.transaction_type IS '거래 유형 (INCOME / EXPENSE)';
COMMENT ON COLUMN transactions.description IS '거래 설명';
COMMENT ON COLUMN transactions.vendor_name IS '가맹점 또는 거래처 이름';
COMMENT ON COLUMN transactions.location IS '거래 발생 위치';
COMMENT ON COLUMN transactions.transaction_at IS '거래 발생 일시';
COMMENT ON COLUMN transactions.created_at IS '데이터 생성 일시';


-- =============================================================================
-- 4. AI 분석 리포트 (AI Reports)
-- =============================================================================
CREATE TABLE ai_reports (
    report_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,

    report_type VARCHAR(50) NOT NULL, -- 예: MONTHLY_SUMMARY
    report_content TEXT NOT NULL,

    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_report_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

COMMENT ON TABLE ai_reports IS '금융 데이터 기반 AI 자동 생성 리포트';
COMMENT ON COLUMN ai_reports.report_id IS '리포트 고유 ID (PK)';
COMMENT ON COLUMN ai_reports.user_id IS '대상 사용자 ID (FK)';
COMMENT ON COLUMN ai_reports.report_type IS '분석 유형 (월간 요약, 소비 패턴 등)';
COMMENT ON COLUMN ai_reports.report_content IS 'AI 분석 결과 텍스트';
COMMENT ON COLUMN ai_reports.generated_at IS '생성 일시';


-- =============================================================================
-- 5. 초기 마스터 데이터 및 샘플 내역 (DML)
-- =============================================================================

-- 5.1. 테스트 유저
INSERT INTO users (user_id, email, password_hash)
VALUES (1, 'user1@test.com', 'pw');

-- 5.2. 기본 카테고리
INSERT INTO categories (category_id, user_id, name, type)
VALUES
(1, 1, '수입', 'INCOME'),
(2, 1, '식비', 'EXPENSE'),
(3, 1, '교통비', 'EXPENSE');

-- 5.3. 샘플 거래 내역 (10건)
INSERT INTO transactions (
    transaction_id, user_id, category_id,
    amount, balance_before,
    transaction_type, description,
    vendor_name, location,
    transaction_at, created_at
) VALUES
(101, 1, 1, 2500000.00, 0.00, 'INCOME', '4월 월급', '네이버', '서울', '2026-04-01 09:00:00', CURRENT_TIMESTAMP),
(102, 1, 2, 4500.00, 2500000.00, 'EXPENSE', '아메리카노', '스타벅스 신촌점', '서울 서대문구', '2026-04-01 10:30:00', CURRENT_TIMESTAMP),
(103, 1, 2, 12000.00, 2495500.00, 'EXPENSE', '점심 식사', '김밥천국 신촌점', '서울 서대문구', '2026-04-01 12:10:00', CURRENT_TIMESTAMP),
(104, 1, 2, 23000.00, 2483500.00, 'EXPENSE', '저녁 배달', '배달의민족', '서울', '2026-04-01 19:20:00', CURRENT_TIMESTAMP),
(105, 1, 2, 56000.00, 2460500.00, 'EXPENSE', '생활용품 구매', '쿠팡', '온라인', '2026-04-02 21:00:00', CURRENT_TIMESTAMP),
(106, 1, 3, 1250.00, 2404500.00, 'EXPENSE', '지하철 이용', '티머니', '서울', '2026-04-03 08:10:00', CURRENT_TIMESTAMP),
(107, 1, 2, 4800.00, 2403250.00, 'EXPENSE', '카페 라떼', '스타벅스 홍대점', '서울 마포구', '2026-04-03 15:20:00', CURRENT_TIMESTAMP),
(108, 1, 2, 13900.00, 2398450.00, 'EXPENSE', '음악 스트리밍', '멜론', '온라인', '2026-04-04 00:05:00', CURRENT_TIMESTAMP),
(109, 1, 2, 35000.00, 2384550.00, 'EXPENSE', '저녁 외식', '교촌치킨 합정점', '서울 마포구', '2026-04-05 18:30:00', CURRENT_TIMESTAMP),
(110, 1, 1, 15000.00, 2349550.00, 'INCOME', '상품 환불', '쿠팡', '온라인', '2026-04-06 11:00:00', CURRENT_TIMESTAMP);
