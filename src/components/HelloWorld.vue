<script setup lang="ts">
import { computed, ref, nextTick, onMounted } from 'vue'
import { requestChatAnswer } from '../api/chat'
import { uploadKnowledgeDocument } from '../api/knowledge'
import { getConversationMessages, getConversations } from '../api/conversation'
import {
  clearAuthSession,
  getAuthSession,
  login,
  register,
  type AuthSession
} from '../api/auth'
import './HelloWorld.css'

// 채팅 입력, 인증 화면, 지식 문서 등록의 반응형 화면 상태이다.
const inputMessage = ref('')
const chatBox = ref<HTMLElement | null>(null)
const isLoading = ref(false)
const authSession = ref<AuthSession | null>(getAuthSession())
const showAuth = ref(false)
const isRegisterMode = ref(false)
const authEmail = ref('')
const authPassword = ref('')
const authName = ref('')
const authError = ref('')
const isAuthLoading = ref(false)
const knowledgeFileInput = ref<HTMLInputElement | null>(null)
const knowledgeStatus = ref('')
const isKnowledgeLoading = ref(false)

/** 화면에 표시할 사용자 또는 AI 메시지를 표현한다. */
interface ChatMessage {
  id: number
  role: 'user' | 'assistant'
  content: string
  time: string
  sources?: string[]
}

/** 하나의 대화와 서버 저장 상태를 표현한다. */
interface Conversation {
  id: number
  serverId?: number
  title: string
  messages: ChatMessage[]
  loaded: boolean
}

/** 새 대화에 처음 표시할 AI 안내 메시지를 생성한다. */
function createInitialMessage(content: string): ChatMessage {
  return {
    id: Date.now(),
    role: 'assistant',
    content,
    time: getTime()
  }
}

/** 아직 서버에 저장되지 않은 새 대화를 생성한다. */
function createConversation(): Conversation {
  return {
    id: Date.now(),
    title: '새로운 대화',
    messages: [
      createInitialMessage('새로운 대화를 시작했습니다. 무엇을 도와드릴까요?')
    ],
    loaded: true
  }
}

// 최초 화면에 표시할 비로그인 대화 상태를 생성한다.
const firstConversation = createConversation()
const conversations = ref<Conversation[]>([firstConversation])
const activeConversationId = ref(firstConversation.id)

// 현재 선택한 대화와 화면에 표시할 메시지를 반응형 계산값으로 제공한다.
const activeConversation = computed(() => {
  return conversations.value.find(
    (conversation) => conversation.id === activeConversationId.value
  ) ?? conversations.value[0]
})

const messages = computed(() => activeConversation.value.messages)

/** 첫 질문을 최근 대화 영역에 표시할 짧은 제목으로 변환한다. */
function createConversationTitle(message: string) {
  // 첫 질문이 길면 최근 대화 영역에 맞게 줄여서 표시한다.
  return message.length > 20 ? `${message.slice(0, 20)}…` : message
}

/** 메시지 옆에 표시할 현재 시각을 한국어 형식으로 반환한다. */
function getTime() {
  return new Date().toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

/** 사용자 질문을 화면에 추가하고 eGovFramework를 통해 AI 답변을 요청한다. */
async function sendMessage() {
  const text = inputMessage.value.trim()

  if (!text || isLoading.value) return

  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: text,
    time: getTime()
  })

  // 첫 사용자 메시지를 해당 대화의 제목으로 사용한다.
  if (activeConversation.value.title === '새로운 대화') {
    activeConversation.value.title = createConversationTitle(text)
  }

  inputMessage.value = ''
  await scrollToBottom()

  isLoading.value = true

  try {
    // Vue는 FastAPI를 직접 호출하지 않고 eGovFramework API만 호출한다.
    const response = await requestChatAnswer(text, activeConversation.value.serverId)

    if (response.conversationId) {
      activeConversation.value.serverId = response.conversationId
    }

    messages.value.push({
      id: Date.now(),
      role: 'assistant',
      content: response.answer,
      time: getTime(),
      sources: response.sources
    })
  } catch (error) {
    if (error instanceof Error && error.message.includes('인증이 만료')) {
      logout()
      authError.value = error.message
      showAuth.value = true
      return
    }

    messages.value.push({
      id: Date.now(),
      role: 'assistant',
      content: '응답을 가져오지 못했습니다. 서버 실행 상태를 확인해 주세요.',
      time: getTime()
    })
  } finally {
    isLoading.value = false
    await scrollToBottom()
  }
}

