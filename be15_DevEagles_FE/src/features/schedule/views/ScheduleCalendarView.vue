<script setup>
  import { ref } from 'vue';
  import ScheduleCalendar from '@/features/schedule/components/ScheduleCalendar.vue';

  const searchText = ref('');
  const selectedService = ref('');
  const selectedEmployee = ref('');
  const selectedType = ref('');
</script>

<template>
  <div class="container">
    <!-- 🔍 필터 영역 -->
    <div class="filter-bar">
      <input
        v-model="searchText"
        type="text"
        placeholder="고객명 또는 연락처 검색"
        class="input input-search"
      />

      <select v-model="selectedService" class="input input-select">
        <option value="">시술 종류</option>
        <option value="cut">컷트</option>
        <option value="color">염색</option>
        <option value="perm">펌</option>
      </select>

      <select v-model="selectedEmployee" class="input input-select">
        <option value="">담당자</option>
        <option value="minsu">최민수</option>
        <option value="chaeun">이채은</option>
      </select>

      <select v-model="selectedType" class="input input-select">
        <option value="">스케줄</option>
        <option value="reservation">예약</option>
        <option value="holiday">휴무</option>
        <option value="event">일정</option>
      </select>

      <button class="btn btn-primary schedule-btn">스케줄 등록</button>
    </div>

    <!-- 📅 캘린더 -->
    <ScheduleCalendar />
  </div>
</template>

<style scoped>
  .container {
    padding: 24px;
  }
  .filter-bar {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
  }
  .input-search {
    width: 240px;
  }
  .input-select {
    width: 160px;
  }
  .schedule-btn {
    margin-left: auto;
  }
</style>
