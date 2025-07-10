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
            { text: '월별 조회', value: 'MONTH' },
            { text: '기간별 조회', value: 'PERIOD' },
          ]"
        />
      </div>
      <div class="date-picker">
        <PrimeDatePicker
          v-if="searchMode === 'MONTH'"
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
      <input v-model="staffNameFilter" placeholder="직원 이름 검색" class="name-filter-input" />
      <div class="incentive-guide">※ 괄호 안의 <span>파란 숫자</span>는 인센티브 금액입니다.</div>
    </div>
    <!-- 테이블 -->
    <div class="table-wrapper">
      <div v-if="loading" class="table-loading-overlay">
        <BaseLoading text="정산 내역을 불러오는 중입니다..." />
      </div>
      <BaseTable
        v-if="!loading && staffSalesApiData"
        :columns="columns"
        :data="currentData"
        :row-class="getRowClass"
        :scroll="{ y: '600px' }"
        :pagination="false"
        :sticky-header="true"
      >
        <!-- 카드 -->
        <template #cell-CARD="{ item }">
          {{ formatCurrency(item?.CARD ?? 0) }}
          <div class="incentive-amount">({{ formatCurrency(item?.CARD_INCENTIVE ?? 0) }})</div>
        </template>

        <!-- 현금 -->
        <template #cell-CASH="{ item }">
          {{ formatCurrency(item?.CASH ?? 0) }}
          <div class="incentive-amount">({{ formatCurrency(item?.CASH_INCENTIVE ?? 0) }})</div>
        </template>

        <!-- 네이버페이 -->
        <template #cell-NAVER_PAY="{ item }">
          {{ formatCurrency(item?.NAVER_PAY ?? 0) }}
          <div class="incentive-amount">({{ formatCurrency(item?.NAVER_PAY_INCENTIVE ?? 0) }})</div>
        </template>

        <!-- 지역화폐 -->
        <template #cell-LOCAL="{ item }">
          {{ formatCurrency(item?.LOCAL ?? 0) }}
          <div class="incentive-amount">({{ formatCurrency(item?.LOCAL_INCENTIVE ?? 0) }})</div>
        </template>

        <!-- 할인 -->
        <template #cell-DISCOUNT="{ item }">
          {{ formatCurrency(item?.DISCOUNT ?? 0) }}
        </template>

        <!-- 쿠폰 -->
        <template #cell-COUPON="{ item }">
          {{ formatCurrency(item?.COUPON ?? 0) }}
        </template>

        <!-- 선불권 -->
        <template #cell-PREPAID="{ item }">
          {{ formatCurrency(item?.PREPAID ?? 0) }}
        </template>

        <!-- 총 실매출 -->
        <template #cell-totalSales="{ item }">
          {{ formatCurrency(item?.totalSales ?? 0) }}
        </template>

        <!-- 총 공제 -->
        <template #cell-totalDeductions="{ item }">
          {{ formatCurrency(item?.totalDeductions ?? 0) }}
        </template>

        <!-- 최종 실매출 -->
        <template #cell-finalSales="{ item }">
          {{ formatCurrency(item?.finalSales ?? 0) }}
        </template>
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
  <BaseToast ref="toastRef" />
</template>

