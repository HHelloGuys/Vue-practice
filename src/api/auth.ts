export interface AuthUser {
  userId: number
  email: string
  name: string
}

export interface AuthSession {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: AuthUser
}

const SESSION_KEY = 'nova-auth-session'

async function parseError(response: Response) {
  const body = await response.json().catch(() => null) as { message?: string } | null
  return body?.message ?? '요청을 처리하지 못했습니다.'
}

export async function register(email: string, password: string, name: string): Promise<void> {
  const response = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password, name })
  })
  if (!response.ok) throw new Error(await parseError(response))
}

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

export function getAuthSession(): AuthSession | null {
  const stored = sessionStorage.getItem(SESSION_KEY)
  return stored ? JSON.parse(stored) as AuthSession : null
}

export function getAccessToken(): string | null {
  return getAuthSession()?.accessToken ?? null
}

export function clearAuthSession(): void {
  sessionStorage.removeItem(SESSION_KEY)
}
