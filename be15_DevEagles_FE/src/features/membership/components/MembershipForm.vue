<template>
  <div class="membership-page-wrapper">
    <!-- 헤더 -->
    <div class="header-row">
      <h1 class="page-title">회원권 관리 페이지</h1>
      <BaseButton class="register-button" @click="modalVisible = true">회원권 관리</BaseButton>
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
      <BaseForm
        v-model="searchKeyword"
        type="text"
        placeholder="고객명 검색"
        style="width: 300px"
        @keydown.enter="applySearch"
      />
      <BaseButton
        class="filter-button"
        style="transform: translateY(-8px)"
        @click="toggleFilterModal"
        >필터</BaseButton
      >
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

  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

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

  .tab-filter-row,
  .sub-tab-row {
    display: flex;
    /* ✅ 가운데 정렬 */
    margin-bottom: 1rem;
    font-weight: bold;
  }

  .tab-group {
    display: flex;
    width: 220px; /* ✅ 상하 탭 동일한 너비 */
    border: 1px solid #364f6b;
    border-radius: 6px;
    overflow: hidden;
  }

  .tab {
    flex: 1; /* ✅ 탭 버튼 너비 동일하게 */
    text-align: center;
    padding: 0.5rem 0;
    font-size: 14px;
    cursor: pointer;
    background-color: white;
    color: #364f6b;
    font-weight: bold;
    border-right: 1px solid #364f6b;
  }

  .tab:last-child {
    border-right: none;
  }

  .tab.active {
    background-color: #364f6b;
    color: white;
    font-weight: bold;
  }

  .search-bar {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 1rem;
  }
  .search-bar {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 1rem;
  }

  .filter-badges {
    display: flex;
    gap: 8px;
    margin-bottom: 1rem;
    flex-wrap: wrap;
  }

  .badge {
    background-color: #ff6b91;
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
