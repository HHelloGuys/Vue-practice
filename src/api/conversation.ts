import { getAccessToken } from './auth'

/** 최근 대화 목록에 표시할 대화 요약을 표현한다. */
export interface ConversationSummary {
  conversationId: number
  title: string
}

/** Oracle에서 불러온 저장 메시지를 표현한다. */
export interface StoredMessage {
  messageId: number
  role: 'user' | 'assistant'
  content: string
  createdAt: string
}

/** 로그인 전용 API에 전달할 Bearer 인증 헤더를 생성한다. */
function authorizationHeaders() {
  const token = getAccessToken()
  if (!token) throw new Error('로그인이 필요합니다.')
  return { Authorization: `Bearer ${token}` }
}

/** 현재 사용자가 저장한 대화 목록을 가져온다. */
export async function getConversations(): Promise<ConversationSummary[]> {
  const response = await fetch('/api/conversations', { headers: authorizationHeaders() })
  if (response.status === 401) throw new Error('인증이 만료되었습니다. 다시 로그인해 주세요.')
  if (!response.ok) throw new Error('대화 목록을 가져오지 못했습니다.')
  return response.json() as Promise<ConversationSummary[]>
}

/** 선택한 대화에 저장된 메시지 목록을 가져온다. */
export async function getConversationMessages(conversationId: number): Promise<StoredMessage[]> {
  const response = await fetch(`/api/conversations/${conversationId}/messages`, {
    headers: authorizationHeaders()
  })
  if (response.status === 401) throw new Error('인증이 만료되었습니다. 다시 로그인해 주세요.')
  if (!response.ok) throw new Error('대화 내용을 가져오지 못했습니다.')
  return response.json() as Promise<StoredMessage[]>
}
