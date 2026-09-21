/** 로그인한 사용자 식별 정보를 표현한다. */
export interface AuthUser {
  userId: number
  email: string
  name: string
}

/** 브라우저에 보관할 JWT 인증 세션을 표현한다. */
export interface AuthSession {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: AuthUser
}

// 인증 세션을 저장할 sessionStorage 키이다.
const SESSION_KEY = 'nova-auth-session'

/** 백엔드 JSON 오류 응답에서 사용자에게 표시할 메시지를 추출한다. */
async function parseError(response: Response) {
  const body = await response.json().catch(() => null) as { message?: string } | null
  return body?.message ?? '요청을 처리하지 못했습니다.'
}

/** 회원가입 정보를 eGovFramework 인증 API로 전송한다. */
export async function register(email: string, password: string, name: string): Promise<void> {
  const response = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password, name })
  })
  if (!response.ok) throw new Error(await parseError(response))
}

/** 로그인하고 반환된 JWT 인증 세션을 브라우저에 저장한다. */
export async function login(email: string, password: string): Promise<AuthSession> {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  })
  if (!response.ok) throw new Error(await parseError(response))

  const session = await response.json() as AuthSession
  // 브라우저를 닫으면 토큰이 사라지도록 sessionStorage에만 보관한다.
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session))
  return session
}

/** 브라우저에 저장된 인증 세션을 읽는다. */
export function getAuthSession(): AuthSession | null {
  const stored = sessionStorage.getItem(SESSION_KEY)
  return stored ? JSON.parse(stored) as AuthSession : null
}

/** API Authorization 헤더에 사용할 JWT를 반환한다. */
export function getAccessToken(): string | null {
  return getAuthSession()?.accessToken ?? null
}

/** 로그아웃 시 브라우저의 인증 세션을 삭제한다. */
export function clearAuthSession(): void {
  sessionStorage.removeItem(SESSION_KEY)
}
