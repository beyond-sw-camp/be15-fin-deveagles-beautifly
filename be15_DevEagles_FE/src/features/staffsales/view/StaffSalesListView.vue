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
        <BaseButton v-else @click="openTargetPopup"> 목표매출 설정 </BaseButton>
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
        :data="currentData"
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
        <template #cell-target="{ value }">
          {{ formatCurrency(value) }}
        </template>
        <template #cell-totalSales="{ value }">
          {{ formatCurrency(value) }}
        </template>
        <template #cell-rate="{ value }"> {{ value }}% </template>
      </BaseTable>
    </div>
  </div>
  <BaseSlidePanel
    v-if="showIncentiveModal"
    :title="'인센티브 설정'"
    @close="showIncentiveModal = false"
  >
    <IncentiveSettingModal ref="modalRef" />
    <template #footer>
      <div class="footer-btn-row">
        <BaseButton type="primary" @click="modalRef?.handleSave?.()">저장하기</BaseButton>
      </div>
    </template>
  </BaseSlidePanel>

  <BaseSlidePanel
    v-if="showTargetSalesModal"
    title="목표매출 설정"
    @close="showTargetSalesModal = false"
  >
    <TargetSalesSettingModal ref="targetModalRef" />
    <template #footer>
      <div class="footer-btn-row">
        <BaseButton type="primary" @click="targetModalRef?.handleSubmit?.()">저장하기</BaseButton>
      </div>
    </template>
  </BaseSlidePanel>
</template>

<script setup>
  import { computed, ref } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseLoading from '@/components/common/BaseLoading.vue';
  import BaseTab from '@/components/common/BaseTab.vue';
  import IncentiveSettingModal from '@/features/staffsales/components/IncentiveSettingModal.vue';
  import BaseSlidePanel from '@/features/staffsales/components/BaseSlidePanel.vue';
  import TargetSalesSettingModal from '@/features/staffsales/components/TargetSalesSettingModal.vue';

  const activeTab = ref('직원별 결산');
  const tabs = ['직원별 결산', '직원별 상세결산', '목표매출'];
  const searchMode = ref('month');
  const selectedMonth = ref(new Date());
  const selectedRange = ref([]);
  const showIncentiveModal = ref(false);
  const showTargetSalesModal = ref(false);
  const loading = ref(false);

  const baseColumns = [
    { title: '직원 이름', key: 'name' },
    { title: '분류', key: 'category' },
    { title: '카드', key: 'card' },
    { title: '실매출액', key: 'sales' },
    { title: '선불액', key: 'prepaid' },
    { title: '할인', key: 'discount' },
    { title: '총영업액', key: 'total' },
  ];

  const targetColumns = [
    { title: '직원 이름', key: 'name' },
    { title: '분류', key: 'category' },
    { title: '목표', key: 'target' },
    { title: '총영업액', key: 'totalSales' },
    { title: '달성률', key: 'rate' },
  ];

  const columns = computed(() => {
    if (activeTab.value === '목표매출') return targetColumns;
    return baseColumns;
  });

  const baseSettlementData = ref([
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
      name: '',
      category: '총계',
      card: 1871653,
      sales: 3700743,
      prepaid: 663364,
      discount: 265245,
      total: 4098862,
    },
  ]);

  const detailedSettlementData = ref([
    {
      name: '김이글',
      items: [
        {
          category: '상품-시술',
          card: 0,
          sales: 0,
          prepaid: 0,
          discount: 0,
          total: 0,
        },
        {
          category: '총계',
          card: 0,
          sales: 0,
          prepaid: 0,
          discount: 0,
          total: 0,
        },
      ],
    },
    {
      name: '한위니',
      items: [
        {
          category: '상품-선불권',
          card: 0,
          sales: 0,
          prepaid: 0,
          discount: 0,
          total: 0,
        },
        {
          category: '총계',
          card: 0,
          sales: 0,
          prepaid: 0,
          discount: 0,
          total: 0,
        },
      ],
    },
    {
      name: '',
      category: '총계',
      card: 0,
      sales: 0,
      prepaid: 0,
      discount: 0,
      total: 0,
    },
  ]);

  const targetSalesData = ref([
    {
      name: '김이글',
      items: [
        {
          category: '상품-시술 판매',
          target: 100000,
          totalSales: 10000,
          rate: 0,
        },
        {
          category: '상품-선불액 판매',
          target: 100000,
          totalSales: 10000,
          rate: 0,
        },
        {
          category: '상품-제품 판매',
          target: 100000,
          totalSales: 10000,
          rate: 0,
        },
        {
          category: '총계',
          target: 300000,
          totalSales: 30000,
          rate: 0,
        },
      ],
    },
    {
      name: '한위니',
      items: [
        {
          category: '상품-시술 판매',
          target: 100000,
          totalSales: 10000,
          rate: 0,
        },
        {
          category: '상품-선불액 판매',
          target: 200000,
          totalSales: 10000,
          rate: 0,
        },
        {
          category: '상품-제품 판매',
          target: 200000,
          totalSales: 10000,
          rate: 0,
        },
        {
          category: '총계',
          target: 300000,
          totalSales: 30000,
          rate: 0,
        },
      ],
    },
    {
      name: '',
      items: [
        {
          category: '총계',
          target: 600000,
          totalSales: 60000,
          rate: 0,
        },
      ],
    },
  ]);

  const currentData = computed(() => {
    if (activeTab.value === '직원별 상세결산') {
      return flattenDetailData(detailedSettlementData.value);
    } else if (activeTab.value === '목표매출') {
      return flattenTargetSales(targetSalesData.value);
    }
    return baseSettlementData.value;
  });

  function flattenDetailData(data) {
    const result = [];

    data.forEach(staff => {
      if (staff.items) {
        staff.items.forEach(item => {
          result.push({
            name: item.category === '총계' ? '' : staff.name,
            category: item.category,
            card: item.card,
            sales: item.sales,
            prepaid: item.prepaid,
            discount: item.discount,
            total: item.total,
          });
        });
      } else {
        result.push(staff);
      }
    });

    return result;
  }

  function flattenTargetSales(data) {
    const result = [];

    data.forEach(staff => {
      let isFirst = true;

      staff.items.forEach(item => {
        result.push({
          name: item.category === '총 합계' ? '' : isFirst ? staff.name : '',
          category: item.category,
          target: item.target,
          totalSales: item.totalSales,
          rate: item.target ? calculateRate(item.target, item.totalSales) : 0,
        });
        isFirst = false;
      });
    });

    return result;
  }

  const calculateRate = (target, actual) => {
    if (!target || target === 0) return 0;
    return Math.round((actual / target) * 100);
  };

  const openIncentivePopup = () => {
    showIncentiveModal.value = true;
  };

  const openTargetPopup = () => {
    showTargetSalesModal.value = true;
  };

  const getRowClass = row => {
    if (row.name === '총계' && row.category === '') return 'summary-row';
    if (row.category === '총계') return 'staff-summary-row';
    return '';
  };

  const formatCurrency = value => {
    return value?.toLocaleString('ko-KR') ?? '-';
  };
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
    background-color: #eaeaea;
  }
  :deep(.staff-summary-row) {
    font-weight: bold;
    background-color: #f6f6f6;
  }
</style>
