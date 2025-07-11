<script setup>
  import { ref } from 'vue';

  const emit = defineEmits(['send']);
  const text = ref('');

  function handleSubmit() {
    if (!text.value.trim()) return;
    emit('send', text.value.trim());
    text.value = '';
  }
</script>

<template>
  <form class="chat-input" @submit.prevent="handleSubmit">
    <input
      v-model="text"
      type="text"
      placeholder="메시지를 입력하세요..."
      class="chat-text-input"
      autofocus
      autocomplete="off"
    />
    <button type="submit" class="chat-send-btn">전송</button>
  </form>
</template>

<style scoped>
  .chat-input {
    display: flex;
    gap: 0.5rem;
  }
  .chat-text-input {
    flex: 1;
    padding: 0.5rem 0.75rem;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 8px;
    outline: none;
  }
  .chat-send-btn {
    background-color: var(--color-primary-main);
    color: white;
    padding: 0.5rem 1rem;
    border: none;
    font-size: 14px;
    border-radius: 8px;
    cursor: pointer;
  }
</style>
