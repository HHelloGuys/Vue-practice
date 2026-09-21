import { getAccessToken } from './auth'

/** 지식 문서 등록 결과와 생성된 청크 개수를 표현한다. */
export interface KnowledgeUploadResponse {
  message: string
  chunkCount: number
}

/** 선택한 문서를 JWT와 함께 eGovFramework 지식 API로 업로드한다. */
export async function uploadKnowledgeDocument(file: File): Promise<KnowledgeUploadResponse> {
  const token = getAccessToken()
  if (!token) throw new Error('로그인 후 문서를 등록할 수 있습니다.')

  const formData = new FormData()
  formData.append('file', file)
  const response = await fetch('/api/knowledge/documents', {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData
  })
  const body = await response.json().catch(() => null) as Partial<KnowledgeUploadResponse> | null
  if (response.status === 401) throw new Error('인증이 만료되었습니다. 다시 로그인해 주세요.')
  if (!response.ok) throw new Error(body?.message ?? '문서를 등록하지 못했습니다.')
  return body as KnowledgeUploadResponse
}
