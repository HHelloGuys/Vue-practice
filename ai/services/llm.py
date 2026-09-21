import json
import os
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "qwen3:4b-instruct")


class LlmConnectionError(RuntimeError):
    """로컬 LLM 서버에 연결할 수 없을 때 발생한다."""


def generate_answer(message: str, contexts=None) -> str:
    """Ollama의 무료 로컬 모델에 사용자 질문을 전달한다."""
    contexts = contexts or []
    context_text = "\n\n".join(
        f"[출처: {context.file_name}]\n{context.content}"
        for context in contexts
    )
    user_content = message
    if context_text:
        user_content = (
            "아래 참고 문서에 근거해 질문에 답하세요. 문서에 없는 내용은 "
            "없다고 명확히 말하세요.\n\n"
            f"참고 문서:\n{context_text}\n\n질문: {message}"
        )
    payload = {
        "model": OLLAMA_MODEL,
        "stream": False,
        "messages": [
            {
                "role": "system",
                "content": (
                    "당신은 Nova AI입니다. 사용자의 질문에 한국어로 명확하고 "
                    "간결하게 답변하세요. 모르는 내용은 추측하지 마세요. "
                    "긴 답변은 2~4문장마다 빈 줄을 넣어 문단을 구분하세요. "
                    "여러 항목을 설명할 때는 각 항목을 줄바꿈한 목록으로 작성하세요."
                ),
            },
            {"role": "user", "content": user_content},
        ],
    }

    request = Request(
        f"{OLLAMA_BASE_URL}/api/chat",
        data=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )

    try:
        # FastAPI의 동기 엔드포인트는 작업 스레드에서 실행되므로 이 호출은 이벤트 루프를 막지 않는다.
        with urlopen(request, timeout=120) as response:
            result = json.loads(response.read().decode("utf-8"))
    except (HTTPError, URLError, TimeoutError) as error:
        raise LlmConnectionError("Ollama에 연결할 수 없습니다.") from error

    answer = result.get("message", {}).get("content", "").strip()
    if not answer:
        raise LlmConnectionError("Ollama가 빈 응답을 반환했습니다.")

    return answer
