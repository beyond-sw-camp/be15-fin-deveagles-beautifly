<template>
  <div class="base-table-wrapper">
    <!-- 정렬 가능한 테이블 -->
    <BaseTable
      :columns="columns"
      :data="sortedData"
      :striped="true"
      :hover="true"
      :loading="loading"
      row-key="id"
      @row-click="(row, $event) => openModal(row, $event)"
    >
      <!-- 이름 정렬 -->
      <template #header-name>
        <span class="sortable-header" @click="toggleSort('name')">
          이름
          <span class="sort-icon">
            <template v-if="sortKey === 'name'">
              {{ sortOrder === 'asc' ? '▲' : '▼' }}
            </template>
            <template v-else>⬍</template>
          </span>
        </span>
      </template>

      <!-- 잔여선불권 정렬 -->
      <template #header-remaining>
        <span class="sortable-header" @click="toggleSort('remaining')">
          잔여선불권
          <span class="sort-icon">
            <template v-if="sortKey === 'remaining'">
              {{ sortOrder === 'asc' ? '▲' : '▼' }}
            </template>
            <template v-else>⬍</template>
          </span>
        </span>
      </template>

      <!-- 만료일 정렬 + 강조 -->
      <template #header-expiryDate>
        <span class="sortable-header" @click="toggleSort('expiryDate')">
          만료일
          <span class="sort-icon">
            <template v-if="sortKey === 'expiryDate'">
              {{ sortOrder === 'asc' ? '▲' : '▼' }}
            </template>
            <template v-else>⬍</template>
          </span>
        </span>
      </template>

      <template #cell-used="{ value }">
        <span class="ellipsis-cell" :title="value">{{ value }}</span>
      </template>

      <template #cell-expiryDate="{ value }">
        <span :class="{ 'text-red-500': isExpiringSoon(value) }">
          {{ value }}
        </span>
      </template>
    </BaseTable>

    <!-- 페이지네이션 -->
    <BasePagination
      :current-page="pagination.currentPage"
      :total-pages="pagination.totalPages"
      :total-items="pagination.totalItems"
      :items-per-page="pageSize"
      @page-change="handlePageChange"
    />

    <!-- 상세 모달 -->
    <CustomerMembershipDetailModal
      v-if="detailModalVisible"
      v-model="detailModalVisible"
      :customer="selectedCustomer"
    />
  </div>
</template>

<script setup>
  import { ref, computed, watch } from 'vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BasePagination from '@/components/common/Pagination.vue';
  import CustomerMembershipDetailModal from '@/features/membership/components/CustomerMembershipDetailModal.vue';
  import {
    getAllCustomerMemberships,
    getFilteredCustomerMemberships,
  } from '@/features/membership/api/membership.js';

  const props = defineProps({
    searchKeyword: String,
    filterState: Object,
  });

  const fullList = ref([]);
  const loading = ref(false);
  const sortKey = ref(null);
  const sortOrder = ref('asc');

  const currentPage = ref(1);
  const pageSize = 10;
  const pagination = ref({
    currentPage: 1,
    totalPages: 1,
    totalItems: 0,
  });

  const fetchMembershipList = async () => {
    loading.value = true;
    try {
      let result;
      if (props.filterState?.min != null || props.filterState?.max != null) {
        result = await getFilteredCustomerMemberships({
          minRemainingAmount: props.filterState?.min ?? null,
          maxRemainingAmount: props.filterState?.max ?? null,
          page: currentPage.value,
          size: pageSize,
        });
      } else {
        result = await getAllCustomerMemberships(currentPage.value, pageSize);
      }

      fullList.value = result.list.map(item => ({
        id: item.customerId,
        name: item.customerName,
        phone: item.phoneNumber,
        remaining: item.totalRemainingPrepaidAmount,
        used: item.sessionPasses?.map(s => `(상품) ${s.sessionPassName}`).join(', ') || '-',
        expiryDate: item.expirationDate || '-',
        memberships: [],
        usageHistory: [],
      }));

      pagination.value = result.pagination;
    } catch (e) {
      console.error('회원권 전체 조회 실패', e);
    } finally {
      loading.value = false;
    }
  };

  watch(
    () => [props.searchKeyword, props.filterState],
    () => {
      currentPage.value = 1;
      fetchMembershipList();
    },
    { immediate: true }
  );

  const handlePageChange = page => {
    currentPage.value = page;
    fetchMembershipList();
  };

  const filteredData = computed(() => {
    return fullList.value.filter(member => member.name.includes(props.searchKeyword?.trim() || ''));
  });

  const sortedData = computed(() => {
    const sorted = [...filteredData.value];
    if (!sortKey.value) return sorted;

    return sorted.sort((a, b) => {
      const aVal = a[sortKey.value];
      const bVal = b[sortKey.value];

      if (sortKey.value === 'name') {
        return sortOrder.value === 'asc' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
      }

      if (sortKey.value === 'expiryDate') {
        const aDate = new Date(aVal);
        const bDate = new Date(bVal);
        return sortOrder.value === 'asc' ? aDate - bDate : bDate - aDate;
      }

      return sortOrder.value === 'asc' ? aVal - bVal : bVal - aVal;
    });
  });

  const toggleSort = key => {
    if (sortKey.value === key) {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
      sortKey.value = key;
      sortOrder.value = 'asc';
    }
  };

  const columns = [
    { key: 'name', title: '이름', width: '120px' },
    { key: 'phone', title: '연락처', width: '140px' },
    { key: 'remaining', title: '잔여선불권', width: '100px' },
    { key: 'used', title: '보유횟수권', width: '300px' },
    { key: 'expiryDate', title: '만료일', width: '120px' },
  ];

  const isExpiringSoon = dateStr => {
    if (!dateStr) return false;
    const today = new Date();
    const expiry = new Date(dateStr);
    const diff = (expiry - today) / (1000 * 60 * 60 * 24);
    return diff >= 0 && diff <= 7;
  };

  const detailModalVisible = ref(false);
  const selectedCustomer = ref(null);

  const openModal = (item, event) => {
    const target = event?.target;
    const isInsideCell = target?.closest('td');
    if (!isInsideCell) return;

    selectedCustomer.value = item;
    detailModalVisible.value = true;
  };
</script>

<style scoped>
  .base-table-wrapper {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 24px;
    margin-bottom: 24px;
  }
  .sortable-header {
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }
  .sort-icon {
    font-size: 12px;
    color: #888;
  }

  .ellipsis-cell {
    display: inline-block;
    max-width: 280px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }
</style>
