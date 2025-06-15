<template>
  <div class="history-wrapper">
    <div class="page-header">
      <h1 class="font-screen-title">예약 변경 이력</h1>
    </div>

    <BaseTable :columns="columns" :data="historyData" row-key="id" :striped="true" :hover="true" />
  </div>
</template>

<script setup>
  import BaseTable from '@/components/common/BaseTable.vue';

  const columns = [
    { key: 'name', title: '고객 이름', width: '120px' },
    { key: 'service', title: '시술', width: '100px' },
    { key: 'employee', title: '담당자', width: '100px' },
    { key: 'date', title: '예약 날짜', width: '160px' },
    { key: 'change', title: '변경 내용', width: '100px' },
    { key: 'changedAt', title: '변경 일시', width: '160px' },
  ];

  const historyData = [
    {
      id: 1,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 2,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 3,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 4,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 5,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 6,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 7,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 8,
      name: '김미글',
      service: '염펌',
      employee: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
  ];
</script>

<style scoped>
  .page-header {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .history-wrapper {
    padding: 24px;
    max-width: 1000px;
    margin: 0 auto;
  }

  .title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 16px;
  }
</style>
