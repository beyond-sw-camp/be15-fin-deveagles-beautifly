<template>
  <div class="customer-list-page">
    <header class="customer-list-header">
      <h1>고객 리스트</h1>
      <div class="customer-list-actions">
        <BaseButton type="primary" size="sm" @click="showCreateDrawer = true"
          >+ 신규 고객 등록</BaseButton
        >
        <BaseButton type="primary" size="sm">+ 등급·태그 설정</BaseButton>
      </div>
    </header>

    <div class="customer-list-toolbar">
      <div class="toolbar-left">
        <div class="search-form-wrapper">
          <span class="search-icon">
            <SearchIcon />
          </span>
          <input
            v-model="search"
            type="text"
            class="search-input"
            placeholder="고객명, 연락처"
            @keyup.enter="handleSearch"
          />
        </div>
        <BaseButton type="primary" size="sm" class="filter-btn" @click="handleSearch">
          <FilterIcon style="margin-right: 4px" />
          필터
        </BaseButton>
        <BaseButton type="primary" size="sm" class="settings-btn" @click="openColumnDrawer">
          <SettingsIcon />
        </BaseButton>
      </div>
      <div class="toolbar-right">
        <BaseButton type="primary" size="sm" class="sms-btn" @click="onSendMessage">
          <SendHorizonalIcon style="margin-right: 4px" :size="18" />
          문자 발송
        </BaseButton>
      </div>
    </div>

    <BaseCard shadow="sm" no-padding>
      <div class="table-header-count">
        <span class="customer-count">
          총 <span class="customer-count-num">{{ total }}</span
          >명
        </span>
      </div>
      <BaseTable
        :columns="visibleColumns"
        :data="pagedData"
        :loading="loading"
        row-key="customer_id"
        striped
        hover
        class="wide-table"
      >
        <template #header-checkbox>
          <div class="dropdown-checkbox-wrapper">
            <input
              ref="checkboxDropdownRef"
              type="checkbox"
              class="header-checkbox"
              :checked="isAllSelected || isPageSelected"
              readonly
              @click="toggleDropdown"
            />
            <div v-if="showDropdown" class="select-dropdown-menu">
              <div class="select-dropdown-item" @mousedown.prevent="selectAll('all')">
                전체 선택
              </div>
              <div class="select-dropdown-item" @mousedown.prevent="selectAll('page')">
                현재 페이지만 선택
              </div>
            </div>
          </div>
        </template>
        <template #header-customer_name>
          <div class="sortable-header" @click="sortBy('customer_name')">
            고객명
            <SortIcon :direction="getSortDirection('customer_name')" />
          </div>
        </template>
        <template #header-visit_count>
          <div class="sortable-header" @click="sortBy('visit_count')">
            방문횟수
            <SortIcon :direction="getSortDirection('visit_count')" />
          </div>
        </template>
        <template #header-remaining_amount>
          <div class="sortable-header" @click="sortBy('remaining_amount')">
            잔여선불액
            <SortIcon :direction="getSortDirection('remaining_amount')" />
          </div>
        </template>
        <template #header-total_revenue>
          <div class="sortable-header" @click="sortBy('total_revenue')">
            누적매출액
            <SortIcon :direction="getSortDirection('total_revenue')" />
          </div>
        </template>
        <template #header-recent_visit_date>
          <div class="sortable-header" @click="sortBy('recent_visit_date')">
            최근방문일
            <SortIcon :direction="getSortDirection('recent_visit_date')" />
          </div>
        </template>
        <template #cell-checkbox="{ item }">
          <input v-model="selectedIds" type="checkbox" :value="item.customer_id" />
        </template>
        <template #cell-memo="{ value }">
          <span class="memo-ellipsis" :title="value">{{ value }}</span>
        </template>
        <template #cell-tags="{ item }">
          <template v-if="Array.isArray(item.tags) && item.tags.length > 0">
            <BaseBadge
              v-for="tag in item.tags"
              :key="tag.tag_name"
              :text="tag.tag_name"
              :style="{ backgroundColor: tag.color_code, color: '#222', marginRight: '4px' }"
              pill
            />
          </template>
        </template>
        <template #cell-customer_grade_name="{ value }">
          <span class="single-line-ellipsis" :title="value">{{ value }}</span>
        </template>
      </BaseTable>
    </BaseCard>

    <div class="customer-list-pagination">
      <Pagination
        :current-page="page"
        :total-pages="totalPages"
        :total-items="total"
        :items-per-page="pageSize"
        @page-change="handlePageChange"
      />
    </div>

    <!-- 신규 고객 등록 Drawer -->
    <CustomerCreateDrawer v-model="showCreateDrawer" @create="handleCreateCustomer" />

    <!-- 컬럼 설정 Drawer -->
    <CustomerColumnSettingsDrawer
      v-model="showColumnDrawer"
      :columns="columns"
      :value="columnSettings"
      @save="applyColumnSettings"
    />

    <BaseToast ref="toastRef" position="top-right" />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import SettingsIcon from '@/components/icons/SettingsIcon.vue';
  import FilterIcon from '@/components/icons/FilterIcon.vue';
  import SortIcon from '@/components/icons/SortIcon.vue';
  import SearchIcon from '@/components/icons/SearchIcon.vue';
  import { SendHorizonalIcon } from 'lucide-vue-next';
  import CustomerCreateDrawer from '../components/CustomerCreateDrawer.vue';
  import CustomerColumnSettingsDrawer from '../components/CustomerColumnSettingsDrawer.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  const dummyData = ref(
    Array.from({ length: 20 }, (_, i) => ({
      customer_id: 100 - i,
      customer_name: `고객${100 - i}`,
      phone_number: `010-0000-${String(1000 + i).slice(-4)}`,
      staff_name: i % 2 === 0 ? '부재녕' : '김담당',
      memo: i % 3 === 0 ? '이것은 예시 메모입니다. 길어지면 ...으로 표시됩니다.' : '',
      visit_count: Math.floor(Math.random() * 10),
      remaining_amount: Math.floor(Math.random() * 100000),
      total_revenue: Math.floor(Math.random() * 1000000),
      recent_visit_date: `2025-06-${String(10 + (i % 20)).padStart(2, '0')}`,
      tags:
        i % 3 !== 0
          ? [
              {
                tag_name: i % 2 === 0 ? 'VIP' : '신규',
                color_code: i % 2 === 0 ? '#FFD700' : '#00BFFF',
              },
            ]
          : [],
      customer_grade_name: i % 2 === 0 ? '일반' : '아주아주 긴 등급명 테스트',
      birthdate: `1990${String(i + 1).padStart(2, '0')}15`,
      channel_id: i % 2 === 0 ? 1 : 2,
      acquisition_channel_name: i % 2 === 0 ? '네이버검색' : '지인 추천',
      created_at: new Date(2025, 5, 30 - i).toISOString(),
    }))
  );

  const columns = ref([
    { key: 'checkbox', title: '', width: '60px' },
    { key: 'customer_name', title: '고객명', width: '170px' },
    { key: 'phone_number', title: '연락처', width: '160px' },
    { key: 'staff_name', title: '담당자', width: '110px' },
    { key: 'acquisition_channel_name', title: '유입경로', width: '120px' },
    { key: 'memo', title: '메모', width: '170px' },
    { key: 'visit_count', title: '방문횟수', width: '120px' },
    { key: 'remaining_amount', title: '잔여선불액', width: '130px' },
    { key: 'total_revenue', title: '누적매출액', width: '130px' },
    { key: 'recent_visit_date', title: '최근방문일', width: '130px' },
    { key: 'tags', title: '태그', width: '110px' },
    { key: 'customer_grade_name', title: '등급', width: '80px' },
    { key: 'birthdate', title: '생일', width: '110px' },
  ]);

  // 컬럼 설정 로직
  const showColumnDrawer = ref(false);
  const columnSettings = ref(
    columns.value
      .filter(col => col.key !== 'checkbox')
      .map(col => ({ key: col.key, title: col.title, visible: true }))
  );

  function openColumnDrawer() {
    showColumnDrawer.value = true;
  }

  function applyColumnSettings(newSettings) {
    columnSettings.value = newSettings;
  }

  const visibleColumns = computed(() => {
    const checkboxCol = columns.value.find(c => c.key === 'checkbox');
    const settingOrder = columnSettings.value.map(s => s.key);
    const visibleCols = columns.value
      .filter(
        c => c.key !== 'checkbox' && columnSettings.value.find(s => s.key === c.key && s.visible)
      )
      .sort((a, b) => settingOrder.indexOf(a.key) - settingOrder.indexOf(b.key));

    return [checkboxCol, ...visibleCols].filter(Boolean);
  });

  const search = ref('');
  const page = ref(1);
  const pageSize = ref(10);
  const selectedIds = ref([]);
  const loading = ref(false);
  const sortKey = ref('created_at');
  const sortOrder = ref('desc');
  const sortableKeys = [
    'customer_name',
    'visit_count',
    'remaining_amount',
    'total_revenue',
    'recent_visit_date',
  ];

  function sortBy(key) {
    if (sortKey.value === key) {
      sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
      sortKey.value = key;
      sortOrder.value = 'desc';
    }
  }
  function getSortDirection(key) {
    return sortKey.value === key ? sortOrder.value : '';
  }

  const sortedData = computed(() => {
    let data = dummyData.value.filter(
      c =>
        !search.value ||
        c.customer_name.includes(search.value) ||
        c.phone_number.includes(search.value)
    );
    if (sortableKeys.includes(sortKey.value)) {
      data = [...data].sort((a, b) => {
        let aValue = a[sortKey.value];
        let bValue = b[sortKey.value];
        if (sortKey.value === 'recent_visit_date') {
          aValue = aValue || '';
          bValue = bValue || '';
          return sortOrder.value === 'asc'
            ? aValue.localeCompare(bValue)
            : bValue.localeCompare(aValue);
        }
        if (['visit_count', 'remaining_amount', 'total_revenue'].includes(sortKey.value)) {
          aValue = Number(aValue);
          bValue = Number(bValue);
          return sortOrder.value === 'asc' ? aValue - bValue : bValue - aValue;
        }
        return sortOrder.value === 'asc'
          ? String(aValue).localeCompare(String(bValue))
          : String(bValue).localeCompare(String(aValue));
      });
    } else {
      data = [...data].sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
    }
    return data;
  });

  const total = computed(() => sortedData.value.length);
  const totalPages = computed(() => Math.ceil(total.value / pageSize.value));
  const pagedData = computed(() => {
    const start = (page.value - 1) * pageSize.value;
    return sortedData.value.slice(start, start + pageSize.value);
  });

  const showDropdown = ref(false);
  const checkboxDropdownRef = ref(null);

  const isAllSelected = computed(() => {
    return selectedIds.value.length === sortedData.value.length && sortedData.value.length > 0;
  });
  const isPageSelected = computed(() => {
    const pageIds = pagedData.value.map(item => item.customer_id);
    return (
      pageIds.length > 0 &&
      pageIds.every(id => selectedIds.value.includes(id)) &&
      !isAllSelected.value
    );
  });

  function toggleDropdown(e) {
    if (isAllSelected.value) {
      selectedIds.value = [];
      showDropdown.value = false;
    } else if (isPageSelected.value) {
      const pageIds = pagedData.value.map(item => item.customer_id);
      selectedIds.value = selectedIds.value.filter(id => !pageIds.includes(id));
      showDropdown.value = false;
    } else {
      showDropdown.value = !showDropdown.value;
    }
    e.stopPropagation();
  }
  function selectAll(mode) {
    if (mode === 'all') {
      selectedIds.value = sortedData.value.map(item => item.customer_id);
    } else {
      const pageIds = pagedData.value.map(item => item.customer_id);
      selectedIds.value = Array.from(new Set([...selectedIds.value, ...pageIds]));
    }
    showDropdown.value = false;
  }
  function handleClickOutside(event) {
    if (checkboxDropdownRef.value && !checkboxDropdownRef.value.contains(event.target)) {
      showDropdown.value = false;
    }
  }
  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
  });
  onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside);
  });

  function handleSearch() {
    page.value = 1;
  }
  function handlePageChange(newPage) {
    page.value = newPage;
  }
  function onSendMessage() {}

  const acquisitionChannelOptions = [
    { channel_id: 1, channel_name: '네이버검색' },
    { channel_id: 2, channel_name: '지인 추천' },
  ];

  const toastRef = ref(null);
  const showCreateDrawer = ref(false);
  function handleCreateCustomer(newCustomer) {
    const channel = acquisitionChannelOptions.find(c => c.channel_id === newCustomer.channel_id);

    dummyData.value.unshift({
      customer_id: Date.now(),
      customer_name: newCustomer.name,
      phone_number: newCustomer.phone,
      staff_name: newCustomer.staff_name,
      memo: newCustomer.memo,
      visit_count: 0,
      remaining_amount: 0,
      total_revenue: 0,
      recent_visit_date: newCustomer.birthdate || '',
      channel_id: newCustomer.channel_id,
      acquisition_channel_name: channel ? channel.channel_name : '',
      tags:
        Array.isArray(newCustomer.tags) && newCustomer.tags.length > 0
          ? newCustomer.tags.map((tag, idx) => ({
              tag_id: Date.now() + idx,
              ...tag,
            }))
          : [],
      customer_grade_name: newCustomer.grade,
      birthdate: newCustomer.birthdate || '',
      created_at: new Date().toISOString(),
    });
    toastRef.value?.success('고객 등록 완료', { duration: 2000 });
    sortKey.value = 'created_at';
    sortOrder.value = 'desc';
    page.value = 1;
  }
