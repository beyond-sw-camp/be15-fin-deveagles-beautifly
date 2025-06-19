<script setup>
  import { nextTick, onUpdated, watch, ref } from 'vue';

  const props = defineProps({
    messages: {
      type: Array,
      required: true,
    },
  });

  const containerRef = ref(null);

  function scrollToBottom() {
    if (!containerRef.value) return;
    containerRef.value.scrollTop = containerRef.value.scrollHeight;
  }

  watch(
    () => props.messages.length,
    async () => {
      await nextTick();
      scrollToBottom();
    }
  );

  onUpdated(scrollToBottom);

  function senderName(from) {
    switch (from) {
      case 'user':
        return '나';
      case 'bot':
        return 'Beautifly 도우미';
      case 'person':
        return '상담 직원';
      default:
        return '알 수 없음';
    }
  }
</script>

<template>
  <div ref="containerRef" class="chat-messages">
    <div
      v-for="(msg, index) in props.messages"
      :key="index"
      :class="['chat-msg-wrapper', msg.from]"
    >
      <div class="chat-sender">
        {{ senderName(msg.from) }}
      </div>

      <div :class="['chat-msg', msg.from]">
        {{ msg.text }}
      </div>
    </div>
  </div>
</template>

<style scoped>
  .chat-messages {
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
    overflow-y: auto;
    height: 100%;
    padding-right: 4px;
  }

  .chat-msg-wrapper {
    display: flex;
    flex-direction: column;
    max-width: 80%;
  }

  .chat-msg-wrapper.bot,
  .chat-msg-wrapper.person {
    align-self: flex-start;
  }

  .chat-msg-wrapper.user {
    align-self: flex-end;
    align-items: flex-end;
  }

  .chat-sender {
    font-size: 11px;
    color: var(--color-gray-500);
    margin-bottom: 2px;
    line-height: 1;
  }

  .chat-msg {
    padding: 0.6rem 1rem;
    border-radius: 12px;
    word-break: break-word;
    white-space: pre-wrap;
  }

  .chat-msg.bot {
    background-color: #eef4ff;
  }

  .chat-msg.person {
    background-color: #fff3cd;
  }

  .chat-msg.user {
    background-color: #d0f5e8;
  }
</style>
