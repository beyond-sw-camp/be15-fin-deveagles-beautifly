<script setup>
  import { ref } from 'vue';

  const emit = defineEmits(['send']);
  const input = ref('');

  function handleSend() {
    if (input.value.trim()) {
      emit('send', input.value.trim());
      input.value = '';
    }
  }
</script>

<template>
  <div class="chat-input-wrapper">
    <input
      v-model="input"
      class="chat-input"
      type="text"
      placeholder="메시지를 입력하세요"
      @keydown.enter="handleSend"
    />
    <button class="chat-send-btn" @click="handleSend">전송</button>
  </div>
</template>

<style scoped>
  .chat-input-wrapper {
    display: flex;
    gap: 0.5rem;
  }
  .chat-input {
    flex: 1;
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
  }
  .chat-send-btn {
    padding: 0.5rem 1rem;
    background-color: var(--color-primary-main);
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }
</style>
