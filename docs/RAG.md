# RAG Architecture

## Status

현재 RAG 설계 및 초기 구현 단계.

확정되지 않은 기술이나 설정을 임의로 결정하지 않는다.

---

## AI Server

위치:
`ai/`

기술:
- Python
- FastAPI

AI Server는 AI/RAG 처리 전용 서비스다.

---

## RAG Pipeline

기본 흐름:

Document
↓
Text Extraction
↓
Chunking
↓
Embedding
↓
Vector Storage
↓
Retrieval
↓
Prompt
↓
LLM
↓
Response

---

## Document Processing

사용자가 등록한 문서를 AI 검색에 사용할 수 있도록 처리한다.

지원 파일 형식은 실제 구현 단계에서 결정한다.

---

## Chunking

문서 텍스트를 검색 가능한 Chunk 단위로 분리한다.

다음 값은 테스트 없이 임의로 확정하지 않는다.

- Chunk Size
- Chunk Overlap
- Chunk 분할 방식

---

## Embedding

Embedding Model은 실제 구현 단계에서 결정한다.

특정 모델을 임의로 하드코딩하지 않는다.

---

## Vector Storage

Vector 저장 방식은 아직 확정하지 않는다.

가능한 구조에는 다음이 포함될 수 있으나 현재 단계에서 임의 결정하지 않는다.

- Oracle Vector 기능
- 별도 Vector Database
- 기타 Vector Store

실제 요구사항과 테스트 결과를 기준으로 결정한다.

---

## Retrieval

사용자 질문과 관련된 Chunk를 검색한다.

다음 값은 테스트 후 결정한다.

- Top-K
- Similarity 기준
- Retrieval 전략
- Metadata Filtering

---

## Prompt

기본적인 Prompt 구성:

System Prompt
+
Retrieved Context
+
Conversation Context
+
User Question

Prompt는 가능한 경우 중앙에서 관리한다.

동일한 Prompt를 여러 파일에 중복 작성하지 않는다.

---

## Conversation Context

AI Chat은 이전 대화를 활용할 수 있다.

단, 전체 대화를 무조건 LLM에 전달하지 않는다.

향후 다음 방식을 고려할 수 있다.

- 최근 메시지 사용
- 대화 요약
- Token 제한
- 필요한 Context 선택

실제 방식은 구현 및 테스트 후 결정한다.

---

## LLM

LLM Provider와 Model은 구현 단계에서 결정한다.

특정 Provider 또는 Model을 임의로 고정하지 않는다.

API Key 및 인증정보는 환경변수로 관리한다.

---

## RAG Rules

- 검색하지 않은 내용을 검색 결과처럼 만들지 않는다.
- Retrieved Context와 LLM 생성 답변을 구분한다.
- Embedding Model을 임의 변경하지 않는다.
- Vector DB를 임의 선택하지 않는다.
- Chunk 설정을 임의 확정하지 않는다.
- Prompt를 여러 위치에 중복 구현하지 않는다.
- API Key를 코드에 저장하지 않는다.
- 사용자별 지식 데이터가 섞이지 않도록 설계한다.

---

## Planned AI Structure

ai/
├─ .venv/
├─ main.py
├─ api/
├─ services/
│  ├─ rag.py
│  ├─ embedding.py
│  ├─ retrieval.py
│  └─ llm.py
├─ prompts/
└─ core/

실제 구현 과정에서 필요한 구조만 추가한다.

사용하지 않는 폴더나 계층을 미리 과도하게 생성하지 않는다.