/** DOM 갱신 후 채팅 영역을 마지막 메시지 위치로 이동한다. */
async function scrollToBottom() {
  await nextTick()

  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

/** 질문이 존재하는 현재 대화를 유지하면서 새 대화를 추가한다. */
function newChat() {
  if (isLoading.value) return

  // 아직 질문하지 않은 새 대화가 있으면 중복 생성하지 않는다.
  const hasUserMessage = activeConversation.value.messages.some(
    (message) => message.role === 'user'
  )

  if (!hasUserMessage) return

  const conversation = createConversation()
  conversations.value.unshift(conversation)
  activeConversationId.value = conversation.id
}

/** 최근 대화를 선택하고 아직 로드하지 않은 메시지를 서버에서 가져온다. */
async function selectConversation(conversationId: number) {
  // 응답을 기다리는 동안 대화를 바꿔 답변 위치가 섞이지 않게 한다.
  if (isLoading.value) return

  activeConversationId.value = conversationId
  const conversation = activeConversation.value
  if (conversation.serverId && !conversation.loaded) {
    try {
      const storedMessages = await getConversationMessages(conversation.serverId)
      conversation.messages = storedMessages.map((message) => ({
        id: message.messageId,
        role: message.role,
        content: message.content,
        time: new Date(message.createdAt).toLocaleTimeString('ko-KR', {
          hour: '2-digit',
          minute: '2-digit'
        })
      }))
      conversation.loaded = true
    } catch (error) {
      authError.value = error instanceof Error ? error.message : '대화를 불러오지 못했습니다.'
    }
  }
  await scrollToBottom()
}

/** 회원가입 또는 로그인을 수행하고 계정 대화를 복원한다. */
async function submitAuth() {
  authError.value = ''
  isAuthLoading.value = true
  try {
    if (isRegisterMode.value) {
      await register(authEmail.value, authPassword.value, authName.value)
    }
    authSession.value = await login(authEmail.value, authPassword.value)
    try {
      await loadAccountConversations()
    } catch (error) {
      if (error instanceof Error && error.message.includes('인증이 만료')) {
        logout()
        throw error
      }
      // 인증 성공과 기존 대화 복원은 별개이므로 조회 실패가 로그인을 취소하지 않게 한다.
      resetToGuestConversation()
      knowledgeStatus.value = error instanceof Error
        ? `로그인은 완료됐지만 ${error.message}`
        : '로그인은 완료됐지만 기존 대화를 불러오지 못했습니다.'
    }
    authPassword.value = ''
    showAuth.value = false
  } catch (error) {
    authError.value = error instanceof Error ? error.message : '인증에 실패했습니다.'
  } finally {
    isAuthLoading.value = false
  }
}

/** 인증 세션을 삭제하고 화면을 비로그인 대화 상태로 전환한다. */
function logout() {
  clearAuthSession()
  authSession.value = null
  resetToGuestConversation()
}

/** 현재 대화 목록을 저장되지 않는 새 게스트 대화로 초기화한다. */
function resetToGuestConversation() {
  const guestConversation = createConversation()
  conversations.value = [guestConversation]
  activeConversationId.value = guestConversation.id
}

/** 로그인 사용자의 저장된 대화 목록과 첫 대화 메시지를 불러온다. */
async function loadAccountConversations() {
  const summaries = await getConversations()
  if (summaries.length === 0) {
    resetToGuestConversation()
    return
  }
  conversations.value = summaries.map((summary) => ({
    id: summary.conversationId,
    serverId: summary.conversationId,
    title: summary.title,
    messages: [],
    loaded: false
  }))
  activeConversationId.value = conversations.value[0].id
  await selectConversation(activeConversationId.value)
}

// 컴포넌트가 표시될 때 기존 로그인 세션이 있으면 계정 대화를 복원한다.
onMounted(async () => {
  if (!authSession.value) return
  try {
    await loadAccountConversations()
  } catch (error) {
    if (error instanceof Error && error.message.includes('인증이 만료')) {
      logout()
      authError.value = error.message
      showAuth.value = true
      return
    }
    resetToGuestConversation()
    knowledgeStatus.value = error instanceof Error
      ? `로그인은 유지됐지만 ${error.message}`
      : '로그인은 유지됐지만 기존 대화를 불러오지 못했습니다.'
  }
})

/** 로그인 모달을 열고 이전 인증 오류를 초기화한다. */
function openAuth() {
  authError.value = ''
  showAuth.value = true
}

/** 숨겨진 지식 문서 파일 선택 창을 연다. */
function openKnowledgeFile() {
  knowledgeStatus.value = ''
  knowledgeFileInput.value?.click()
}

/** 선택된 문서를 업로드하고 처리 결과를 화면에 표시한다. */
async function handleKnowledgeFile(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  isKnowledgeLoading.value = true
  knowledgeStatus.value = '문서를 처리하고 있습니다...'
  try {
    const response = await uploadKnowledgeDocument(file)
    knowledgeStatus.value = `${file.name}: ${response.chunkCount}개 지식 조각 등록 완료`
  } catch (error) {
    knowledgeStatus.value = error instanceof Error ? error.message : '문서를 등록하지 못했습니다.'
  } finally {
    isKnowledgeLoading.value = false
    input.value = ''
  }
}
</script>

<template>
  <!-- 로그인과 회원가입을 전환해서 사용하는 인증 화면이다. -->
  <div v-if="showAuth" class="auth-page">
    <form class="auth-card" @submit.prevent="submitAuth">
      <button type="button" class="auth-close" aria-label="닫기" @click="showAuth = false">×</button>
      <div class="logo-icon">N</div>
      <h1>{{ isRegisterMode ? '회원가입' : '로그인' }}</h1>
      <p>Nova AI를 사용하려면 계정 인증이 필요합니다.</p>

      <input v-if="isRegisterMode" v-model="authName" placeholder="이름" required />
      <input v-model="authEmail" type="email" placeholder="이메일" required />
      <input v-model="authPassword" type="password" placeholder="비밀번호 8자 이상" required />

      <span v-if="authError" class="auth-error">{{ authError }}</span>
      <button class="auth-submit" :disabled="isAuthLoading">
        {{ isAuthLoading ? '처리 중...' : (isRegisterMode ? '가입하고 로그인' : '로그인') }}
      </button>
      <button type="button" class="auth-switch" @click="isRegisterMode = !isRegisterMode">
        {{ isRegisterMode ? '이미 계정이 있습니다' : '새 계정 만들기' }}
      </button>
    </form>
  </div>

  <!-- 인증 화면 밖에서도 사용할 수 있는 메인 채팅 레이아웃이다. -->
  <div class="app">
    <!-- 사이드바 -->
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">N</div>

        <div>
          <strong>Nova AI</strong>
          <span>Assistant</span>
        </div>
      </div>

      <button class="new-chat" @click="newChat">
        ＋ 새로운 대화
      </button>

      <button
        v-if="authSession"
        class="knowledge-button"
        :disabled="isKnowledgeLoading"
        @click="openKnowledgeFile"
      >
        ＋ 지식 문서 추가
      </button>
      <input
        ref="knowledgeFileInput"
        class="knowledge-file"
        type="file"
        accept=".txt,.md,text/plain,text/markdown"
        @change="handleKnowledgeFile"
      />
      <p v-if="knowledgeStatus" class="knowledge-status">{{ knowledgeStatus }}</p>

      <div class="history">
        <p class="history-title">최근 대화</p>

        <button
          v-for="conversation in conversations"
          :key="conversation.id"
          class="history-item"
          :class="{ active: conversation.id === activeConversationId }"
          @click="selectConversation(conversation.id)"
        >
          💬 {{ conversation.title }}
        </button>
      </div>

      <div class="profile">
        <div class="avatar">{{ authSession ? authSession.user.name.charAt(0) : '?' }}</div>

        <div v-if="authSession">
          <strong>{{ authSession.user.name }}</strong>
          <span>{{ authSession.user.email }}</span>
        </div>
        <div v-else>
          <strong>게스트</strong>
          <span>대화가 저장되지 않습니다</span>
        </div>
        <button v-if="authSession" class="logout-button" @click="logout">로그아웃</button>
        <button v-else class="logout-button" @click="openAuth">로그인</button>
      </div>
    </aside>

    <!-- 채팅 영역 -->
    <main class="chat-container">
      <header class="header">
        <div>
          <h2>AI Assistant</h2>
          <p>
            <span class="online"></span>
            Online
          </p>
        </div>

        <button class="header-button">•••</button>
      </header>

      <section ref="chatBox" class="messages">
        <div class="welcome">
          <div class="ai-symbol">✦</div>
          <h1>무엇을 도와드릴까요?</h1>
          <p>AI에게 궁금한 내용을 자유롭게 질문해보세요.</p>
        </div>

        <div
          v-for="message in messages"
          :key="message.id"
          class="message-row"
          :class="{ user: message.role === 'user' }"
        >
          <div
            v-if="message.role === 'assistant'"
            class="message-avatar"
          >
            ✦
          </div>

          <div>
            <div
              class="message"
              :class="message.role"
            >
              {{ message.content }}
            </div>

            <span class="time">
              {{ message.time }}
            </span>
            <span v-if="message.sources?.length" class="message-sources">
              참고: {{ message.sources.join(', ') }}
            </span>
          </div>
        </div>

        <!-- AI 응답 대기 -->
        <div
          v-if="isLoading"
          class="message-row"
        >
          <div class="message-avatar">✦</div>

          <div class="message assistant typing">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </section>

      <!-- 입력 -->
      <footer class="input-area">
        <div class="input-box">
          <textarea
            v-model="inputMessage"
            placeholder="Nova AI에게 메시지를 보내세요..."
            rows="1"
            @keydown.enter.exact.prevent="sendMessage"
          ></textarea>

          <button
            class="send-button"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
          >
            ➤
          </button>
        </div>

        <p class="notice">
          AI가 생성한 답변은 정확하지 않을 수 있습니다.
        </p>
      </footer>
    </main>
  </div>
</template>
