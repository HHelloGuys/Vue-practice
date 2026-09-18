# Project Instructions

## Project Goal

Vue 3 + TypeScript + eGovFramework + Oracle + Python/FastAPI + RAG + LLM 기반 AI Chat 프로젝트.

사용자 계정별 AI 대화 저장과 지식 기반 RAG 기능을 구현한다.

---

## Tech Stack

Frontend:
- Vue 3
- TypeScript
- Vite
- CSS

Main Backend:
- Java
- eGovFramework
- REST API

Database:
- Oracle

AI Server:
- Python
- FastAPI

AI:
- RAG
- Embedding
- Vector Search
- LLM

---

## Architecture

기본 구조:

Vue 3 + TypeScript
→ eGovFramework
→ Oracle / FastAPI
→ RAG
→ LLM

역할:

- Vue: 화면 및 사용자 입력
- eGovFramework: 메인 백엔드 및 비즈니스 로직
- Oracle: 사용자, 대화, 메시지 및 서비스 데이터
- FastAPI: AI/RAG 전용 API
- Python: RAG, Embedding, Retrieval, LLM 처리

특별한 이유가 없다면 Vue에서 FastAPI를 직접 호출하지 않는다.

Vue
→ eGovFramework
→ FastAPI

구조를 기본으로 한다.

---

## Directories

- `src/` : Vue 3 + TypeScript
- `backend/` : eGovFramework
- `ai/` : Python + FastAPI + RAG
- `docs/` : 상세 설계 문서

실제 프로젝트 구조가 문서와 다르면 실제 코드를 우선한다.

---

## Reference Documents

필요한 작업에서만 관련 문서를 읽는다.

전체 구조:
`docs/ARCHITECTURE.md`

Oracle/DB:
`docs/DATABASE.md`

RAG/LLM:
`docs/RAG.md`

현재 작업과 관계없는 문서는 읽지 않는다.

---

## Core Rules

1. 요청과 직접 관련된 파일만 확인한다.
2. 프로젝트 전체를 매 요청마다 다시 탐색하지 않는다.
3. 이미 확인한 파일을 이유 없이 반복해서 읽지 않는다.
4. 존재하지 않는 코드, API, 테이블, 컬럼을 추측하지 않는다.
5. 정상 동작하는 코드를 불필요하게 변경하지 않는다.
6. 요청 범위만 최소 수정한다.
7. 요청하지 않은 리팩터링을 하지 않는다.
8. 새 라이브러리는 필요한 경우에만 추가한다.
9. 오류 발생 시 실제 오류 메시지와 관련 코드를 먼저 확인한다.
10. 문서와 실제 코드가 충돌하면 실제 코드를 우선한다.
11. 사용자의 기존 구현을 임의로 삭제하거나 대체하지 않는다.

---

## Vue / TypeScript Rules

- Vue 3 사용
- TypeScript 사용
- Composition API 사용
- `<script setup lang="ts">` 사용
- JavaScript로 임의 변환하지 않는다.
- 불필요한 `any` 사용을 피한다.
- 타입 오류를 단순히 숨기기 위한 캐스팅을 남발하지 않는다.
- 기존 UI를 불필요하게 변경하지 않는다.
- CSS는 가능한 경우 별도 `.css` 파일로 관리한다.

---

## eGovFramework Rules

메인 Backend는 eGovFramework 기반 Java 애플리케이션이다.

일반 Spring Boot 프로젝트라고 임의 가정하지 않는다.

현재 프로젝트의:
- eGovFramework 버전
- Spring 구성
- 패키지 구조
- Controller 구조
- Service 구조
- DAO / Mapper 구조
- DB 접근 방식

을 먼저 확인하고 기존 패턴을 따른다.

담당 영역:
- 회원
- 인증/인가
- 대화
- 메시지
- 비즈니스 로직
- Oracle 연동
- FastAPI 호출
- Vue용 REST API

JPA/MyBatis 등의 기술을 임의로 선택하거나 변경하지 않는다.

---

## AI Server Rules

`ai/`는 Python AI 전용 영역이다.

FastAPI 담당:
- RAG
- Document Processing
- Chunking
- Embedding
- Retrieval
- Prompt 구성
- LLM 호출
- AI Response

회원/로그인 등 일반 서비스 로직을 FastAPI로 옮기지 않는다.

AI 작업인 경우에만 `docs/RAG.md`를 확인한다.

---

## Oracle Rules

- Oracle Database 사용
- Oracle SQL 문법 사용
- MySQL/MariaDB/PostgreSQL 문법 혼용 금지
- 기존 테이블/컬럼을 임의 변경하지 않는다.
- PK/FK/INDEX/UNIQUE를 임의 추가하지 않는다.
- 실제 Oracle 버전을 확인하지 않고 버전 의존 기능을 가정하지 않는다.

DB 작업인 경우에만 `docs/DATABASE.md`를 확인한다.

---

## Security

- API Key 하드코딩 금지
- DB 비밀번호 하드코딩 금지
- LLM Key 하드코딩 금지
- 비밀정보는 환경변수 또는 프로젝트의 기존 설정 방식으로 관리한다.
- `.env` 및 인증정보를 Git에 커밋하지 않는다.
- 로그에 비밀번호/API Key를 출력하지 않는다.

---

## Token Efficiency

- 현재 작업에 필요한 파일만 읽는다.
- 관련 없는 문서는 읽지 않는다.
- 같은 파일을 이유 없이 반복해서 읽지 않는다.
- 이미 확인한 내용을 반복 설명하지 않는다.
- 변경하지 않은 전체 코드를 재출력하지 않는다.
- 사용자가 요구하지 않은 긴 설명을 하지 않는다.
- 불필요한 대안을 여러 개 나열하지 않는다.
- 프로젝트 전체를 매 요청마다 다시 분석하지 않는다.

작업 완료 보고는 가능하면 다음만 포함한다:

1. 변경 파일
2. 핵심 변경사항
3. 검증 결과
4. 필요한 경우 다음 작업

---

## Work Process

요청 확인
→ 관련 파일 확인
→ 최소 수정
→ 실행/검증
→ 간단한 결과 보고

추측으로 여러 파일을 동시에 수정하지 않는다.