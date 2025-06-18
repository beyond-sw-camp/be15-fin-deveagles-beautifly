import { ref, reactive, computed } from 'vue';
import { generateMockAnalyticsData } from '../utils/mockDataGenerator.js';
import {
  createDailySalesChartOption,
  createMonthlySalesChartOption,
  createCategorySalesChartOption,
} from '../config/chartOptions.js';
import { formatCurrency, formatPercentage } from '../utils/formatters.js';

export function useSalesAnalytics() {
  const loading = ref(false);
  const error = ref(null);

  // 매출 데이터
  const salesData = reactive({
    totalSales: 0,
    monthlyGrowth: 0,
    dailyAverage: 0,
    topProducts: [],
    monthlySales: [],
    dailySales: [],
    categorySales: [],
  });

  // 필터 옵션
  const filters = reactive({
    period: '30d', // 7d, 30d, 90d, 1y
    category: 'all',
    dateRange: {
      start: null,
      end: null,
    },
  });

  // 데이터 로드
  const loadSalesData = async () => {
    loading.value = true;
    error.value = null;

    try {
      // TODO: 실제 API 호출로 교체
      // const response = await axios.get('/api/analytics/sales', { params: filters })

      // 가짜 데이터 사용
      const mockData = generateMockAnalyticsData();

      salesData.totalSales = mockData.totalSales;
      salesData.monthlyGrowth = mockData.monthlyGrowth;
      salesData.dailyAverage = mockData.dailyAverage;
      salesData.topProducts = mockData.topProducts;
      salesData.monthlySales = mockData.monthlySales;
      salesData.dailySales = mockData.dailySales;
      salesData.categorySales = mockData.categorySales;
    } catch (err) {
      error.value = err.message;
      console.error('매출 데이터 로드 실패:', err);
    } finally {
      loading.value = false;
    }
  };

  // 차트 옵션 생성 (computed로 반응형 데이터와 연결)
  const getDailySalesChartOption = computed(() =>
    createDailySalesChartOption(salesData.dailySales)
  );

  const getMonthlySalesChartOption = computed(() =>
    createMonthlySalesChartOption(salesData.monthlySales)
  );

  const getCategorySalesChartOption = computed(() =>
    createCategorySalesChartOption(salesData.categorySales)
  );

  // 필터 업데이트
  const updateFilters = newFilters => {
    Object.assign(filters, newFilters);
    loadSalesData();
  };

  return {
    // 상태
    loading,
    error,
    salesData,
    filters,

    // 메서드
    loadSalesData,
    updateFilters,

    // 차트 옵션
    getDailySalesChartOption,
    getMonthlySalesChartOption,
    getCategorySalesChartOption,

    // 유틸리티
    formatCurrency,
    formatPercentage,
  };
}
