<script setup>
  import { ref } from 'vue';

  const emit = defineEmits(['send']);
  const input = ref('');

  function sendMessage() {
    const trimmed = input.value.trim();
    if (!trimmed) return;
    emit('send', trimmed);
    input.value = '';
  }
</script>

<template>
  <div class="chat-input-wrap">
    <input
      v-model="input"
      type="text"
      class="chat-input"
      placeholder="메시지를 입력하세요..."
      @keydown.enter="sendMessage"
    />
    <button class="send-button" @click="sendMessage">전송</button>
  </div>
</template>

<style scoped>
  .chat-input-wrap {
    display: flex;
    gap: 0.5rem;
  }
  .chat-input {
    flex: 1;
    padding: 0.6rem 1rem;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
  }
  .send-button {
    background-color: var(--color-primary-main);
    color: white;
    border: none;
    border-radius: 8px;
    padding: 0.6rem 1rem;
    cursor: pointer;
  }
</style>
