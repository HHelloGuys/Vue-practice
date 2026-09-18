<script setup lang="ts">
import { computed, ref, nextTick } from 'vue'
import { requestChatAnswer } from '../api/chat'
import {
  clearAuthSession,
  getAuthSession,
  login,
  register,
  type AuthSession
} from '../api/auth'
import './HelloWorld.css'

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

interface ChatMessage {
  id: number
  role: 'user' | 'assistant'
  content: string
  time: string
}

interface Conversation {
  id: number
  title: string
  messages: ChatMessage[]
}

function createInitialMessage(content: string): ChatMessage {
  return {
    id: Date.now(),
    role: 'assistant',
    content,
    time: getTime()
  }
}

function createConversation(): Conversation {
  return {
    id: Date.now(),
    title: '새로운 대화',
    messages: [
      createInitialMessage('새로운 대화를 시작했습니다. 무엇을 도와드릴까요?')
    ]
  }
}

const firstConversation = createConversation()
const conversations = ref<Conversation[]>([firstConversation])
const activeConversationId = ref(firstConversation.id)

const activeConversation = computed(() => {
  return conversations.value.find(
    (conversation) => conversation.id === activeConversationId.value
  ) ?? conversations.value[0]
})

const messages = computed(() => activeConversation.value.messages)

function createConversationTitle(message: string) {
  // 첫 질문이 길면 최근 대화 영역에 맞게 줄여서 표시한다.
  return message.length > 20 ? `${message.slice(0, 20)}…` : message
}

function getTime() {
  return new Date().toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

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
    const response = await requestChatAnswer(text)

    messages.value.push({
      id: Date.now(),
      role: 'assistant',
      content: response.answer,
      time: getTime()
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

async function scrollToBottom() {
  await nextTick()

  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

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

function selectConversation(conversationId: number) {
  // 응답을 기다리는 동안 대화를 바꿔 답변 위치가 섞이지 않게 한다.
  if (isLoading.value) return

  activeConversationId.value = conversationId
  void scrollToBottom()
}

async function submitAuth() {
  authError.value = ''
  isAuthLoading.value = true
  try {
    if (isRegisterMode.value) {
      await register(authEmail.value, authPassword.value, authName.value)
    }
    authSession.value = await login(authEmail.value, authPassword.value)
    authPassword.value = ''
    showAuth.value = false
  } catch (error) {
    authError.value = error instanceof Error ? error.message : '인증에 실패했습니다.'
  } finally {
    isAuthLoading.value = false
  }
}

function logout() {
  clearAuthSession()
  authSession.value = null
}

function openAuth() {
  authError.value = ''
  showAuth.value = true
}
</script>

<template>
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
