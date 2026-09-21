import os
import re
from dataclasses import dataclass
from typing import Iterable, List


TOKEN_PATTERN = re.compile(r"[가-힣A-Za-z0-9]{2,}")
RAG_TOP_K = int(os.getenv("RAG_TOP_K", "2"))
RAG_CONTEXT_CHARS = int(os.getenv("RAG_CONTEXT_CHARS", "500"))


@dataclass(frozen=True)
class RetrievedChunk:
    file_name: str
    content: str
    score: int


def retrieve(message: str, chunks: Iterable[dict], top_k: int = RAG_TOP_K) -> List[RetrievedChunk]:
    """사용자 질문과 단어가 겹치는 청크를 점수순으로 선택한다."""
    query_terms = set(TOKEN_PATTERN.findall(message.lower()))
    if not query_terms:
        return []

    ranked = []
    for chunk in chunks:
        content = str(chunk.get("content", ""))
        content_terms = set(TOKEN_PATTERN.findall(content.lower()))
        score = len(query_terms & content_terms)
        if score > 0:
            ranked.append(RetrievedChunk(
                file_name=str(chunk.get("fileName", "알 수 없는 문서")),
                # 로컬 CPU 모델에 과도한 문맥을 보내지 않도록 검색 청크를 한 번 더 제한한다.
                content=content[:RAG_CONTEXT_CHARS],
                score=score,
            ))

    ranked.sort(key=lambda item: item.score, reverse=True)
    return ranked[:top_k]
