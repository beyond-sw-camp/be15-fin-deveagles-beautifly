<template>
  <div class="history-wrapper">
    <div class="page-header">
      <h1 class="font-screen-title">예약 변경 이력</h1>
    </div>
    <div class="base-table-wrapper">
      <BaseTable :columns="columns" :data="pagedData" row-key="id" :striped="true" :hover="true" />
    </div>

    <Pagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="historyData.length"
      :items-per-page="itemsPerPage"
      @page-change="handlePageChange"
      @items-per-page-change="handleItemsPerPageChange"
    />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import Pagination from '@/components/common/Pagination.vue';

  const columns = [
    { key: 'name', title: '고객 이름', width: '120px' },
    { key: 'service', title: '시술', width: '100px' },
    { key: 'staff', title: '담당자', width: '100px' },
    { key: 'date', title: '예약 날짜', width: '160px' },
    { key: 'change', title: '변경 내용', width: '100px' },
    { key: 'changedAt', title: '변경 일시', width: '160px' },
  ];

  const historyData = [
    {
      id: 1,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 2,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 3,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 4,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 5,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 6,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 7,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 8,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 9,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 10,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 11,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 12,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 13,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 14,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '수정',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 15,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
    {
      id: 16,
      name: '김미글',
      service: '염펌',
      staff: '박미글',
      date: '2025.06.08 14시',
      change: '삭제',
      changedAt: '2025.06.08 11:08',
    },
  ];

  const currentPage = ref(1);
  const itemsPerPage = ref(10);

  const totalPages = computed(() => Math.ceil(historyData.length / itemsPerPage.value));

  const pagedData = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return historyData.slice(start, start + itemsPerPage.value);
  });

  function handlePageChange(page) {
    currentPage.value = page;
  }

  function handleItemsPerPageChange(count) {
    itemsPerPage.value = count;
    currentPage.value = 1;
  }
</script>

<style scoped>
  .base-table-wrapper {
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 24px;
    box-sizing: border-box;
  }

  .history-wrapper {
    padding: 24px;
  }

  .page-header {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
</style>
