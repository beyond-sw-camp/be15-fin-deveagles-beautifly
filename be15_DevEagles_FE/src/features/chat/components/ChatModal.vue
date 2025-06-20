<script setup>
  import { ref, defineEmits } from 'vue';
  import ChatMessages from './ChatMessages.vue';
  import ChatInput from './ChatInput.vue';

  const emit = defineEmits(['close']);

  const messages = ref([{ from: 'person', text: '안녕하세요! Beautifly 상담 도우미입니다 😊' }]);

  function handleSend(text) {
    messages.value.push({ from: 'user', text });
    setTimeout(() => {
      messages.value.push({ from: 'bot', text: '빠르게 답변드리겠습니다!' });
    }, 600);
  }

  function close() {
    emit('close');
  }
</script>

<template>
  <div class="chat-widget-panel">
    <!-- Header -->
    <div class="chat-modal-header">
      <img src="@/images/logo_positive.png" class="chat-modal-logo" />
      <div class="chat-modal-header-text">
        <p class="chat-modal-title">Beautifly 상담센터</p>
        <p class="chat-modal-subtitle">운영시간 평일 10:00 ~ 18:00</p>
      </div>
      <button class="chat-modal-close" @click="close">✖</button>
    </div>

    <!-- Messages -->
    <div class="chat-modal-body">
      <ChatMessages :messages="messages" />
    </div>

    <!-- Input -->
    <div class="chat-modal-footer">
      <ChatInput @send="handleSend" />
    </div>
  </div>
</template>

<!-- ✅ scoped 스타일은 컴포넌트 내부 전용 UI만 -->
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
  }

  .chat-modal-header {
    background-color: var(--color-primary-main);
    color: white;
    padding: 1rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
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
</style>

<!-- ✅ 반응형은 global style로 따로 분리 -->
<style>
  @media (max-width: 768px) {
    .chat-widget-panel {
      width: calc(100vw - 24px);
      height: 80vh;
    }
  }
</style>
