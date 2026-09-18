<script setup lang="ts">
import { ref, nextTick } from 'vue'
import './HelloWorld.css'

const inputMessage = ref('')
const chatBox = ref(null)
const isLoading = ref(false)

const messages = ref([
  {
    id: 1,
    role: 'assistant',
    content: '안녕하세요! 👋 저는 Nova AI입니다. 무엇이든 물어보세요.',
    time: getTime()
  }
])

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

  inputMessage.value = ''
  await scrollToBottom()

  isLoading.value = true

  setTimeout(async () => {
    messages.value.push({
      id: Date.now(),
      role: 'assistant',
      content: `"${text}"에 대해 질문하셨네요. 현재는 Vue 학습용 AI 챗봇입니다.`,
      time: getTime()
    })

    isLoading.value = false
    await scrollToBottom()
  }, 700)
}

async function scrollToBottom() {
  await nextTick()

  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

function newChat() {
  messages.value = [
    {
      id: Date.now(),
      role: 'assistant',
      content: '새로운 대화를 시작했습니다. 무엇을 도와드릴까요?',
      time: getTime()
    }
  ]
}
</script>

<template>
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

        <button class="history-item active">
          💬 Vue 프로젝트 질문
        </button>

        <button class="history-item">
          💬 Spring Boot API
        </button>

        <button class="history-item">
          💬 데이터베이스 설계
        </button>
      </div>

      <div class="profile">
        <div class="avatar">M</div>

        <div>
          <strong>Developer</strong>
          <span>Vue + TypeScript</span>
        </div>
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