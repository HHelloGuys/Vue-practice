from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

app = FastAPI()


# Vue 개발 서버의 요청 허용
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
    print("Vue에서 받은 메시지:", request.message)

    return {
        "answer": f"FastAPI가 받은 메시지입니다: {request.message}"
    }