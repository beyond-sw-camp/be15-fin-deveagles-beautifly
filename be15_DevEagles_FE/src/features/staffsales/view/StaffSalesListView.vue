<template>
  <div class="staff-settlement-page">
    <h2 class="page-title">직원 결산</h2>

    <!-- 탭 + 설정 버튼 -->
    <div class="tab-row">
      <div class="tab-list">
        <BaseTab v-model="activeTab" :tabs="tabs" />
      </div>
      <div class="tab-action-button">
        <BaseButton v-if="activeTab !== '목표매출'" @click="openIncentivePopup">
          직원별 인센티브 설정
        </BaseButton>
        <BaseButton v-else @click="openGoalPopup"> 목표매출 설정 </BaseButton>
      </div>
    </div>

    <!-- 조회 필터 -->
    <div class="filter-row">
      <div class="radio-wrapper">
        <BaseForm
          v-model="searchMode"
          type="radio"
          :options="[
            { text: '월별 조회', value: 'month' },
            { text: '기간별 조회', value: 'range' },
          ]"
        />
      </div>
      <div class="date-picker">
        <PrimeDatePicker
          v-if="searchMode === 'month'"
          v-model="selectedMonth"
          view="month"
          selection-mode="single"
          placeholder="월 선택"
        />
        <PrimeDatePicker
          v-else
          v-model="selectedRange"
          view="date"
          selection-mode="range"
          placeholder="기간 선택"
        />
      </div>
    </div>

    <!-- 로딩 -->
    <BaseLoading v-if="loading" text="정산 내역을 불러오는 중입니다..." :overlay="true" />

    <!-- 테이블 -->
    <div v-if="!loading" class="table-wrapper">
      <BaseTable
        :columns="columns"
        :data="settlementData"
        :row-class="getRowClass"
        :scroll="{ y: '600px' }"
        :pagination="false"
      >
        <template #cell-card="{ value }">
          {{ formatCurrency(value) }}
        </template>
        <template #cell-sales="{ value }">
          {{ formatCurrency(value) }}
        </template>
        <template #cell-prepaid="{ value }">
          {{ formatCurrency(value) }}
        </template>
        <template #cell-discount="{ value }">
          {{ formatCurrency(value) }}
        </template>
        <template #cell-total="{ value }">
          {{ formatCurrency(value) }}
        </template>
      </BaseTable>
    </div>
  </div>
  <Teleport to="body">
    <BaseModal v-model="showIncentiveModal" :title="'인센티브 설정'">
      <IncentiveSettingModal />
    </BaseModal>
  </Teleport>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseLoading from '@/components/common/BaseLoading.vue';
  import BaseTab from '@/components/common/BaseTab.vue';
  import IncentiveSettingModal from '@/features/staffsales/components/IncentiveSettingModal.vue';
  import BaseModal from '@/components/common/BaseModal.vue';

  const activeTab = ref('직원별 결산');
  const tabs = ['직원별 결산', '직원별 상세결산', '목표매출'];
  const searchMode = ref('month');
  const selectedMonth = ref(new Date());
  const selectedRange = ref([]);
  const showIncentiveModal = ref(false);
  const loading = ref(false);

  const columns = [
    { title: '직원 이름', key: 'name' },
    { title: '분류', key: 'category' },
    { title: '카드', key: 'card' },
    { title: '실매출액', key: 'sales' },
    { title: '선불액', key: 'prepaid' },
    { title: '할인', key: 'discount' },
    { title: '총영업액', key: 'total' },
  ];

  const settlementData = ref([
    {
      name: '홍길동',
      category: '피부',
      card: 162940,
      sales: 338760,
      prepaid: 36890,
      discount: 49682,
      total: 325968,
    },
    {
      name: '이수민',
      category: '피부',
      card: 246496,
      sales: 330970,
      prepaid: 90060,
      discount: 11440,
      total: 409590,
    },
    {
      name: '박민준',
      category: '속눈썹',
      card: 245027,
      sales: 328569,
      prepaid: 49427,
      discount: 35512,
      total: 342484,
    },
    {
      name: '김지연',
      category: '피부',
      card: 156085,
      sales: 496684,
      prepaid: 37943,
      discount: 21797,
      total: 512830,
    },
    {
      name: '최윤호',
      category: '속눈썹',
      card: 202084,
      sales: 366722,
      prepaid: 58802,
      discount: 44996,
      total: 380528,
    },
    {
      name: '정하늘',
      category: '피부',
      card: 194160,
      sales: 389013,
      prepaid: 96540,
      discount: 17137,
      total: 468416,
    },
    {
      name: '서다인',
      category: '네일',
      card: 184191,
      sales: 346170,
      prepaid: 89899,
      discount: 29513,
      total: 406556,
    },
    {
      name: '한도윤',
      category: '네일',
      card: 134693,
      sales: 362885,
      prepaid: 38576,
      discount: 36363,
      total: 364098,
    },
    {
      name: '이해진',
      category: '속눈썹',
      card: 209923,
      sales: 326128,
      prepaid: 37000,
      discount: 10385,
      total: 352743,
    },
    {
      name: '장하림',
      category: '속눈썹',
      card: 135054,
      sales: 423842,
      prepaid: 77727,
      discount: 18420,
      total: 483149,
    },
    {
      name: '총계',
      category: '',
      card: 1871653,
      sales: 3700743,
      prepaid: 663364,
      discount: 265245,
      total: 4098862,
    },
  ]);

  function openIncentivePopup() {
    showIncentiveModal.value = true;
  }

  function openGoalPopup() {
    // 목표매출 설정 팝업 오픈
  }

  function getRowClass(row) {
    return row.name === '총계' ? 'summary-row' : '';
  }

  function formatCurrency(value) {
    return value?.toLocaleString('ko-KR') ?? '-';
  }
</script>

<style scoped>
  .page-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 16px;
  }
  .tab-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
  .filter-row {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;
  }
  .table-wrapper {
    overflow-y: auto;
    max-height: 600px;
  }
  .radio-wrapper {
    display: flex;
    align-items: center;
    height: 40px;
    font-size: 13px;
  }
  .radio-wrapper label {
    font-size: 13px;
    margin: 0;
  }
  .radio-wrapper :deep(.form-group) {
    display: flex;
    align-items: center;
    gap: 16px;
    margin: 0;
    padding: 0;
  }
  .radio-wrapper :deep(.radio-group) {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 16px;
    min-height: 100%;
  }
  .radio-wrapper :deep(.radio-group .radio) {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  .radio-wrapper :deep(.radio-group label),
  .radio-wrapper :deep(.radio-group input[type='radio']) {
    font-size: 13px;
  }
  :deep(.summary-row) {
    font-weight: bold;
    background-color: #f4f4f4;
  }
</style>