<script setup>
  import { computed, onMounted, ref, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseLoading from '@/components/common/BaseLoading.vue';
  import BaseTab from '@/components/common/BaseTab.vue';
  import IncentiveSettingModal from '@/features/staffsales/components/IncentiveSettingModal.vue';
  import BaseSlidePanel from '@/features/staffsales/components/BaseSlidePanel.vue';
  import TargetSalesSettingModal from '@/features/staffsales/components/TargetSalesSettingModal.vue';
  import { getStaffDetailSales, getStaffSales } from '@/features/staffsales/api/staffsales.js';
  import BaseToast from '@/components/common/BaseToast.vue';
  import dayjs from 'dayjs';

  const toastRef = ref();
  const activeTab = ref('직원별 결산');
  const tabs = ['직원별 결산', '직원별 상세결산', '목표매출'];
  const searchMode = ref('MONTH');
  const selectedMonth = ref(new Date());
  const selectedRange = ref([]);
  const showIncentiveModal = ref(false);
  const showTargetSalesModal = ref(false);
  const loading = ref(false);
  const staffSalesApiData = ref(null);
  const staffNameFilter = ref('');

  const categoryLabelMap = {
    PRODUCT: '상품-제품',
    SERVICE: '상품-시술',
    SESSION_PASS: '회원권-횟수권',
    PREPAID_PASS: '회원권-선불권',
  };

  const baseColumns = [
    { title: '직원 이름', key: 'name' },
    { title: '상품 구분', key: 'category' },
    // 매출 항목
    { title: '카드', key: 'CARD' },
    { title: '현금', key: 'CASH' },
    { title: '네이버페이', key: 'NAVER_PAY' },
    { title: '지역화폐', key: 'LOCAL' },
    { title: '실매출액', key: 'totalSales' },
    // 공제 항목
    { title: '할인', key: 'DISCOUNT' },
    { title: '쿠폰', key: 'COUPON' },
    { title: '선불권', key: 'PREPAID' },
    // 합계
    { title: '총 공제', key: 'totalDeductions' },
    { title: '총영업액', key: 'finalSales' },
  ];

  const detailColums = [
    { title: '직원 이름', key: 'name' },
    { title: '상품 구분', key: 'category' },
    { title: '1차', key: 'primary' },
    { title: '2차', key: 'secondary' },
    // 매출 항목
    { title: '카드', key: 'CARD' },
    { title: '현금', key: 'CASH' },
    { title: '네이버페이', key: 'NAVER_PAY' },
    { title: '지역화폐', key: 'LOCAL' },
    { title: '실매출액', key: 'totalSales' },
    // 공제 항목
    { title: '할인', key: 'DISCOUNT' },
    { title: '쿠폰', key: 'COUPON' },
    { title: '선불권', key: 'PREPAID' },
    // 합계
    { title: '총 공제', key: 'totalDeductions' },
    { title: '총영업액', key: 'finalSales' },
  ];

  const targetColumns = [
    { title: '직원 이름', key: 'name' },
    { title: '분류', key: 'category' },
    { title: '목표', key: 'target' },
    { title: '총영업액', key: 'totalSales' },
    { title: '달성률', key: 'rate' },
  ];

  const columns = computed(() =>
    activeTab.value === '목표매출'
      ? targetColumns
      : activeTab.value === '직원별 상세결산'
        ? detailColums
        : baseColumns
  );

  const currentData = computed(() => {
    if (!staffSalesApiData.value) return [];
    switch (activeTab.value) {
      case '직원별 상세결산':
        return flattenDetailData();
      case '목표매출':
        return flattenTargetSales();
      default:
        return flattenStaffSalesList();
    }
  });

  const formatToISODate = date => {
    return dayjs(date).format('YYYY-MM-DD');
  };

  const getFormattedDates = () => {
    if (searchMode.value === 'MONTH') {
      const start = new Date(selectedMonth.value);
      return {
        startDate: formatToISODate(start),
        endDate: null,
      };
    } else {
      const start = selectedRange.value?.[0];
      const end = selectedRange.value?.[1];
      return {
        startDate: formatToISODate(start),
        endDate: formatToISODate(end),
      };
    }
  };

  const fetchStaffSales = async () => {
    loading.value = true;
    try {
      staffSalesApiData.value = null;
      const { startDate, endDate } = getFormattedDates();
      const payload = {
        searchMode: searchMode.value,
        startDate,
      };
      if (endDate) payload.endDate = endDate;
      let data;
      if (activeTab.value === '직원별 상세결산') data = await getStaffDetailSales(payload);
      else if (activeTab.value === '목표매출')
        data = { data: { staffSalesList: [] } }; // todo : api 연동
      else data = await getStaffSales(payload);
      staffSalesApiData.value = data.data.data;
    } catch (err) {
      staffSalesApiData.value = { staffSalesList: [] };
      toastRef.value?.error?.('직원 결산 조회에 실패했습니다.');
      console.error(`직원 결산 조회 실패`, err);
    } finally {
      loading.value = false;
    }
  };

  watch([searchMode, selectedMonth, selectedRange, activeTab], () => {
    const isMonthValid = searchMode.value === 'MONTH';
    const isPeriodValid =
      searchMode.value === 'PERIOD' &&
      Array.isArray(selectedRange.value) &&
      selectedRange.value.length === 2 &&
      selectedRange.value[0] &&
      selectedRange.value[1];

    if (isMonthValid || isPeriodValid) {
      fetchStaffSales();
    }
  });

  const flattenStaffSalesList = () => {
    if (!staffSalesApiData.value?.staffSalesList) return [];
    const result = [];
    staffSalesApiData.value.staffSalesList.forEach(staff => {
      if (staffNameFilter.value && !staff.staffName.includes(staffNameFilter.value.trim())) {
        return;
      }
      let isFirstRow = true;
      staff.paymentsSalesList.forEach(payment => {
        const row = {
          name: isFirstRow ? staff.staffName : '',
          category: categoryLabelMap[payment.category] || payment.category,
          CARD: 0,
          CASH: 0,
          NAVER_PAY: 0,
          LOCAL: 0,
          CARD_INCENTIVE: 0,
          CASH_INCENTIVE: 0,
          NAVER_PAY_INCENTIVE: 0,
          LOCAL_INCENTIVE: 0,
          DISCOUNT: 0,
          COUPON: 0,
          PREPAID: 0,
          totalSales: 0,
          totalDeductions: 0,
          finalSales: 0,
        };

        payment.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
          if (
            paymentsMethod !== 'PREPAID' &&
            Object.prototype.hasOwnProperty.call(row, paymentsMethod)
          ) {
            row[paymentsMethod] += amount;
            row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
            row.totalSales += amount;
          }
        });

        payment.deductionList?.forEach(({ deduction, amount }) => {
          if (Object.prototype.hasOwnProperty.call(row, deduction)) {
            row[deduction] += amount;
            row.totalDeductions += amount;
          }
        });

        row.finalSales = (row.totalSales || 0) - (row.totalDeductions || 0);
        result.push(row);
        isFirstRow = false;
      });
    });

    const totals = {
      name: '총계',
      category: '',
      CARD: 0,
      CASH: 0,
      NAVER_PAY: 0,
      LOCAL: 0,
      CARD_INCENTIVE: 0,
      CASH_INCENTIVE: 0,
      NAVER_PAY_INCENTIVE: 0,
      LOCAL_INCENTIVE: 0,
      DISCOUNT: 0,
      COUPON: 0,
      PREPAID: 0,
      totalSales: 0,
      totalDeductions: 0,
      finalSales: 0,
    };

    result.forEach(row => {
      ['CARD', 'CASH', 'NAVER_PAY', 'LOCAL'].forEach(method => {
        totals[method] += row[method];
        totals[`${method}_INCENTIVE`] += row[`${method}_INCENTIVE`];
      });

      ['DISCOUNT', 'COUPON', 'PREPAID', 'totalSales', 'totalDeductions', 'finalSales'].forEach(
        key => {
          totals[key] += row[key] || 0;
        }
      );
    });

    result.push(totals);
    return result;
  };

  const flattenDetailData = () => {
    if (!staffSalesApiData.value?.staffSalesList) return [];

    const result = [];

    staffSalesApiData.value.staffSalesList.forEach(staff => {
      if (staffNameFilter.value && !staff.staffName.includes(staffNameFilter.value.trim())) {
        return;
      }

      const staffRows = [];
      let isFirstRow = true;

      // 1. 상품(시술/제품)
      staff.paymentsDetailSalesList.forEach(payment => {
        const categoryLabel = categoryLabelMap[payment.category] || payment.category;

        payment.primaryList?.forEach(primary => {
          const primaryName = primary.primaryItemName;

          primary.secondaryList?.forEach(secondary => {
            const row = {
              name: isFirstRow ? staff.staffName : '',
              category: categoryLabel,
              primary: primaryName,
              secondary: secondary.secondaryItemName,
              CARD: 0,
              CASH: 0,
              NAVER_PAY: 0,
              LOCAL: 0,
              CARD_INCENTIVE: 0,
              CASH_INCENTIVE: 0,
              NAVER_PAY_INCENTIVE: 0,
              LOCAL_INCENTIVE: 0,
              DISCOUNT: 0,
              COUPON: 0,
              PREPAID: 0,
              totalSales: 0,
              totalDeductions: 0,
              finalSales: 0,
            };

            secondary.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
              if (
                paymentsMethod !== 'PREPAID' &&
                Object.prototype.hasOwnProperty.call(row, paymentsMethod)
              ) {
                row[paymentsMethod] += amount;
                row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
                row.totalSales += amount;
              }
            });

            secondary.deductionList?.forEach(({ deduction, amount }) => {
              if (Object.prototype.hasOwnProperty.call(row, deduction)) {
                row[deduction] += amount;
                row.totalDeductions += amount;
              }
            });

            row.finalSales = row.totalSales - row.totalDeductions;

            staffRows.push(row);
            isFirstRow = false;
          });
        });
      });

      // 2. 회원권(횟수권/선불권)
      staff.paymentsSalesList?.forEach(payment => {
        const row = {
          name: isFirstRow ? staff.staffName : '',
          category: categoryLabelMap[payment.category] || payment.category,
          primary: '',
          secondary: '',
          CARD: 0,
          CASH: 0,
          NAVER_PAY: 0,
          LOCAL: 0,
          CARD_INCENTIVE: 0,
          CASH_INCENTIVE: 0,
          NAVER_PAY_INCENTIVE: 0,
          LOCAL_INCENTIVE: 0,
          DISCOUNT: 0,
          COUPON: 0,
          PREPAID: 0,
          totalSales: 0,
          totalDeductions: 0,
          finalSales: 0,
        };

        payment.netSalesList?.forEach(({ paymentsMethod, amount, incentiveAmount }) => {
          if (
            paymentsMethod !== 'PREPAID' &&
            Object.prototype.hasOwnProperty.call(row, paymentsMethod)
          ) {
            row[paymentsMethod] += amount;
            row[`${paymentsMethod}_INCENTIVE`] += incentiveAmount;
            row.totalSales += amount;
          }
        });

        payment.deductionList?.forEach(({ deduction, amount }) => {
          if (Object.prototype.hasOwnProperty.call(row, deduction)) {
            row[deduction] += amount;
            row.totalDeductions += amount;
          }
        });

        row.finalSales = row.totalSales - row.totalDeductions;

        staffRows.push(row);
        isFirstRow = false;
      });

      // 3. 직원별 총계
      const summaryRow = {
        name: '',
        category: '총계',
        primary: '',
        secondary: '',
        CARD: 0,
        CASH: 0,
        NAVER_PAY: 0,
        LOCAL: 0,
        CARD_INCENTIVE: 0,
        CASH_INCENTIVE: 0,
        NAVER_PAY_INCENTIVE: 0,
        LOCAL_INCENTIVE: 0,
        DISCOUNT: 0,
        COUPON: 0,
        PREPAID: 0,
        totalSales: 0,
        totalDeductions: 0,
        finalSales: 0,
      };

      staffRows.forEach(row => {
        ['CARD', 'CASH', 'NAVER_PAY', 'LOCAL'].forEach(method => {
          summaryRow[method] += row[method];
          summaryRow[`${method}_INCENTIVE`] += row[`${method}_INCENTIVE`];
        });

        ['DISCOUNT', 'COUPON', 'PREPAID', 'totalSales', 'totalDeductions', 'finalSales'].forEach(
          key => {
            summaryRow[key] += row[key] || 0;
          }
        );
      });
      result.push(...staffRows, summaryRow);
    });
    return result;
  };

  const flattenTargetSales = () => {
    const result = [];
    staffSalesApiData.value?.staffSalesList?.forEach(staff => {
      let isFirst = true;
      staff.items?.forEach(item => {
        result.push({
          name: item.category === '총 합계' ? '' : isFirst ? staff.staffName : '',
          category: item.category,
          target: item.target,
          totalSales: item.totalSales,
          rate: item.target ? calculateRate(item.target, item.totalSales) : 0,
        });
        isFirst = false;
      });
    });
    return result;
  };

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

  const formatCurrency = value => (typeof value === 'number' ? value.toLocaleString('ko-KR') : '0');

  onMounted(() => {
    const { startDate, endDate } = getFormattedDates();
    if ((searchMode.value === 'PERIOD' && startDate && endDate) || searchMode.value === 'MONTH') {
      fetchStaffSales();
    }
  });
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
  .table-wrapper {
    background-color: #fff;
    border-radius: 12px;
    box-shadow:
      0 0 0 1px #e5e7eb,
      0 1px 3px rgba(0, 0, 0, 0.05);
    padding: 24px;
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
  .staff-settlement-page {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .incentive-amount {
    font-size: 12px;
    color: #3f51b5;
  }
  .filter-row {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 1rem;
  }
  .name-filter-input {
    height: 100%;
    padding: 12px;
    font-size: 14px;
    border: 1px solid var(--surface-border, #cbd5e1);
    border-radius: 6px;
    line-height: 1.5;
    box-sizing: border-box;
    transition:
      border-color 0.2s ease,
      box-shadow 0.2s ease;
  }
  .name-filter-input:focus {
    outline: none;
    border-color: #1a2331;
    box-shadow: 0 0 0 0.125rem rgba(0, 0, 0, 0.1);
  }
  .incentive-guide {
    font-size: 13px;
    color: var(--color-gray-600);
  }
  .incentive-guide span {
    color: #3f51b5;
    font-weight: 500;
  }
</style>
