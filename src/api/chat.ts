export interface ChatResponse {
  answer: string
}

export async function requestChatAnswer(message: string): Promise<ChatResponse> {
  const accessToken = getAccessToken()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json'
  }

  // 로그인한 경우에만 JWT를 전달하고 비로그인 요청에는 인증 헤더를 넣지 않는다.
  if (accessToken) {
    headers.Authorization = `Bearer ${accessToken}`
  }

  // 개발 중에는 Vite 프록시가 이 요청을 eGovFramework 8080 포트로 전달한다.
  const response = await fetch('/api/chat', {
    method: 'POST',
    headers,
    body: JSON.stringify({ message })
  })

  if (response.status === 401) {
    throw new Error('인증이 만료되었습니다. 다시 로그인해 주세요.')
  }

  if (!response.ok) {
    throw new Error('채팅 응답을 가져오지 못했습니다.')
  }

  return response.json() as Promise<ChatResponse>
}
import { getAccessToken } from './auth'
