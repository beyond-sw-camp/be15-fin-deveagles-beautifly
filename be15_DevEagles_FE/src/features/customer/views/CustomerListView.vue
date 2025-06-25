<template>
  <div class="customer-list-page">
    <header class="customer-list-header">
      <h1>고객 리스트</h1>
      <div class="customer-list-actions">
        <BaseButton type="primary" size="sm" @click="showCreateDrawer = true"
          >+ 신규 고객 등록</BaseButton
        >
        <div ref="gradeTagDropdownWrapper" class="dropdown-container">
          <BaseButton
            type="primary"
            size="sm"
            @click="showGradeTagDropdown = !showGradeTagDropdown"
          >
            + 등급·태그 설정
          </BaseButton>
          <div v-if="showGradeTagDropdown" class="dropdown-menu">
            <div class="dropdown-item" @click="openGradeSettingsDrawer">등급 설정</div>
            <div class="dropdown-item">태그 설정</div>
          </div>
        </div>
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
      <div class="table-scroll-wrapper">
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
            <button class="sortable-header" type="button" @click="sortBy('customer_name')">
              고객명
              <SortIcon :direction="getSortDirection('customer_name')" />
            </button>
          </template>
          <template #header-visit_count>
            <button class="sortable-header" type="button" @click="sortBy('visit_count')">
              방문횟수
              <SortIcon :direction="getSortDirection('visit_count')" />
            </button>
          </template>
          <template #header-remaining_amount>
            <button class="sortable-header" type="button" @click="sortBy('remaining_amount')">
              잔여선불액
              <SortIcon :direction="getSortDirection('remaining_amount')" />
            </button>
          </template>
          <template #header-total_revenue>
            <button class="sortable-header" type="button" @click="sortBy('total_revenue')">
              누적매출액
              <SortIcon :direction="getSortDirection('total_revenue')" />
            </button>
          </template>
          <template #header-recent_visit_date>
            <button class="sortable-header" type="button" @click="sortBy('recent_visit_date')">
              최근방문일
              <SortIcon :direction="getSortDirection('recent_visit_date')" />
            </button>
          </template>
          <template #body>
            <tr
              v-for="item in pagedData"
              :key="item.customer_id"
              class="clickable-row"
              @click="openDetailModal(item)"
            >
              <td v-for="column in visibleColumns" :key="column.key">
                <template v-if="column.key === 'checkbox'">
                  <div @click.stop>
                    <input v-model="selectedIds" type="checkbox" :value="item.customer_id" />
                  </div>
                </template>
                <template v-else-if="column.key === 'tags'">
                  <div class="tag-cell-wrapper">
                    <template v-if="Array.isArray(item.tags) && item.tags.length > 0">
                      <BaseBadge
                        v-for="tag in item.tags"
                        :key="tag.tag_name"
                        :text="tag.tag_name"
                        :style="{
                          backgroundColor: tag.color_code,
                          color: '#222',
                          marginRight: '4px',
                        }"
                        pill
                      />
                    </template>
                  </div>
                </template>
                <template v-else>
                  <span class="single-line-ellipsis" :title="item[column.key] || ''">
                    {{ item[column.key] }}
                  </span>
                </template>
              </td>
            </tr>
          </template>
        </BaseTable>
      </div>
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

    <CustomerCreateDrawer v-model="showCreateDrawer" @create="handleCreateCustomer" />

    <CustomerEditDrawer
      v-model="showEditDrawer"
      :customer="selectedCustomerEdit"
      @update="handleUpdateCustomer"
      @close="showEditDrawer = false"
    />

    <CustomerGradeSettingsDrawer v-model="showGradeSettingsDrawer" />

    <CustomerColumnSettingsDrawer
      v-model="showColumnDrawer"
      :columns="columns"
      :value="columnSettings"
      @save="applyColumnSettings"
    />

    <CustomerDetailModal
      v-if="selectedCustomer"
      v-model="showDetailModal"
      :customer="selectedCustomer"
      @request-delete="handleRequestDelete"
      @request-edit="handleEditRequest"
    />

    <BaseConfirm
      v-model="showConfirmDelete"
      title="고객 삭제"
      message="정말 삭제하시겠습니까?"
      confirm-text="삭제"
      confirm-type="error"
      @confirm="handleDeleteCustomerConfirmed"
      @cancel="showConfirmDelete = false"
    />

    <BaseToast ref="toastRef" />
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
  import CustomerEditDrawer from '../components/CustomerEditDrawer.vue';
  import CustomerColumnSettingsDrawer from '../components/CustomerColumnSettingsDrawer.vue';
  import CustomerDetailModal from '../components/CustomerDetailModal.vue';
  import BaseConfirm from '@/components/common/BaseConfirm.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import CustomerGradeSettingsDrawer from '../components/CustomerGradeSettingsDrawer.vue';

  const dummyData = ref(
    Array.from({ length: 20 }, (_, i) => ({
      customer_id: 100 - i,
      customer_name: i % 4 === 0 ? '홍길동입니다아' : `고객${100 - i}`,
      phone_number: `010-0000-${String(1000 + i).slice(-4)}`,
      staff_name: i % 2 === 0 ? '부재녕' : '김담당',
      memo:
        i % 3 === 0
          ? '이것은 예시 메모입니다. 길어지면 ...으로 표시됩니다. 아주 긴 메모 테스트입니다. 정말로 깁니다.'
          : '',
      visit_count: Math.floor(Math.random() * 10),
      noshow_count: Math.floor(Math.random() * 2),
      remaining_amount: Math.floor(Math.random() * 100000),
      total_revenue: Math.floor(Math.random() * 1000000),
      recent_visit_date: `2025-06-${String(10 + (i % 20)).padStart(2, '0')}`,
      tags:
        i % 3 !== 0
          ? [
              {
                tag_name: i % 2 === 0 ? 'VIP' : '아주아주 긴 태그명 테스트',
                color_code: i % 2 === 0 ? '#FFD700' : '#00BFFF',
              },
            ]
          : [],
      customer_grade_name: i % 2 === 0 ? '일반' : '아주아주 긴 등급명 테스트',
      birthdate: `1990-${String(i + 1).padStart(2, '0')}-15`,
      gender: i % 2 === 0 ? '남성' : '여성',
      channel_id: i % 2 === 0 ? 1 : 2,
      acquisition_channel_name: i % 2 === 0 ? '네이버검색' : '지인 추천',
      created_at: new Date(2025, 5, 30 - i).toISOString(),
      customer_session_passes:
        i % 4 === 0
          ? [
              {
                customer_session_pass_id: i * 10 + 1,
                session_pass_name: '10회 이용권',
                remaining_count: 5,
                expiration_date: '2025-12-31',
              },
            ]
          : [],
      customer_prepaid_passes:
        i % 5 === 0
          ? [
              {
                customer_prepaid_pass_id: i * 10 + 2,
                prepaid_pass_name: '10만원 정액권',
                remaining_amount: 35000,
                expiration_date: '2026-06-30',
              },
            ]
          : [],
    }))
  );

  const columns = ref([
    { key: 'checkbox', title: '', width: '50px' },
    { key: 'customer_name', title: '고객명', width: '110px' },
    { key: 'phone_number', title: '연락처', width: '130px' },
    { key: 'staff_name', title: '담당자', width: '90px' },
    { key: 'acquisition_channel_name', title: '유입경로', width: '100px' },
    { key: 'memo', title: '메모', width: '150px' },
    { key: 'visit_count', title: '방문횟수', width: '90px' },
    { key: 'remaining_amount', title: '잔여선불액', width: '110px' },
    { key: 'total_revenue', title: '누적매출액', width: '110px' },
    { key: 'recent_visit_date', title: '최근방문일', width: '120px' },
    { key: 'tags', title: '태그', width: '120px' },
    { key: 'customer_grade_name', title: '등급', width: '90px' },
    { key: 'birthdate', title: '생일', width: '110px' },
  ]);

  const toastRef = ref(null);
  const showCreateDrawer = ref(false);
  const showEditDrawer = ref(false);
  const showDetailModal = ref(false);
  const selectedCustomer = ref(null);
  const selectedCustomerEdit = ref(null);
  const showConfirmDelete = ref(false);
  const customerIdToDelete = ref(null);

  const showGradeTagDropdown = ref(false);
  const gradeTagDropdownWrapper = ref(null);
  const showGradeSettingsDrawer = ref(false);

  const openGradeSettingsDrawer = () => {
    showGradeSettingsDrawer.value = true;
    showGradeTagDropdown.value = false;
  };

  const handleRequestDelete = customerId => {
    customerIdToDelete.value = customerId;
    showConfirmDelete.value = true;
  };

  const handleDeleteCustomerConfirmed = () => {
    if (customerIdToDelete.value) {
      dummyData.value = dummyData.value.filter(c => c.customer_id !== customerIdToDelete.value);
      toastRef.value?.success('고객 정보가 삭제되었습니다.');
      customerIdToDelete.value = null;
      showDetailModal.value = false;
    }
  };

  function openDetailModal(customer) {
    selectedCustomer.value = customer;
    showDetailModal.value = true;
  }

  function handleEditRequest(customer) {
    selectedCustomerEdit.value = customer;
    showEditDrawer.value = true;
    showDetailModal.value = false;
  }

  function handleUpdateCustomer(updatedCustomer) {
    const idx = dummyData.value.findIndex(c => c.customer_id === updatedCustomer.customer_id);
    if (idx !== -1) dummyData.value[idx] = { ...updatedCustomer };
    toastRef.value?.success('고객 수정 완료');
    showEditDrawer.value = false;
  }

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
    if (gradeTagDropdownWrapper.value && !gradeTagDropdownWrapper.value.contains(event.target)) {
      showGradeTagDropdown.value = false;
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

  function handleCreateCustomer(newCustomer) {
    const channel = acquisitionChannelOptions.find(c => c.channel_id === newCustomer.channel_id);

    dummyData.value.unshift({
      customer_id: Date.now(),
      customer_name: newCustomer.name,
      phone_number: newCustomer.phone,
      staff_name: newCustomer.staff_name,
      memo: newCustomer.memo,
      visit_count: 0,
      noshow_count: 0,
      remaining_amount: 0,
      total_revenue: 0,
      recent_visit_date: null,
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
      birthdate: newCustomer.birthdate || null,
      gender: newCustomer.gender || null,
      customer_session_passes: [],
      customer_prepaid_passes: [],
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
  .clickable-row {
    cursor: pointer;
  }
  .clickable-row:hover {
    background-color: #f9f9f9;
  }
  .customer-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
  .customer-list-actions {
    display: flex;
    gap: 8px;
  }
  .dropdown-container {
    position: relative;
    display: inline-block;
  }
  .dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    margin-top: 8px;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 100;
    padding: 0.5rem 0;
    min-width: 160px;
  }
  .dropdown-item {
    padding: 0.75rem 1rem;
    cursor: pointer;
    font-size: 0.9rem;
    color: #333;
  }
  .dropdown-item:hover {
    background-color: #f5f5f5;
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

  /* 테이블 스크롤 래퍼 */
  .table-scroll-wrapper {
    width: 100%;
    overflow-x: auto;
  }
  .wide-table :deep(.table-container) {
    width: 100%;
    min-width: 100%;
    overflow-x: auto;
  }
  .wide-table :deep(.table) {
    table-layout: fixed;
    width: 100%;
    min-width: 100%;
  }

  .single-line-ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
  }
  .tag-cell-wrapper {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: flex;
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
    justify-content: flex-start;
    cursor: pointer;
    user-select: none;
    gap: 4px;
    white-space: nowrap;
    padding: 0 8px;
    height: 42px;
    width: 100%;
    font-size: 15px;
    font-weight: 500;
    background: transparent;
    border: none;
    outline: none;
    transition: background 0.15s;
    text-align: left;
  }
  .sortable-header:hover {
    background: #f0f4fa;
  }
  .customer-list-pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
</style>
