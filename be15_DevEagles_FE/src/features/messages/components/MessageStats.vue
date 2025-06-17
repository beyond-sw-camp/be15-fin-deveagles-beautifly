<script setup>
  import { computed } from 'vue';

  const props = defineProps({
    messages: {
      type: Array,
      required: true,
    },
  });

  const sentCount = computed(() => props.messages.filter(m => m.status === 'sent').length);
  const reservedCount = computed(() => props.messages.filter(m => m.status === 'reserved').length);
</script>

<template>
  <div class="message-stats-bar">
    <div class="message-stats-card">
      <div class="stats-item">
        <span class="stats-label">발송 완료</span>
        <span class="stats-value">{{ sentCount }}건</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">예약 문자</span>
        <span class="stats-value">{{ reservedCount }}건</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">전체</span>
        <span class="stats-value">{{ props.messages.length }}건</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .message-stats-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    gap: 24px;
  }
  .message-stats-card {
    display: flex;
    gap: 24px;
    background-color: var(--color-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 8px;
    padding: 12px 20px;
    box-shadow: var(--shadow-xs);
  }
  .stats-item {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
  .stats-label {
    font-size: 12px;
    color: var(--color-gray-500);
  }
  .stats-value {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-gray-900);
    margin-top: 2px;
  }
  .message-filter-group {
    display: flex;
    align-items: center;
    gap: 8px;
    white-space: nowrap;
  }
  .filter-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-gray-700);
  }
  .filter-select {
    padding: 4px 8px;
    font-size: 14px;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    background-color: var(--color-white);
    min-width: 120px;
  }
</style>
