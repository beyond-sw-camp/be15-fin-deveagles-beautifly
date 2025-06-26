<script setup>
  import { ref } from 'vue';
  import ChatMessages from './ChatMessages.vue';
  import ChatInput from './ChatInput.vue';
  import ChatListView from './ChatListView.vue';
  import ListIcon from '@/components/icons/ListIcon.vue';
  import HomeIcon from '@/components/icons/HomeIcon.vue';

  const emit = defineEmits(['close']);

  const currentView = ref('home'); // 'home', 'chat', 'list'
  const messages = ref([]);

  function openNewChat() {
    messages.value = [{ from: 'person', text: '안녕하세요! 무엇을 도와드릴까요?' }];
    currentView.value = 'chat';
  }

  function openList() {
    currentView.value = 'list';
  }

  function goHome() {
    currentView.value = 'home';
  }

  function handleSend(text) {
    messages.value.push({ from: 'user', text });
    setTimeout(() => {
      messages.value.push({ from: 'bot', text: '확인했습니다!' });
    }, 600);
  }
</script>

<template>
  <div class="chat-widget-panel">
    <!-- Header -->
    <div class="chat-modal-header">
      <img src="@/images/logo_positive.png" class="chat-modal-logo" alt="Beautifly 로고" />
      <div class="chat-modal-header-text">
        <p class="chat-modal-title">Beautifly 상담센터</p>
        <p class="chat-modal-subtitle">운영시간 평일 10:00 ~ 18:00</p>
      </div>
      <button class="chat-modal-close" @click="$emit('close')">✖</button>
    </div>

    <!-- Body View -->
    <div class="chat-modal-body">
      <div v-if="currentView === 'home'">
        <p class="chat-greeting">안녕하세요 😊 Beautifly 상담센터입니다.</p>
        <div class="home-action">
          <button class="home-new-button" @click="openNewChat">새 문의하기</button>
        </div>
      </div>
      <ChatMessages v-else-if="currentView === 'chat'" :messages="messages" />
      <ChatListView v-else-if="currentView === 'list'" />
    </div>

    <!-- Input & Bottom Actions -->
    <div class="chat-modal-footer">
      <ChatInput v-if="currentView === 'chat'" @send="handleSend" />
      <div class="chat-footer-buttons">
        <div class="chat-footer-half">
          <button class="chat-btn" title="대화 목록" @click="openList">
            <ListIcon :size="24" />
          </button>
        </div>
        <div class="chat-footer-half">
          <button class="chat-btn" title="홈으로" @click="goHome">
            <HomeIcon :size="24" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .chat-widget-panel {
    width: 360px;
    height: 520px;
    background-color: #fff;
    border-radius: 1rem;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: fixed;
    bottom: 60px;
    right: 24px;
    z-index: 9999;
  }
  .chat-modal-header {
    background-color: var(--color-primary-main);
    color: white;
    padding: 1rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    border-top-left-radius: 1rem;
    border-top-right-radius: 1rem;
  }
  .chat-modal-logo {
    width: 20px;
    height: 20px;
  }
  .chat-modal-header-text {
    display: flex;
    flex-direction: column;
  }
  .chat-modal-title {
    font-size: 1rem;
    font-weight: bold;
  }
  .chat-modal-subtitle {
    font-size: 0.75rem;
    opacity: 0.85;
  }
  .chat-modal-close {
    margin-left: auto;
    font-size: 1.2rem;
    background: none;
    border: none;
    color: white;
    cursor: pointer;
  }
  .chat-modal-body {
    flex: 1;
    overflow-y: auto;
    background-color: #f7f9fc;
    padding: 1rem;
  }
  .chat-modal-footer {
    border-top: 1px solid #e0e0e0;
    padding: 0.75rem 1rem;
    background-color: #fff;
  }
  .chat-footer-buttons {
    display: flex;
    justify-content: space-between;
    gap: 1rem;
    margin-top: 0.5rem;
  }
  .chat-footer-half {
    flex: 1;
    display: flex;
    justify-content: center;
  }
  .chat-btn {
    background: none;
    border: none;
    cursor: pointer;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .chat-btn :deep(svg) {
    color: var(--color-gray-600);
    transition: color 0.2s ease;
  }
  .chat-btn:hover :deep(svg) {
    color: var(--color-primary-main);
  }
  .chat-greeting {
    font-size: 15px;
    padding: 2rem 0 1rem;
    text-align: center;
    color: var(--color-gray-700);
  }
  .home-action {
    text-align: center;
    margin-top: 1rem;
  }
  .home-new-button {
    background-color: var(--color-primary-main);
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 8px;
    font-weight: bold;
    cursor: pointer;
    font-size: 14px;
    border: none;
  }
</style>
