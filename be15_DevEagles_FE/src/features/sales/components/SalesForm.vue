<template>
  <div class="sales-page-wrapper">
    <!-- 헤더 -->
    <div class="header-row">
      <h1 class="page-title">매출 등록/목록</h1>
      <div class="button-group">
        <BaseButton type="primary" @click="showMembershipModal = true">회원권 판매</BaseButton>
        <BaseButton type="primary" @click="showItemModal = true">상품 판매</BaseButton>
      </div>
    </div>

    <!-- 검색 & 필터 -->
    <div class="search-bar">
      <BaseForm
        v-model="searchKeyword"
        type="text"
        placeholder="고객명/매출메모 검색"
        style="width: 300px"
        @keydown.enter="applySearch"
      />
      <BaseButton type="primary" style="transform: translateY(-8px)" @click="toggleFilterModal"
        >필터</BaseButton
      >
    </div>

    <!-- 필터 뱃지 -->
    <div v-if="filterState" class="filter-badges">
      <span v-if="filterState.startDate && filterState.endDate" class="badge">
        날짜: {{ formatDate(filterState.startDate) }} ~ {{ formatDate(filterState.endDate) }}
        <button @click="clearField('date')">×</button>
      </span>
      <span v-if="filterState.types?.length" class="badge">
        매출유형: {{ filterState.types.join(', ') }}
        <button @click="clearField('types')">×</button>
      </span>
      <span v-if="filterState.products?.length" class="badge">
        상품유형: {{ filterState.products.join(', ') }}
        <button @click="clearField('products')">×</button>
      </span>
      <span v-if="filterState.staff" class="badge">
        담당자: {{ filterState.staff }}
        <button @click="clearField('staff')">×</button>
      </span>
    </div>

    <!-- 테이블 -->
    <SalesTableForm
      :search-keyword="searchKeyword"
      :filter-state="filterState"
      :current-page="currentPage"
      :items-per-page="itemsPerPage"
      @update:total-items="totalItems = $event"
    />

    <!-- 페이지네이션 -->
    <Pagination
      :current-page="currentPage"
      :total-pages="displayTotalPages"
      :total-items="totalItems"
      :items-per-page="itemsPerPage"
      @page-change="handlePageChange"
    />

    <!-- 필터 모달 -->
    <SalesFilterModal v-if="showFilterModal" v-model="showFilterModal" @apply="handleFilterApply" />

    <!-- 회원권 판매 모달 -->
    <div v-if="showMembershipModal" class="overlay" @click.self="showMembershipModal = false">
      <div class="modal-panel">
        <MembershipSalesRegistModal
          @close="showMembershipModal = false"
          @submit="handleMembershipSubmit"
        />
      </div>
    </div>

    <!-- 상품 판매 모달 -->
    <div v-if="showItemModal" class="overlay" @click.self="showItemModal = false">
      <div class="modal-panel">
        <ItemsSalesRegistModal @close="showItemModal = false" @submit="handleItemSubmit" />
      </div>
    </div>

    <!-- ✅ Toast 추가 -->
    <BaseToast ref="toastRef" />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import SalesFilterModal from '@/features/sales/components/SalesFilterModal.vue';
  import SalesTableForm from '@/features/sales/components/SalesTableForm.vue';
  import MembershipSalesRegistModal from '@/features/sales/components/MembershipSalesRegistModal.vue';
  import ItemsSalesRegistModal from '@/features/sales/components/ItemsSalesRegistModal.vue';

  const searchKeyword = ref('');
  const filterState = ref(null);
  const showFilterModal = ref(false);
  const showMembershipModal = ref(false);
  const showItemModal = ref(false);

  const toastRef = ref(null); // ✅ 토스트 참조

  // 페이지네이션 관련 상태
  const currentPage = ref(1);
  const itemsPerPage = 10;
  const totalItems = ref(0);

  const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage));

  // ✅ 항상 2 이상을 넘기기 위해 계산된 페이지 수
  const displayTotalPages = computed(() => Math.max(totalPages.value, 2));

  const handlePageChange = page => {
    currentPage.value = page;
  };

  const toggleFilterModal = () => {
    showFilterModal.value = !showFilterModal.value;
  };

  const handleFilterApply = filters => {
    filterState.value = filters;
    currentPage.value = 1;
    showFilterModal.value = false;
  };

  const applySearch = () => {
    currentPage.value = 1;
  };

  const clearField = key => {
    if (!filterState.value) return;
    const newFilter = { ...filterState.value };

    switch (key) {
      case 'date':
        newFilter.startDate = null;
        newFilter.endDate = null;
        break;
      case 'types':
        newFilter.types = [];
        break;
      case 'products':
        newFilter.products = [];
        break;
      case 'staff':
        newFilter.staff = null;
        break;
    }

    filterState.value = newFilter;
  };

  const formatDate = date => {
    return new Date(date).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  // ✅ 회원권/상품 등록 성공 시 토스트 처리
  const handleMembershipSubmit = () => {
    showMembershipModal.value = false;
    toastRef.value?.success('회원권 매출이 등록되었습니다.');
  };
  const handleItemSubmit = () => {
    showItemModal.value = false;
    toastRef.value?.success('상품 매출이 등록되었습니다.');
  };
</script>

<style scoped>
  .sales-page-wrapper {
    padding: 2rem;
  }

  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }

  .button-group {
    display: flex;
    gap: 8px;
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
</style>
