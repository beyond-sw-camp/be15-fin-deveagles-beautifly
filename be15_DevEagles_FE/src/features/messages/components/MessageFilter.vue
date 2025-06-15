<script setup>
  import { reactive, watch } from 'vue';

  const emit = defineEmits(['update:filter']);

  const local = reactive({
    status: 'all',
  });

  // 상태 바뀔 때마다 자동 emit
  watch(
    () => local.status,
    newStatus => {
      emit('update:filter', { status: newStatus });
    }
  );
</script>

<template>
  <div class="filter-wrapper">
    <div class="filter-row">
      <select v-model="local.status" class="input">
        <option value="all">전체</option>
        <option value="sent">발송 완료</option>
        <option value="reserved">예약 문자</option>
      </select>
    </div>
  </div>
</template>

<style scoped>
  .filter-wrapper {
    width: 100%;
    display: flex;
    justify-content: flex-start;
    margin-bottom: 1rem;
  }

  .filter-row {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }
</style>
