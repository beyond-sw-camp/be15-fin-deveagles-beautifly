<script setup>
  const props = defineProps({
    message: {
      type: Object,
      required: true,
    },
  });
</script>

<template>
  <div class="message-list-row">
    <div class="message-list-col message-title">
      {{ message.title }}
    </div>
    <div class="message-list-col message-content">
      {{ message.content }}
    </div>
    <div class="message-list-col message-status">
      {{ message.status === 'sent' ? '발송 완료' : '예약 문자' }}
    </div>
    <div class="message-list-col message-date">
      {{ message.date }}
    </div>
    <div class="message-list-col message-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<style scoped>
  .message-list-row {
    display: flex;
    padding: 12px 16px;
    border-bottom: 1px solid var(--color-gray-100);
    font-size: 14px;
    color: var(--color-gray-900);
  }
  .message-list-col {
    flex: 1;
    padding: 4px 8px;
    display: flex;
    align-items: center;
    justify-content: center; /* 이 줄 추가하면 전체 가운데 정렬됨 */
  }
  .message-title {
    flex: 1.5;
  }
  .message-content {
    flex: 3;
  }
  .message-status,
  .message-date,
  .message-actions {
    flex: 1;
    justify-content: center;
  }
</style>
