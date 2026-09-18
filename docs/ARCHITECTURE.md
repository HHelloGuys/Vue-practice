# System Architecture

## Overview

Vue 3 + TypeScript + eGovFramework + Oracle + Python/FastAPI + RAG + LLM 기반 AI Chat 시스템.

---

## High-Level Architecture

User
↓
Vue 3 + TypeScript
↓
eGovFramework
├─ Oracle
└─ FastAPI
      ↓
   RAG Pipeline
      ↓
   Vector Search
      ↓
     LLM
      ↓
   AI Response

---

## Frontend

위치:
`src/`

기술:
- Vue 3
- TypeScript
- Vite
- CSS

역할:
- 사용자 화면
- 로그인 UI
- AI Chat UI
- 대화 목록
- 메시지 표시
- 지식/문서 관련 UI
- eGovFramework REST API 호출

특별한 이유가 없다면 FastAPI를 직접 호출하지 않는다.

---

## Main Backend

위치:
`backend/`

기술:
- Java
- eGovFramework

역할:
- 사용자 관리
- 인증/인가
- 대화 관리
- 메시지 관리
- 비즈니스 로직
- Oracle 연동
- FastAPI AI Server 호출
- Vue용 REST API 제공

계층 구조는 실제 eGovFramework 프로젝트 구조를 우선한다.

예상 흐름:

Controller
↓
Service
↓
DAO / Mapper
↓
Oracle

실제 프로젝트가 다른 구조라면 기존 구조를 따른다.

---

## AI Server

위치:
`ai/`

기술:
- Python
- FastAPI

역할:
- Document Processing
- Chunking
- Embedding
- Vector Retrieval
- RAG
- Prompt 생성
- LLM 호출
- AI Response 생성

FastAPI는 메인 웹 Backend가 아니다.

AI 처리 전용 서비스로 사용한다.

---

## Chat Request Flow

1. 사용자가 Vue에서 메시지를 입력한다.
2. Vue가 eGovFramework REST API를 호출한다.
3. eGovFramework가 사용자 및 대화 정보를 확인한다.
4. 사용자 메시지를 Oracle에 저장한다.
5. AI 처리가 필요한 경우 FastAPI를 호출한다.
6. FastAPI에서 RAG 검색을 수행한다.
7. 검색 결과와 사용자 질문으로 Prompt를 구성한다.
8. LLM을 호출한다.
9. FastAPI가 AI 응답을 eGovFramework에 반환한다.
10. eGovFramework가 AI 응답을 Oracle에 저장한다.
11. Vue에 응답을 반환한다.
12. Vue가 AI 답변을 출력한다.

---

## Development Ports

현재 기본 개발 포트:

Vue:
5173

eGovFramework:
8080

FastAPI:
8000

Oracle:
1521

실제 설정값이 다르면 실제 설정을 우선한다.

---

## Current Development Execution

현재:

npm run dev

실행 시:

Vue :5173
FastAPI :8000

를 동시에 실행한다.

eGovFramework 프로젝트가 구성되면 개발 실행 명령에 추가하는 것을 고려한다.

최종 목표:

npm run dev
├─ Vue
├─ eGovFramework
└─ FastAPI