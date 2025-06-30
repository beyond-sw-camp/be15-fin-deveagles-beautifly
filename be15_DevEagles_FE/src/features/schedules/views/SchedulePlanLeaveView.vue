<template>
  <div class="schedule-wrapper">
    <div class="page-header">
      <h1 class="font-screen-title">{{ currentTabTitle }}</h1>
    </div>

    <!-- 탭 메뉴 -->
    <div class="tab-bar">
      <BaseButton
        v-for="tab in tabs"
        :key="tab.value"
        type="primary"
        :outline="true"
        class="base-button"
        :class="{ active: tab.value === currentTab }"
        @click="currentTab = tab.value"
      >
        {{ tab.label }}
      </BaseButton>
    </div>

    <!-- 상단 버튼 + 필터 바 -->
    <div class="top-bar-flex">
      <div class="left-bar">
        <BaseButton type="error" :disabled="selectedIds.length === 0" @click="deleteSelected">
          {{ currentTab === 'plan' ? '일정 삭제' : '휴무 삭제' }}
        </BaseButton>
      </div>

      <div class="right-bar">
        <BaseForm
          v-model="selectedType"
          type="select"
          :options="typeOptions"
          style="width: 160px"
        />
        <BaseForm
          v-model="selectedStaff"
          type="select"
          :options="staffOptions"
          style="width: 160px"
        />
        <PrimeDatePicker
          v-model="selectedDateRange"
          selection-mode="range"
          :show-button-bar="true"
          :clearable="true"
          :show-icon="true"
          style="width: 280px"
        />
        <BaseButton type="primary" class="fix-button-height" @click="isRegistModalOpen = true">
          {{ currentTab === 'plan' ? '일정 등록' : '휴무 등록' }}
        </BaseButton>
      </div>
    </div>

    <!-- 테이블 -->
    <div class="base-table-wrapper">
      <BaseTable
        :columns="columns"
        :data="pagedItems"
        row-key="id"
        :striped="true"
        :hover="true"
        @row-click="handleRowClick"
      >
        <template #header-select>
          <input type="checkbox" :checked="allSelected" @change="toggleAll" />
        </template>
        <template #cell-select="{ item }">
          <input v-model="selectedIds" type="checkbox" :value="item.id" @click.stop />
        </template>
        <template v-if="currentTab === 'leave'" #cell-date="{ value }">
          <div class="text-left">{{ formatDate(value) }}</div>
        </template>
      </BaseTable>
    </div>

    <BasePagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="filteredItems.length"
      :items-per-page="itemsPerPage"
      @page-change="page => (currentPage = page)"
    />

    <!-- 상세 모달 -->
    <component
      :is="currentTab === 'plan' ? PlanDetailModal : LeaveDetailModal"
      v-if="isModalOpen"
      :model-value="isModalOpen"
      :reservation="selectedItem"
      @update:model-value="closeModal"
    />

    <!-- 등록 모달 -->
    <ScheduleRegistModal
      v-if="isRegistModalOpen"
      v-model="isRegistModalOpen"
      :default-tab="currentTab"
    />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BasePagination from '@/components/common/Pagination.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import PlanDetailModal from '@/features/schedules/components/PlanDetailModal.vue';
  import LeaveDetailModal from '@/features/schedules/components/LeaveDetailModal.vue';
  import ScheduleRegistModal from '@/features/schedules/components/ScheduleRegistModal.vue';

  const tabs = [
    { label: '일정', value: 'plan' },
    { label: '휴무', value: 'leave' },
  ];
  const currentTab = ref('plan');
  const currentPage = ref(1);
  const itemsPerPage = 10;
  const selectedType = ref('');
  const selectedStaff = ref('');
  const selectedIds = ref([]);
  const selectedDateRange = ref([]);
  const isModalOpen = ref(false);
  const isRegistModalOpen = ref(false);
  const selectedItem = ref(null);

  const staffOptions = [
    { text: '담당자', value: '' },
    { text: '김이글', value: '김이글' },
    { text: '박이글', value: '박이글' },
  ];

  const typeOptions = computed(() =>
    currentTab.value === 'plan'
      ? [
          { text: '일정 종류', value: '' },
          { text: '단기', value: 'plan' },
          { text: '정기', value: 'regular_plan' },
        ]
      : [
          { text: '휴무 종류', value: '' },
          { text: '단기', value: 'leave' },
          { text: '정기', value: 'regular_leave' },
        ]
  );

  const planData = ref([]);
  const mondays = ['2025-06-02', '2025-06-09', '2025-06-16', '2025-06-23', '2025-06-30'];

  for (let i = 1; i <= 15; i++) {
    const isRegular = i % 3 !== 0;
    const type = isRegular ? 'regular_plan' : 'plan';
    const period = isRegular ? '정기' : '단기';
    const staff = i % 2 === 0 ? '김이글' : '박이글';
    const title = isRegular ? '워크숍' : '세미나';
    const repeatDescription = isRegular
      ? i % 2 === 0
        ? '매달 2일 반복'
        : '매주 월요일 반복'
      : '반복 안함';
    const date =
      isRegular && repeatDescription.includes('월요일') ? mondays[Math.floor(i / 3)] : '2025-06-02';
    const displayDate = isRegular ? date : `2025-06-${String(i).padStart(2, '0')}`;

    planData.value.push({
      id: i,
      staff,
      title,
      type,
      period,
      date: displayDate,
      timeRange: '오후 01:00 - 오후 03:00',
      startTime: '오후 01:00',
      endTime: '오후 03:00',
      start: `${displayDate} 오후 01:00`,
      end: `${displayDate} 오후 03:00`,
      duration: '02:00',
      repeatDescription,
      memo: `${staff} 일정 ${title}`,
    });
  }
  planData.value.sort((a, b) => {
    if (a.type !== b.type) return a.type === 'regular_plan' ? -1 : 1;
    return new Date(a.date) - new Date(b.date);
  });

  const leaveData = ref([]);
  let mondayIndex = 0;

  for (let i = 1; i <= 15; i++) {
    const isRegular = i % 3 === 0;
    const type = isRegular ? 'regular_leave' : 'leave';
    const title = isRegular ? '정기 휴무' : '휴무';
    const staff = i % 2 === 0 ? '김이글' : '박이글';

    let repeatDescription = '반복 안함';
    let displayDate = `2025-06-${String(i).padStart(2, '0')}`;

    if (isRegular) {
      if (i % 2 === 0) {
        repeatDescription = '매달 2일 반복';
        displayDate = '2025-06-02';
      } else {
        repeatDescription = '매주 월요일 반복';
        displayDate = mondays[mondayIndex % mondays.length];
        mondayIndex++;
      }
    }

    leaveData.value.push({
      id: i,
      staff,
      title,
      type,
      start: displayDate,
      date: displayDate,
      repeatDescription,
    });
  }
  leaveData.value.sort((a, b) => {
    if (a.type !== b.type) return a.type === 'regular_leave' ? -1 : 1;
    return new Date(a.date) - new Date(b.date);
  });

  const dataSource = computed(() =>
    currentTab.value === 'plan' ? planData.value : leaveData.value
  );

  const filteredItems = computed(() => {
    const [fromDate, toDate] =
      Array.isArray(selectedDateRange.value) && selectedDateRange.value.length === 2
        ? selectedDateRange.value
        : [null, null];

    return dataSource.value
      .map(item => {
        if (currentTab.value === 'leave') {
          return {
            ...item,
            typeLabel: item.type === 'regular_leave' ? '정기' : '단기',
          };
        }
        return item;
      })
      .filter(item => {
        const itemDate = new Date(item.date + ' 00:00:00');
        const isRegular = item.type === 'regular_plan' || item.type === 'regular_leave';

        const matchesDateRange =
          fromDate && toDate
            ? itemDate >= new Date(fromDate) && itemDate <= endOfDay(toDate)
            : true;

        const matchesType = !selectedType.value || item.type === selectedType.value;
        const matchesStaff = !selectedStaff.value || item.staff === selectedStaff.value;

        return isRegular
          ? matchesType && matchesStaff
          : matchesDateRange && matchesType && matchesStaff;
      });
  });

  function endOfDay(date) {
    const d = new Date(date);
    d.setHours(23, 59, 59, 999);
    return d;
  }

  const pagedItems = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return filteredItems.value.slice(start, start + itemsPerPage);
  });

  const totalPages = computed(() => Math.ceil(filteredItems.value.length / itemsPerPage));

  const allSelected = computed(
    () =>
      pagedItems.value.length > 0 &&
      pagedItems.value.every(item => selectedIds.value.includes(item.id))
  );

  const toggleAll = e => {
    if (e.target.checked) {
      selectedIds.value = [...new Set([...selectedIds.value, ...pagedItems.value.map(i => i.id)])];
    } else {
      selectedIds.value = selectedIds.value.filter(id => !pagedItems.value.some(i => i.id === id));
    }
  };

  const deleteSelected = () => {
    if (currentTab.value === 'plan') {
      planData.value = planData.value.filter(item => !selectedIds.value.includes(item.id));
    } else {
      leaveData.value = leaveData.value.filter(item => !selectedIds.value.includes(item.id));
    }
    selectedIds.value = [];
  };

  const handleRowClick = (row, event) => {
    if (event?.target?.type === 'checkbox') return;
    selectedItem.value = row;
    isModalOpen.value = true;
  };

  const closeModal = () => {
    isModalOpen.value = false;
    selectedItem.value = null;
  };

  const formatDate = dateStr => {
    const date = new Date(dateStr);
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const day = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
    return `${yyyy}.${mm}.${dd} (${day})`;
  };

  const currentTabTitle = computed(() => (currentTab.value === 'plan' ? '일정 목록' : '휴무 목록'));

  const columns = computed(() =>
    currentTab.value === 'plan'
      ? [
          { key: 'select', title: '', width: '40px' },
          { key: 'staff', title: '직원 이름', width: '120px' },
          { key: 'title', title: '일정 제목', width: '160px' },
          { key: 'period', title: '정기/단기', width: '120px' },
          { key: 'repeatDescription', title: '반복 설정', width: '180px' },
          { key: 'start', title: '시작 날짜', width: '160px' },
          { key: 'end', title: '마감 날짜', width: '160px' },
        ]
      : [
          { key: 'select', title: '', width: '40px' },
          { key: 'staff', title: '직원 이름', width: '120px' },
          { key: 'title', title: '휴무 제목', width: '160px' },
          { key: 'typeLabel', title: '정기/단기', width: '120px' },
          { key: 'repeatDescription', title: '반복 설정', width: '180px' },
          { key: 'date', title: '날짜', width: '180px' },
        ]
  );
</script>

<style scoped>
  .btn-outline.btn-primary {
    background: transparent;
    border: 1px solid var(--color-primary-main);
    color: var(--color-primary-main);
  }
  .btn-outline.btn-primary:hover {
    background: var(--color-primary-50);
  }
  .btn-outline.btn-primary:active {
    background: var(--color-primary-100);
  }
  .schedule-wrapper {
    padding: 24px;
  }
  .page-header {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .top-bar-flex {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 16px;
  }
  .left-bar {
    flex-shrink: 0;
  }
  .tab-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
  }
  .right-bar {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    flex-wrap: wrap;
  }
  .base-table-wrapper {
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 24px;
  }
  .fix-button-height {
    height: 40px;
    padding: 0 16px !important;
    font-size: 14px;
    line-height: 1 !important;
  }
  .tab-bar :deep(.base-button.active) {
    background-color: var(--color-primary-main);
    color: white;
    border-color: var(--color-primary-main);
  }
</style>
