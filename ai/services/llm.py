import json
import os
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "qwen3:4b-instruct")


class LlmConnectionError(RuntimeError):
    """로컬 LLM 서버에 연결할 수 없을 때 발생한다."""


def generate_answer(message: str) -> str:
    """Ollama의 무료 로컬 모델에 사용자 질문을 전달한다."""
    payload = {
        "model": OLLAMA_MODEL,
        "stream": False,
        "messages": [
            {
                "role": "system",
                "content": (
                    "당신은 Nova AI입니다. 사용자의 질문에 한국어로 명확하고 "
                    "간결하게 답변하세요. 모르는 내용은 추측하지 마세요."
                ),
            },
            {"role": "user", "content": message},
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
