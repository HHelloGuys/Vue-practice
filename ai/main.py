from fastapi import FastAPI
from fastapi import HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

from ai.services.llm import LlmConnectionError, generate_answer

app = FastAPI()


# 현재는 eGovFramework만 호출하지만 개발 진단을 위해 로컬 요청을 허용한다.
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class ChatRequest(BaseModel):
    message: str


@app.get("/")
def root():
    return {
        "message": "FastAPI 서버 정상 실행!"
    }


@app.post("/api/chat")
def chat(request: ChatRequest):
    print("eGovFramework에서 받은 메시지:", request.message)

    try:
        # 실제 답변은 API Key가 필요 없는 로컬 Ollama 모델에서 생성한다.
        return {
            "answer": generate_answer(request.message)
        }
    except LlmConnectionError as error:
        raise HTTPException(status_code=503, detail=str(error)) from error
