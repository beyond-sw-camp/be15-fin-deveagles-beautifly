<script setup>
  const props = defineProps({
    messages: {
      type: Array,
      required: true,
    },
  });
</script>

<template>
  <div class="chat-messages">
    <div
      v-for="(msg, index) in props.messages"
      :key="index"
      :class="['chat-bubble', msg.from === 'user' ? 'user' : 'bot']"
    >
      {{ msg.text }}
    </div>
  </div>
</template>

<style scoped>
  .chat-messages {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  .chat-bubble {
    max-width: 75%;
    padding: 0.5rem 0.75rem;
    border-radius: 1rem;
    word-break: break-word;
  }
  .user {
    align-self: flex-end;
    background-color: var(--color-primary-main);
    color: white;
  }
  .bot {
    align-self: flex-start;
    background-color: #e5e7eb;
    color: black;
  }
</style>
