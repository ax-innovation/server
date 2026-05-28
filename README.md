# 금융상품 추천 REST API

금융상품 추천 및 시뮬레이션 기능을 제공하는 Spring Boot REST API 서버입니다.

## 기술 스택
- Java 17
- Spring Boot 3.5
- Spring Data JPA
- MySQL 8.0
- Gradle

## 실행 전 준비
- crawler 저장소의 수집기를 먼저 실행해서 DB에 데이터를 채워야 합니다

## 실행 방법

### 1. 저장소 받아오기
```bash
git clone https://github.com/ax-innovation/server.git
cd server
```

### 2. 설정 파일 생성
```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
```
`application.yml` 열어서 아래 항목 입력:
```yaml
spring:
  datasource:
    password: MySQL_비밀번호
```

### 3. 실행
```bash
./gradlew bootRun
```
서버가 실행되면 http://localhost:8080 에서 동작합니다.

## API 명세

### 상품 추천 + 시뮬레이션
POST /api/v1/recommend

저축 요청 예시:
```json
{
  "age": 28,
  "annualIncome": 36000000,
  "monthlyDeposit": 500000,
  "termMonths": 12,
  "purpose": "저축",
  "productTypes": ["적금", "청년도약계좌"]
}
```

대출 요청 예시:
```json
{
  "age": 28,
  "annualIncome": 36000000,
  "loanAmount": 200000000,
  "loanTermMonths": 120,
  "purpose": "대출",
  "productTypes": ["주택담보대출", "디딤돌대출"]
}
```

## 시뮬레이션 계산 방식
| 상품 | 계산 방식 |
|---|---|
| 적금 (단리) | 월납입액 × (연금리/12) × n(n+1)/2 |
| 적금 (월복리) | FV = PMT × [(1+r)^n - 1] / r |
| 정기예금 | 원금 × 금리 × 기간/12 |
| 청년도약계좌 | 납입액 + 이자 + 정부기여금 (소득 구간별 자동 계산) |
| 대출 (원리금균등) | M = P × r(1+r)^n / [(1+r)^n - 1] |
