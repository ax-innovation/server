# 금융상품 추천 REST API

금융상품 추천 및 시뮬레이션 기능을 제공하는 Spring Boot REST API 서버입니다.

## 브랜치 구조
| 브랜치 | 설명 | 상태 |
|---|---|---|
| `main` | 안정적인 기본 버전 | ✅ 배포 가능 |
| `feature/portfolio` | 포트폴리오 배분 기능, 나이 필터링, 청년도약계좌 부분납입 계산 | 🚧 개발 중 |

## 기술 스택
- Java 17
- Spring Boot 3.5
- Spring Data JPA
- MySQL 8.0
- Gradle

## 실행 전 준비
- crawler 저장소의 수집기를 먼저 실행해서 DB에 데이터를 채워야 합니다
- MySQL 8.0이 설치되어 있어야 합니다
- JDK 17이 설치되어 있어야 합니다

## 실행 방법

### 1. 저장소 받아오기
```bash
git clone https://github.com/ax-innovation/server.git
cd server
```

### 2. 브랜치 선택
```bash
# 기본 버전 (main)
git checkout main

# 포트폴리오 기능 버전
git checkout feature/portfolio
```

### 3. 설정 파일 생성

**Windows:**
```bash
copy src\main\resources\application.example.yml src\main\resources\application.yml
```
**Mac/Linux:**
```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
```

`application.yml` 열어서 아래 항목 입력:
```yaml
spring:
  datasource:
    password: MySQL_비밀번호
```

### 4. 실행

**IntelliJ:**
1. server 폴더 열기
2. Gradle 새로고침 (오른쪽 Gradle 탭 → 🔄 버튼)
3. FinapiApplication.java 우클릭 → Run

**터미널:**
```bash
# Windows
.\gradlew.bat bootRun

# Mac/Linux
./gradlew bootRun
```

서버 실행 후 http://localhost:8080 에서 동작합니다.

## API 명세

### 상품 추천 + 시뮬레이션
```
POST /api/v1/recommend
Content-Type: application/json
```

**저축 요청 예시 (일반 모드):**
```json
{
  "age": 28,
  "annualIncome": 36000000,
  "monthlyDeposit": 500000,
  "termMonths": 12,
  "purpose": "저축",
  "productTypes": ["적금", "청년도약계좌"],
  "portfolioMode": false
}
```

**저축 요청 예시 (포트폴리오 모드):**
```json
{
  "age": 28,
  "annualIncome": 36000000,
  "monthlyDeposit": 1000000,
  "termMonths": 12,
  "purpose": "저축",
  "productTypes": ["청년도약계좌", "적금"],
  "portfolioMode": true,
  "allocation": {
    "청년도약계좌": 700000,
    "적금": 300000
  }
}
```

**대출 요청 예시:**
```json
{
  "age": 28,
  "annualIncome": 36000000,
  "loanAmount": 200000000,
  "loanTermMonths": 120,
  "purpose": "대출",
  "productTypes": ["주택담보대출", "디딤돌대출"],
  "portfolioMode": false
}
```

## 시뮬레이션 계산 방식
| 상품 | 계산 방식 |
|---|---|
| 적금 (단리) | 월납입액 × (연금리/12) × n(n+1)/2 |
| 적금 (월복리) | FV = PMT × [(1+r)^n - 1] / r |
| 정기예금 | 원금 × 금리 × 기간/12 |
| 청년도약계좌 | 납입 기간별 이자 + 정부기여금 (소득 구간별 자동 계산) |
| 대출 (원리금균등) | M = P × r(1+r)^n / [(1+r)^n - 1] |
