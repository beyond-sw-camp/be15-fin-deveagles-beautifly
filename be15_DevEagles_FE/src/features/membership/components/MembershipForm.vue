<template>
  <div class="membership-page-wrapper">
    <!-- 헤더 -->
    <div class="header-row">
      <h2 class="page-title">회원권 관리 페이지</h2>
      <button class="register-button" @click="modalVisible = true">회원권 관리</button>
    </div>

    <!-- 상단 탭 -->
    <div class="tab-filter-row">
      <div class="tab-group">
        <div class="tab" :class="{ active: activeTab === '전체' }" @click="activeTab = '전체'">
          전체
        </div>
        <div
          class="tab"
          :class="{ active: activeTab === '만료예정' }"
          @click="activeTab = '만료예정'"
        >
          만료(예정)
        </div>
      </div>
    </div>

    <!-- 하위 탭 -->
    <div v-if="activeTab === '만료예정'" class="sub-tab-row">
      <div class="tab-group">
        <div class="tab" :class="{ active: expiryType === '선불' }" @click="expiryType = '선불'">
          선불권
        </div>
        <div
          class="tab"
          :class="{ active: expiryType === '횟수권' }"
          @click="expiryType = '횟수권'"
        >
          횟수권
        </div>
      </div>
    </div>

    <!-- 검색/필터 -->
    <div class="search-bar">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="고객명 검색"
        @keydown.enter="applySearch"
      />
      <button class="filter-button" @click="toggleFilterModal">필터</button>
    </div>

    <!-- 필터 뱃지 -->
    <div v-if="filterState" class="filter-badges">
      <span v-if="filterState.min || filterState.max" class="badge">
        잔여: {{ filterState.min ?? 0 }} ~ {{ filterState.max ?? '무제한' }}
        <button @click="filterState = null">×</button>
      </span>
      <span v-if="filterState.startDate && filterState.endDate" class="badge">
        만료(예정)일: {{ filterState.startDate }} ~ {{ filterState.endDate }}
        <button @click="filterState = null">×</button>
      </span>
    </div>

    <!-- 테이블 -->
    <MembershipTableForm
      v-if="activeTab === '전체'"
      :search-keyword="searchKeyword"
      :filter-state="filterState"
    />
    <MembershipExpiryTableForm
      v-else
      :type="expiryType"
      :search-keyword="searchKeyword"
      :filter-state="filterState"
    />

    <!-- 모달 -->
    <MembershipFilterModal
      v-if="activeTab === '전체' && showFilterModal"
      v-model="showFilterModal"
      @apply="handleFilterApply"
    />
    <MembershipExpiryFilterModal
      v-if="activeTab === '만료예정' && showFilterModal"
      v-model="showFilterModal"
      :type="expiryType"
      @apply="handleExpiryFilterApply"
    />

    <!-- 회원권 관리 패널 -->
    <div v-if="modalVisible" class="overlay" @click.self="modalVisible = false">
      <div class="modal-panel">
        <MembershipDetailModal @close="modalVisible = false" />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import MembershipFilterModal from '@/features/membership/components/MembershipFilterModal.vue';
  import MembershipExpiryFilterModal from '@/features/membership/components/MembershipExpiryFilterModal.vue';
  import MembershipTableForm from '@/features/membership/components/MembershipTableForm.vue';
  import MembershipExpiryTableForm from '@/features/membership/components/MembershipExpiryTableForm.vue';
  import MembershipDetailModal from '@/features/membership/components/MembershipDetailModal.vue';

  const activeTab = ref('전체');
  const expiryType = ref('선불');
  const showFilterModal = ref(false);
  const modalVisible = ref(false);
  const searchKeyword = ref('');
  const filterState = ref(null);

  const toggleFilterModal = () => {
    showFilterModal.value = !showFilterModal.value;
  };

  const handleFilterApply = filters => {
    filterState.value = filters;
    showFilterModal.value = false;
  };

  const handleExpiryFilterApply = filters => {
    filterState.value = { ...filters, type: expiryType.value };
    showFilterModal.value = false;
  };

  const applySearch = () => {};
</script>

<style scoped>
  .membership-page-wrapper {
    padding: 2rem;
  }
  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }
  .page-title {
    font-size: 20px;
    font-weight: bold;
  }
  .register-button {
    padding: 0.5rem 1rem;
    font-size: 14px;
    border: 1px solid #ccc;
    background-color: white;
    border-radius: 4px;
    cursor: pointer;
  }
  .tab-filter-row {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-bottom: 0.75rem;
  }
  .sub-tab-row {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 1rem;
  }
  .tab-group {
    display: flex;
    border: 1px solid #000;
    border-radius: 6px;
    overflow: hidden;
  }
  .tab {
    padding: 0.5rem 1.25rem;
    font-size: 14px;
    cursor: pointer;
    background-color: white;
    color: black;
  }
  .tab.active {
    background-color: black;
    color: white;
  }
  .search-bar {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 1rem;
  }
  .search-bar input {
    width: 300px;
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  .filter-button {
    padding: 0.5rem 1rem;
    font-size: 14px;
    border: 1px solid #ccc;
    background-color: white;
    border-radius: 4px;
    cursor: pointer;
  }
  .filter-badges {
    display: flex;
    gap: 8px;
    margin-bottom: 1rem;
  }
  .badge {
    background-color: #a58cff;
    color: white;
    padding: 4px 10px;
    border-radius: 20px;
    font-size: 13px;
    display: flex;
    align-items: center;
  }
  .badge button {
    background: none;
    border: none;
    color: white;
    font-size: 14px;
    margin-left: 6px;
    cursor: pointer;
  }
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }
  .modal-panel {
    position: fixed;
    top: 0;
    left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    background: white;
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow-y: auto;
  }
</style>
