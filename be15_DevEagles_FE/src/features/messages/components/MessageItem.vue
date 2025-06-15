<script setup>
  import { computed } from 'vue';

  const props = defineProps({
    message: {
      type: Object,
      required: true,
    },
  });

  const statusLabel = computed(() => {
    switch (props.message.status) {
      case 'sent':
        return '발송 완료';
      case 'reserved':
        return '예약 문자';
      default:
        return '상태 미정';
    }
  });
</script>

<template>
  <li class="message-item">
    <div class="item-header">
      <div class="sender">{{ message.sender }}</div>
      <div class="timestamp">{{ message.createdAt }}</div>
    </div>
    <div class="item-body">
      <p class="content">{{ message.content }}</p>
    </div>
    <div class="item-footer">
      <span class="badge" :class="message.status === 'sent' ? 'badge-primary' : 'badge-secondary'">
        {{ statusLabel }}
      </span>
    </div>
  </li>
</template>

<style scoped>
  .message-item {
    border: 1px solid var(--color-gray-200);
    border-radius: 0.75rem;
    padding: 1rem;
    background-color: var(--color-neutral-white);
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    font-size: 0.875rem;
    color: var(--color-gray-600);
  }

  .item-body .content {
    font-size: 1rem;
    color: var(--color-gray-900);
    word-break: break-word;
  }

  .item-footer {
    display: flex;
    justify-content: flex-end;
  }

  .badge {
    font-size: 0.75rem;
    padding: 0.25rem 0.625rem;
    border-radius: 9999px;
    font-weight: 500;
    line-height: 1;
  }

  .badge-primary {
    background-color: var(--color-primary-main);
    color: var(--color-neutral-white);
  }

  .badge-secondary {
    background-color: var(--color-gray-400);
    color: var(--color-neutral-white);
  }
</style>
