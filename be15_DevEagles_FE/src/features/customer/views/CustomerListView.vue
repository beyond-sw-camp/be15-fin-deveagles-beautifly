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
            <div class="dropdown-item" @click="openTagSettingsDrawer">태그 설정</div>
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
        <BaseButton type="primary" size="sm" class="filter-btn" @click="showFilterModal = true">
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

    <!-- 적용된 필터 표시 영역 -->
    <div v-if="activeFilterPills.length > 0" class="applied-filters-container">
      <div class="filter-pills-wrapper">
        <BaseBadge
          v-for="pill in activeFilterPills"
          :key="pill.id"
          type="primary"
          pill
          class="filter-pill-base-badge"
        >
          {{ pill.label }}: {{ pill.valueText }}
          <button class="remove-pill-btn" @click="removeFilter(pill)">
            <XIcon :size="14" />
          </button>
        </BaseBadge>
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
      :z-index="10002"
      @update="handleUpdateCustomer"
      @close="showEditDrawer = false"
    />

    <CustomerGradeSettingsDrawer v-model="showGradeSettingsDrawer" />

    <CustomerTagSettingsDrawer v-model="showTagSettingsDrawer" />

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

    <CustomerFilterModal
      v-model="showFilterModal"
      :initial-filters="activeFilters"
      @apply-filters="handleApplyFilters"
    />

    <BaseConfirm
      v-model="showConfirmDelete"
      title="고객 삭제"
      message="정말 삭제하시겠습니까?"
      confirm-text="삭제"
      confirm-type="error"
      :z-index="10001"
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
  import { SendHorizonalIcon, XIcon } from 'lucide-vue-next';
  import CustomerCreateDrawer from '../components/CustomerCreateDrawer.vue';
  import CustomerEditDrawer from '../components/CustomerEditDrawer.vue';
  import CustomerColumnSettingsDrawer from '../components/CustomerColumnSettingsDrawer.vue';
  import CustomerDetailModal from '../components/CustomerDetailModal.vue';
  import CustomerFilterModal from '../components/CustomerFilterModal.vue';
  import BaseConfirm from '@/components/common/BaseConfirm.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import CustomerGradeSettingsDrawer from '../components/CustomerGradeSettingsDrawer.vue';
  import CustomerTagSettingsDrawer from '../components/CustomerTagSettingsDrawer.vue';
  import customersAPI from '../api/customers.js';
  import tagsAPI from '../api/tags.js';
  import gradesAPI from '../api/grades.js';
  import { getStaff } from '@/features/staffs/api/staffs.js';
  import { useAuthStore } from '@/store/auth.js';

  const activeFilters = ref({});

  const authStoreMeta = useAuthStore();

  // 백엔드 데이터 저장소
  const tags = ref([]);
  const staff = ref([]);
  const grades = ref([]);
  const customerList = ref([]);

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
  const showTagSettingsDrawer = ref(false);

  const showFilterModal = ref(false);

  const showColumnDrawer = ref(false);
  const columnSettings = ref(
    columns.value
      .filter(col => col.key !== 'checkbox')
      .map(col => ({ key: col.key, title: col.title, visible: true }))
  );

  const authStore = useAuthStore();

  const openGradeSettingsDrawer = () => {
    showGradeSettingsDrawer.value = true;
    showGradeTagDropdown.value = false;
  };

  const openTagSettingsDrawer = () => {
    showTagSettingsDrawer.value = true;
    showGradeTagDropdown.value = false;
  };

  const handleRequestDelete = customerId => {
    customerIdToDelete.value = customerId;
    showConfirmDelete.value = true;
  };

  const handleDeleteCustomerConfirmed = () => {
    if (customerIdToDelete.value) {
      customerList.value = customerList.value.filter(
        c => c.customer_id !== customerIdToDelete.value
      );
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
    // showDetailModal.value = false; // [수정] 상세 모달이 닫히지 않도록 이 줄을 삭제
  }

  async function handleUpdateCustomer(updatedCustomer) {
    try {
      await customersAPI.updateCustomer(updatedCustomer.customer_id, updatedCustomer);
      toastRef.value?.success('고객 수정 완료');
      await loadCustomers();
      showEditDrawer.value = false;
    } catch (err) {
      toastRef.value?.success(err.message || '고객 수정 실패');
    }
  }

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

  const filteredData = computed(() => {
    let data = [...customerList.value];

    // 1. 검색어 필터링
    if (search.value) {
      data = data.filter(
        c => c.customer_name.includes(search.value) || c.phone_number.includes(search.value)
      );
    }

    const filters = activeFilters.value;
    const hasActiveFilter = Object.keys(filters).length > 0;

    if (hasActiveFilter) {
      const checkFunctions = [];
      const searchType = filters.searchType || 'and';

      // 성별
      if (filters.gender?.length > 0) {
        checkFunctions.push(c => filters.gender.includes(c.gender));
      }
      // 태그
      if (filters.tags?.length > 0) {
        checkFunctions.push(c => c.tags.some(t => filters.tags.includes(t.tag_id)));
      }
      // 담당자
      if (filters.staff?.length > 0) {
        const staffNames = filters.staff.map(id => staff.value.find(s => s.id === id)?.name);
        checkFunctions.push(c => staffNames.includes(c.staff_name));
      }
      // 등급
      if (filters.grades?.length > 0) {
        const gradeNames = filters.grades.map(id => grades.value.find(g => g.id === id)?.name);
        checkFunctions.push(c => gradeNames.includes(c.customer_grade_name));
      }
      // 생일 (이번달, 이번주, 오늘)
      if (filters.birthday?.length > 0) {
        checkFunctions.push(c => {
          if (!c.birthdate) return false;
          const today = new Date();
          const birthDateThisYear = new Date(
            today.getFullYear(),
            parseInt(c.birthdate.substring(5, 7)) - 1,
            parseInt(c.birthdate.substring(8, 10))
          );

          if (filters.birthday.includes('today')) {
            if (
              birthDateThisYear.getMonth() === today.getMonth() &&
              birthDateThisYear.getDate() === today.getDate()
            )
              return true;
          }
          if (filters.birthday.includes('this_month')) {
            if (birthDateThisYear.getMonth() === today.getMonth()) return true;
          }
          if (filters.birthday.includes('this_week')) {
            const weekStart = new Date(today);
            weekStart.setDate(weekStart.getDate() - today.getDay());
            weekStart.setHours(0, 0, 0, 0);

            const weekEnd = new Date(weekStart);
            weekEnd.setDate(weekEnd.getDate() + 6);
            weekEnd.setHours(23, 59, 59, 999);

            if (birthDateThisYear >= weekStart && birthDateThisYear <= weekEnd) return true;
          }
          return false;
        });
      }
      // 출생연도
      if (filters.birthYearStart || filters.birthYearEnd) {
        checkFunctions.push(c => {
          if (!c.birthdate) return false;
          const birthYear = parseInt(c.birthdate.substring(0, 4));
          const start = filters.birthYearStart;
          const end = filters.birthYearEnd;
          return (!start || birthYear >= start) && (!end || birthYear <= end);
        });
      }
      // 선불액 구매 내역
      if (filters.prepaidHistory?.length > 0) {
        checkFunctions.push(c => {
          const hasPrepaid = c.customer_prepaid_passes && c.customer_prepaid_passes.length > 0;
          return (
            (filters.prepaidHistory.includes('yes') && hasPrepaid) ||
            (filters.prepaidHistory.includes('no') && !hasPrepaid)
          );
        });
      }
      // 횟수권 구매 내역
      if (filters.sessionPassHistory?.length > 0) {
        checkFunctions.push(c => {
          const hasSessionPass = c.customer_session_passes && c.customer_session_passes.length > 0;
          return (
            (filters.sessionPassHistory.includes('yes') && hasSessionPass) ||
            (filters.sessionPassHistory.includes('no') && !hasSessionPass)
          );
        });
      }
      // 사용 가능 선불액
      if (filters.usablePrepaid?.length > 0) {
        checkFunctions.push(c => {
          const hasUsable =
            c.customer_prepaid_passes &&
            c.customer_prepaid_passes.some(p => p.remaining_amount > 0);
          return (
            (filters.usablePrepaid.includes('yes') && hasUsable) ||
            (filters.usablePrepaid.includes('no') && !hasUsable)
          );
        });
      }
      // 사용 가능 횟수권
      if (filters.usableSessionPass?.length > 0) {
        checkFunctions.push(c => {
          const hasUsable =
            c.customer_session_passes && c.customer_session_passes.some(p => p.remaining_count > 0);
          return (
            (filters.usableSessionPass.includes('yes') && hasUsable) ||
            (filters.usableSessionPass.includes('no') && !hasUsable)
          );
        });
      }
      // 누적 매출액
      if (
        filters.totalRevenue &&
        (filters.totalRevenue.from != null || filters.totalRevenue.to != null)
      ) {
        checkFunctions.push(
          c =>
            (filters.totalRevenue.from == null || c.total_revenue >= filters.totalRevenue.from) &&
            (filters.totalRevenue.to == null || c.total_revenue <= filters.totalRevenue.to)
        );
      }
      // 잔여 선불액
      if (
        filters.remainingPrepaid &&
        (filters.remainingPrepaid.from != null || filters.remainingPrepaid.to != null)
      ) {
        checkFunctions.push(
          c =>
            (filters.remainingPrepaid.from == null ||
              c.remaining_amount >= filters.remainingPrepaid.from) &&
            (filters.remainingPrepaid.to == null ||
              c.remaining_amount <= filters.remainingPrepaid.to)
        );
      }
      // 방문 횟수
      if (
        filters.visitCount &&
        (filters.visitCount.from != null || filters.visitCount.to != null)
      ) {
        checkFunctions.push(
          c =>
            (filters.visitCount.from == null || c.visit_count >= filters.visitCount.from) &&
            (filters.visitCount.to == null || c.visit_count <= filters.visitCount.to)
        );
      }
      // 노쇼 횟수
      if (
        filters.noShowCount &&
        (filters.noShowCount.from != null || filters.noShowCount.to != null)
      ) {
        checkFunctions.push(
          c =>
            (filters.noShowCount.from == null || c.noshow_count >= filters.noShowCount.from) &&
            (filters.noShowCount.to == null || c.noshow_count <= filters.noShowCount.to)
        );
      }
      // 최초 등록일
      if (filters.registrationDate?.mode && filters.registrationDate.mode !== 'none') {
        checkFunctions.push(c => {
          if (!c.created_at) return false;
          const createdAt = new Date(c.created_at);
          createdAt.setHours(0, 0, 0, 0);
          const mode = filters.registrationDate.mode;
          const date1 = filters.registrationDate.date1
            ? new Date(filters.registrationDate.date1)
            : null;
          if (date1) date1.setHours(0, 0, 0, 0);
          const date2 = filters.registrationDate.date2
            ? new Date(filters.registrationDate.date2)
            : null;
          if (date2) date2.setHours(23, 59, 59, 999);
          const days = filters.registrationDate.days;

          if (mode === 'before' && date1) return createdAt < date1;
          if (mode === 'after' && date1) return createdAt > date1;
          if (mode === 'range' && date1 && date2) return createdAt >= date1 && createdAt <= date2;
          if (mode === 'within_days' && days !== null) {
            const daysAgo = new Date();
            daysAgo.setDate(daysAgo.getDate() - days);
            daysAgo.setHours(0, 0, 0, 0);
            return createdAt >= daysAgo;
          }
          if (mode === 'days_ago' && days !== null) {
            const daysAgo = new Date();
            daysAgo.setDate(daysAgo.getDate() - days);
            daysAgo.setHours(23, 59, 59, 999);
            return createdAt <= daysAgo;
          }
          return false;
        });
      }
      // 최근 방문일
      if (filters.recentVisitDate?.mode && filters.recentVisitDate.mode !== 'none') {
        checkFunctions.push(c => {
          if (!c.recent_visit_date) return false;
          const recentVisitDate = new Date(c.recent_visit_date);
          recentVisitDate.setHours(0, 0, 0, 0);
          const mode = filters.recentVisitDate.mode;
          const date1 = filters.recentVisitDate.date1
            ? new Date(filters.recentVisitDate.date1)
            : null;
          if (date1) date1.setHours(0, 0, 0, 0);
          const date2 = filters.recentVisitDate.date2
            ? new Date(filters.recentVisitDate.date2)
            : null;
          if (date2) date2.setHours(23, 59, 59, 999);
          const days = filters.recentVisitDate.days;

          if (mode === 'before' && date1) return recentVisitDate < date1;
          if (mode === 'after' && date1) return recentVisitDate > date1;
          if (mode === 'range' && date1 && date2)
            return recentVisitDate >= date1 && recentVisitDate <= date2;
          if (mode === 'within_days' && days !== null) {
            const daysAgo = new Date();
            daysAgo.setDate(daysAgo.getDate() - days);
            daysAgo.setHours(0, 0, 0, 0);
            return recentVisitDate >= daysAgo;
          }
          if (mode === 'days_ago' && days !== null) {
            const daysAgo = new Date();
            daysAgo.setDate(daysAgo.getDate() - days);
            daysAgo.setHours(23, 59, 59, 999);
            return recentVisitDate <= daysAgo;
          }
          return false;
        });
      }

      if (checkFunctions.length > 0) {
        data = data.filter(customer => {
          if (searchType === 'and') {
            return checkFunctions.every(check => check(customer));
          } else {
            return checkFunctions.some(check => check(customer));
          }
        });
      }
    }

    // 정렬 로직은 필터링 후에 적용
    if (sortableKeys.includes(sortKey.value)) {
      data.sort((a, b) => {
        let aValue = a[sortKey.value];
        let bValue = b[sortKey.value];
        if (sortKey.value === 'recent_visit_date') {
          return sortOrder.value === 'asc'
            ? (aValue || '').localeCompare(bValue || '')
            : (bValue || '').localeCompare(aValue || '');
        }
        if (typeof aValue === 'number') {
          return sortOrder.value === 'asc' ? aValue - bValue : bValue - aValue;
        }
        return sortOrder.value === 'asc'
          ? String(aValue).localeCompare(String(bValue))
          : String(bValue).localeCompare(String(aValue));
      });
    } else {
      data.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
    }

    return data;
  });

  const total = computed(() => filteredData.value.length);
  const totalPages = computed(() => Math.ceil(total.value / pageSize.value));
  const pagedData = computed(() => {
    const start = (page.value - 1) * pageSize.value;
    return filteredData.value.slice(start, start + pageSize.value);
  });

  const showDropdown = ref(false);
  const checkboxDropdownRef = ref(null);

  const isAllSelected = computed(() => {
    if (filteredData.value.length === 0) return false;
    return selectedIds.value.length === filteredData.value.length;
  });
  const isPageSelected = computed(() => {
    const pageIds = pagedData.value.map(item => item.customer_id);
    if (pageIds.length === 0) return false;
    return pageIds.every(id => selectedIds.value.includes(id)) && !isAllSelected.value;
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
      selectedIds.value = filteredData.value.map(item => item.customer_id);
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
    loadCustomers();
    loadMetaData();
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

  const handleApplyFilters = filters => {
    activeFilters.value = filters;
    page.value = 1; // 필터 적용 시 1페이지로 리셋
    toastRef.value?.success('필터가 적용되었습니다.');
  };

  // 유입채널 정보는 필요 시 customers 데이터를 통해 사용합니다.

  async function handleCreateCustomer(newCustomer) {
    try {
      const shopId = authStore.shopId?.value || authStore.shopId || 1;
      await customersAPI.createCustomer({ ...newCustomer, shopId });
      toastRef.value?.success('고객 등록 완료', { duration: 2000 });
      await loadCustomers();
    } catch (err) {
      toastRef.value?.success(err.message || '고객 등록 실패');
    }
  }

  const activeFilterPills = computed(() => {
    const pills = [];
    const filters = activeFilters.value;

    const addPill = (key, label, valueText, value) => {
      if (valueText !== null && valueText !== undefined && valueText !== '') {
        const idValue = typeof value === 'object' && value !== null ? JSON.stringify(value) : value;
        pills.push({ id: `${key}_${idValue}`, key, value, label, valueText });
      }
    };

    if (filters.gender?.length > 0) {
      addPill('gender', '성별', filters.gender.join(', '), filters.gender);
    }
    if (filters.tags?.length > 0) {
      const tagNames = filters.tags.map(
        id => tags.value.find(t => t.tag_id === id)?.tag_name || `ID:${id}`
      );
      addPill('tags', '태그', tagNames.join(', '), filters.tags);
    }
    if (filters.staff?.length > 0) {
      const staffNames = filters.staff.map(
        id => staff.value.find(s => s.id === id)?.name || `ID:${id}`
      );
      addPill('staff', '담당자', staffNames.join(', '), filters.staff);
    }
    if (filters.grades?.length > 0) {
      const gradeNames = filters.grades.map(
        id => grades.value.find(g => g.id === id)?.name || `ID:${id}`
      );
      addPill('grades', '등급', gradeNames.join(', '), filters.grades);
    }
    if (filters.birthday?.length > 0) {
      const birthdayMap = { today: '오늘', this_week: '이번주', this_month: '이번달' };
      const birthdayTexts = filters.birthday.map(b => birthdayMap[b] || b);
      addPill('birthday', '생일', birthdayTexts.join(', '), filters.birthday);
    }
    if (filters.birthYearStart || filters.birthYearEnd) {
      let text = '';
      if (filters.birthYearStart && filters.birthYearEnd) {
        text = `${filters.birthYearStart}년 ~ ${filters.birthYearEnd}년`;
      } else if (filters.birthYearStart) {
        text = `${filters.birthYearStart}년 이후`;
      } else if (filters.birthYearEnd) {
        text = `${filters.birthYearEnd}년 이전`;
      }
      addPill('birthYear', '출생연도', text, {
        start: filters.birthYearStart,
        end: filters.birthYearEnd,
      });
    }
    if (filters.prepaidHistory?.length > 0) {
      const textMap = { yes: '있음', no: '없음' };
      const texts = filters.prepaidHistory.map(h => textMap[h] || h);
      addPill('prepaidHistory', '선불액 구매내역', texts.join(', '), filters.prepaidHistory);
    }
    if (filters.sessionPassHistory?.length > 0) {
      const textMap = { yes: '있음', no: '없음' };
      const texts = filters.sessionPassHistory.map(h => textMap[h] || h);
      addPill(
        'sessionPassHistory',
        '횟수권 구매내역',
        texts.join(', '),
        filters.sessionPassHistory
      );
    }
    if (filters.usablePrepaid?.length > 0) {
      const textMap = { yes: '있음', no: '없음' };
      const texts = filters.usablePrepaid.map(u => textMap[u] || u);
      addPill('usablePrepaid', '사용가능 선불액', texts.join(', '), filters.usablePrepaid);
    }
    if (filters.usableSessionPass?.length > 0) {
      const textMap = { yes: '있음', no: '없음' };
      const texts = filters.usableSessionPass.map(u => textMap[u] || u);
      addPill('usableSessionPass', '사용가능 횟수권', texts.join(', '), filters.usableSessionPass);
    }
    if (
      filters.totalRevenue &&
      (filters.totalRevenue.from != null || filters.totalRevenue.to != null)
    ) {
      let text = '';
      if (filters.totalRevenue.from != null && filters.totalRevenue.to != null) {
        text = `${filters.totalRevenue.from.toLocaleString()}원 ~ ${filters.totalRevenue.to.toLocaleString()}원`;
      } else if (filters.totalRevenue.from != null) {
        text = `${filters.totalRevenue.from.toLocaleString()}원 이상`;
      } else if (filters.totalRevenue.to != null) {
        text = `${filters.totalRevenue.to.toLocaleString()}원 이하`;
      }
      addPill('totalRevenue', '누적 매출액', text, filters.totalRevenue);
    }
    if (
      filters.remainingPrepaid &&
      (filters.remainingPrepaid.from != null || filters.remainingPrepaid.to != null)
    ) {
      let text = '';
      if (filters.remainingPrepaid.from != null && filters.remainingPrepaid.to != null) {
        text = `${filters.remainingPrepaid.from.toLocaleString()}원 ~ ${filters.remainingPrepaid.to.toLocaleString()}원`;
      } else if (filters.remainingPrepaid.from != null) {
        text = `${filters.remainingPrepaid.from.toLocaleString()}원 이상`;
      } else if (filters.remainingPrepaid.to != null) {
        text = `${filters.remainingPrepaid.to.toLocaleString()}원 이하`;
      }
      addPill('remainingPrepaid', '잔여 선불액', text, filters.remainingPrepaid);
    }
    if (filters.visitCount && (filters.visitCount.from != null || filters.visitCount.to != null)) {
      let text = '';
      if (filters.visitCount.from != null && filters.visitCount.to != null) {
        text = `${filters.visitCount.from}회 ~ ${filters.visitCount.to}회`;
      } else if (filters.visitCount.from != null) {
        text = `${filters.visitCount.from}회 이상`;
      } else if (filters.visitCount.to != null) {
        text = `${filters.visitCount.to}회 이하`;
      }
      addPill('visitCount', '방문 횟수', text, filters.visitCount);
    }
    if (
      filters.noShowCount &&
      (filters.noShowCount.from != null || filters.noShowCount.to != null)
    ) {
      let text = '';
      if (filters.noShowCount.from != null && filters.noShowCount.to != null) {
        text = `${filters.noShowCount.from}회 ~ ${filters.noShowCount.to}회`;
      } else if (filters.noShowCount.from != null) {
        text = `${filters.noShowCount.from}회 이상`;
      } else if (filters.noShowCount.to != null) {
        text = `${filters.noShowCount.to}회 이하`;
      }
      addPill('noShowCount', '노쇼 횟수', text, filters.noShowCount);
    }

    const formatDateForDisplay = dateStr => {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
      });
    };
    const getDateFilterText = filterObj => {
      const mode = filterObj.mode;
      const date1 = filterObj.date1;
      const date2 = filterObj.date2;
      const days = filterObj.days;

      if (mode === 'before' && date1) return `${formatDateForDisplay(date1)} 이전`;
      if (mode === 'after' && date1) return `${formatDateForDisplay(date1)} 이후`;
      if (mode === 'range' && date1 && date2)
        return `${formatDateForDisplay(date1)} ~ ${formatDateForDisplay(date2)}`;
      if (mode === 'within_days' && days !== null) return `최근 ${days}일 이내`;
      if (mode === 'days_ago' && days !== null) return `${days}일 이상 경과`;
      return '';
    };

    if (filters.registrationDate?.mode && filters.registrationDate.mode !== 'none') {
      const text = getDateFilterText(filters.registrationDate);
      addPill('registrationDate', '최초 등록일', text, filters.registrationDate);
    }
    if (filters.recentVisitDate?.mode && filters.recentVisitDate.mode !== 'none') {
      const text = getDateFilterText(filters.recentVisitDate);
      addPill('recentVisitDate', '최근 방문일', text, filters.recentVisitDate);
    }

    return pills;
  });

  function removeFilter(pillToRemove) {
    const { key } = pillToRemove;
    const currentFilters = { ...activeFilters.value };

    if (key in currentFilters) {
      delete currentFilters[key];
    }

    activeFilters.value = currentFilters;
    page.value = 1;
  }

  /**
   * 서버에서 고객 목록을 불러온다.
   * TODO: shopId를 실제 로그인 정보에서 가져오도록 수정
   */
  async function loadCustomers() {
    loading.value = true;
    try {
      const shopId = authStore.shopId?.value || authStore.shopId || 1;
      const data = await customersAPI.getCustomersByShop(shopId);
      customerList.value = data;
    } catch (err) {
      toastRef.value?.success(err.message || '고객 데이터를 불러오는데 실패했습니다.');
    } finally {
      loading.value = false;
    }
  }

  async function loadMetaData() {
    const shopId = authStoreMeta.shopId?.value || authStoreMeta.shopId || 1;

    // 태그
    try {
      tags.value = await tagsAPI.getTagsByShop(shopId);
    } catch (e) {
      tags.value = [];
    }

    // 등급
    try {
      grades.value = await gradesAPI.getGradesByShop(shopId);
    } catch (e) {
      grades.value = [];
    }

    // 직원
    try {
      const res = await getStaff({ page: 1, size: 1000, isActive: true });
      const list = res.data?.data?.staffList || [];
      staff.value = list.map(s => ({ id: s.staffId || s.id, name: s.staffName || s.name }));
    } catch (e) {
      staff.value = [];
    }
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
    left: 0;
    width: 100%;
    box-sizing: border-box;
    margin-top: 8px;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 100;
    padding: 0.5rem 0;
  }
  .dropdown-item {
    padding: 0.75rem 1rem;
    cursor: pointer;
    font-size: 0.9rem;
    color: #333;
    text-align: center;
  }
  .dropdown-item:hover {
    background-color: #f5f5f5;
  }
  .customer-list-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
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
  .applied-filters-container {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 18px;
    flex-wrap: wrap;
  }
  .filter-pills-wrapper {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    flex-grow: 1;
  }
  .filter-pill-base-badge {
    display: inline-flex;
    align-items: center;
    font-size: 14px;
    font-weight: 500;
  }
  .remove-pill-btn {
    margin-left: 8px;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    color: inherit;
    opacity: 0.7;
  }
  .remove-pill-btn:hover {
    opacity: 1;
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
  .table-scroll-wrapper {
    width: 100%;
    overflow-x: auto;
  }
  .wide-table :deep(th),
  .wide-table :deep(td) {
    padding: 12px 16px;
    vertical-align: middle;
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
    padding: 0;
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