</script>

<style scoped>
  .customer-list-page {
    max-width: 1700px;
    margin: 0 auto;
    padding: 32px 16px;
  }
  .customer-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
  .customer-list-actions > * + * {
    margin-left: 8px;
  }
  .customer-list-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 18px;
    gap: 16px;
  }
  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .search-form-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    height: 40px;
  }
  .search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #aaa;
    z-index: 2;
    pointer-events: none;
    font-size: 18px;
    width: 18px;
    height: 18px;
    display: flex;
    align-items: center;
  }
  .search-input {
    width: 260px;
    height: 40px;
    padding-left: 38px;
    padding-right: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 15px;
    background: #fff;
    box-sizing: border-box;
    transition: border 0.2s;
    line-height: 40px;
  }
  .search-input:focus {
    outline: none;
    border-color: #364f6b;
    background: #f8fafd;
  }
  .filter-btn,
  .sms-btn,
  .settings-btn {
    height: 40px;
    display: flex;
    align-items: center;
  }
  .sms-btn svg,
  .settings-btn svg {
    margin-right: 0;
    vertical-align: middle;
  }
  .settings-btn {
    padding-left: 10px;
    padding-right: 10px;
    min-width: 40px;
    justify-content: center;
  }
  .table-header-count {
    display: flex;
    align-items: center;
    padding: 12px 16px 0 16px;
    font-size: 15px;
    font-weight: 500;
    color: #222;
  }
  .customer-count {
    margin-right: 16px;
  }
  .customer-count-num {
    font-size: 17px;
    font-weight: bold;
    vertical-align: middle;
  }
  .wide-table .table {
    width: 100%;
    min-width: 1700px;
    table-layout: fixed;
    font-size: 15px;
  }
  .memo-ellipsis {
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    max-width: 150px;
  }
  .single-line-ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
  }
  .dropdown-checkbox-wrapper {
    position: relative;
    display: flex;
    align-items: center;
  }
  .header-checkbox {
    cursor: pointer;
    accent-color: #3fc1c9;
    margin-right: 0;
  }
  .select-dropdown-menu {
    position: absolute;
    left: 0;
    top: 38px;
    min-width: 110px;
    font-size: 14px;
    z-index: 20;
    border-radius: 6px;
    border: 1px solid #ccc;
    padding: 2px 0;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
  .select-dropdown-item {
    padding: 7px 16px;
    cursor: pointer;
    font-size: 14px;
    color: #222;
    transition: background 0.15s;
    font-family: inherit;
    white-space: nowrap;
  }
  .select-dropdown-item:hover {
    background: #f2f2f2;
  }
  .sortable-header {
    display: flex;
    align-items: center;
    cursor: pointer;
    user-select: none;
    gap: 4px;
    white-space: nowrap;
  }
  .customer-list-pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
</style>
