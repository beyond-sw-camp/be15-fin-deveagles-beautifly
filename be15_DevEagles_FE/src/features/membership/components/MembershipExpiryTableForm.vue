<template>
  <div class="base-table-wrapper">
    <BaseTable
      :columns="columns"
      :data="sortedData"
      :striped="true"
      :hover="true"
      :loading="loading"
      row-key="id"
      @row-click="row => openModal(row)"
    >
      <template #cell-name="{ value }">
        <span class="text-blue-500 underline cursor-pointer">{{ value }}</span>
      </template>

      <template #cell-membershipName="{ value }">
        <span class="ellipsis-cell" :title="value">{{ value }}</span>
      </template>

      <template #cell-expiryDate="{ value }">
        <span :class="{ 'text-red-500': isExpiringSoon(value) }">{{ value }}</span>
      </template>

      <template #header-name>
        <span class="sortable-header" @click="toggleSort('name')">
          이름
          <span class="sort-icon">
            <template v-if="sortKey === 'name'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</template>
            <template v-else>⬍</template>
          </span>
        </span>
      </template>

      <template #header-remaining>
        <span class="sortable-header" @click="toggleSort('remaining')">
          잔여
          <span class="sort-icon">
            <template v-if="sortKey === 'remaining'">{{
              sortOrder === 'asc' ? '▲' : '▼'
            }}</template>
            <template v-else>⬍</template>
          </span>
        </span>
      </template>

      <template #header-expiryDate>
        <span class="sortable-header" @click="toggleSort('expiryDate')">
          만료일
          <span class="sort-icon">
            <template v-if="sortKey === 'expiryDate'">{{
              sortOrder === 'asc' ? '▲' : '▼'
            }}</template>
            <template v-else>⬍</template>
          </span>
        </span>
      </template>
    </BaseTable>

    <BasePagination
      :current-page="pagination.currentPage"
      :total-pages="pagination.totalPages"
      :total-items="pagination.totalItems"
      :items-per-page="pageSize"
      @page-change="handlePageChange"
    />

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
    getFilteredExpiringPrepaidPasses,
    getFilteredExpiringSessionPasses,
  } from '@/features/membership/api/membership.js';

  const props = defineProps({
    type: { type: String, default: '선불' },
    searchKeyword: String,
    filter: Object,
  });

  const currentPage = ref(1);
  const pageSize = 10;
  const pagination = ref({ currentPage: 1, totalPages: 1, totalItems: 0 });

  const detailModalVisible = ref(false);
  const selectedCustomer = ref(null);
  const data = ref([]);
  const loading = ref(false);
  const sortKey = ref(null);
  const sortOrder = ref('asc');

  const formatDate = date => (date ? new Date(date).toISOString().split('T')[0] : null);

  const fetchData = async () => {
    loading.value = true;
    try {
      const filter = props.filter ?? {};
      const requestPayload = {
        startDate: formatDate(filter.startDate),
        endDate: formatDate(filter.endDate),
        page: currentPage.value,
        size: pageSize,
      };

      if (props.searchKeyword) {
        requestPayload.searchKeyword = props.searchKeyword;
      }

      let response;
      if (props.type === '선불') {
        requestPayload.minRemainingAmount = filter.minRemainingAmount;
        requestPayload.maxRemainingAmount = filter.maxRemainingAmount;
        response = await getFilteredExpiringPrepaidPasses(requestPayload);
        processResponse(response, 'prepaid');
      } else {
        requestPayload.minRemainingCount = filter.minRemainingCount;
        requestPayload.maxRemainingCount = filter.maxRemainingCount;
        response = await getFilteredExpiringSessionPasses(requestPayload);
        processResponse(response, 'session');
      }

      pagination.value = response.pagination;
    } catch (err) {
      console.error('만료 예정 회원권 조회 실패', err);
    } finally {
      loading.value = false;
    }
  };

  const processResponse = (response, type) => {
    const today = new Date();
    data.value = response.list.map(item => {
      const passes = type === 'prepaid' ? item.prepaidPasses : item.sessionPasses;
      const passNameKey = type === 'prepaid' ? 'prepaidPassName' : 'sessionPassName';
      const remainKey = type === 'prepaid' ? 'remainingAmount' : 'remainingCount';

      const hasValidPass = passes && passes.length > 0;

      const joinedPassNames = hasValidPass
        ? passes.map(p => p[passNameKey]).join(', ')
        : '(만료됨)';

      const totalRemaining = hasValidPass
        ? passes.reduce((sum, p) => {
            const expDate = new Date(p.expirationDate);
            return expDate >= today ? sum + (p[remainKey] ?? 0) : sum;
          }, 0)
        : 0;

      const earliestExpiry = hasValidPass
        ? passes.reduce((earliest, p) => {
            const date = new Date(p.expirationDate);
            return !earliest || date < earliest ? date : earliest;
          }, null)
        : null;

      return {
        id: item.customerId,
        name: item.customerName,
        phone: item.phoneNumber,
        membershipName: joinedPassNames,
        remaining: totalRemaining,
        expiryDate: earliestExpiry ? earliestExpiry.toISOString().split('T')[0] : '(만료됨)',
        raw: item,
      };
    });
  };

  const handlePageChange = page => {
    currentPage.value = page;
    fetchData();
  };

  watch(
    [() => props.type, () => props.filter, () => props.searchKeyword],
    () => {
      currentPage.value = 1;
      fetchData();
    },
    { deep: true, immediate: true }
  );

  const filteredData = computed(() => {
    const keyword = props.searchKeyword?.trim() || '';
    return data.value.filter(item => item.name.includes(keyword));
  });

  const sortedData = computed(() => {
    const list = [...filteredData.value];
    if (!sortKey.value) return list;

    return list.sort((a, b) => {
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

  const columns = computed(() => [
    { key: 'name', title: '이름', width: '70px' },
    { key: 'phone', title: '연락처', width: '70px' },
    {
      key: 'membershipName',
      title: props.type === '선불' ? '선불권명' : '횟수권명',
      width: '180px',
    },
    { key: 'remaining', title: '잔여', width: '100px' },
    { key: 'expiryDate', title: '만료일', width: '160px' },
  ]);

  const openModal = item => {
    const raw = item.raw;
    selectedCustomer.value = {
      id: raw.customerId,
      name: raw.customerName,
      phone: raw.phoneNumber,
      usageHistory: raw.usageHistory ?? [],
    };
    detailModalVisible.value = true;
  };

  const isExpiringSoon = dateStr => {
    if (!dateStr || dateStr === '(만료됨)') return false;
    const today = new Date();
    const expiry = new Date(dateStr);
    const diff = (expiry - today) / (1000 * 60 * 60 * 24);
    return diff >= 0 && diff <= 7;
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
    max-width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }
</style>
