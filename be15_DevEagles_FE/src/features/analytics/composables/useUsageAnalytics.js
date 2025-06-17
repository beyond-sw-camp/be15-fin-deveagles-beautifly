import { ref, reactive, computed } from 'vue';
import { generateMockUsageData } from '../utils/mockUsageDataGenerator.js';
import {
  createHourlyUsageChartOption,
  createServiceUsageChartOption,
  createServiceUsageChartOptionFemale,
  createStaffUsageChartOption,
  createMonthlyUsageChartOption,
  createHeatmapChartOption,
} from '../config/usageChartOptions.js';
import { formatPercentage } from '../utils/formatters.js';

export function useUsageAnalytics() {
  const loading = ref(false);
  const error = ref(null);

  // 예약율 데이터
  const usageData = reactive({
    overallUtilization: 0,
    utilizationGrowth: 0,
    averageUsageTime: 0,
    staffUtilization: 0,
    peakHourUtilization: 0,
    hourlyUsage: [],
    serviceUsage: [],
    staffUsage: [],
    monthlyUsage: [],
    heatmapData: [],
    peakHours: [],
    serviceDuration: [],
    monthlyGrowth: [],
  });

  // 필터 옵션
  const filters = reactive({
    period: '30d', // 7d, 30d, 90d, 1y
    viewType: 'hourly', // hourly, daily, weekly, monthly
    dateRange: {
      start: null,
      end: null,
    },
  });

  // 데이터 로드
  const loadUsageData = async () => {
    loading.value = true;
    error.value = null;

    try {
      // TODO: 실제 API 호출로 교체
      // const response = await axios.get('/api/analytics/usage', { params: filters })

      // 가짜 데이터 사용
      const mockData = generateMockUsageData();

      usageData.overallUtilization = mockData.overallUtilization;
      usageData.utilizationGrowth = mockData.utilizationGrowth;
      usageData.averageUsageTime = mockData.averageUsageTime;
      usageData.staffUtilization = mockData.staffUtilization;
      usageData.peakHourUtilization = mockData.peakHourUtilization;
      usageData.hourlyUsage = mockData.hourlyUsage;
      usageData.serviceUsage = mockData.serviceUsage;
      usageData.staffUsage = mockData.staffUsage;
      usageData.monthlyUsage = mockData.monthlyUsage;
      usageData.heatmapData = mockData.heatmapData;
      usageData.peakHours = mockData.peakHours;
      usageData.serviceDuration = mockData.serviceDuration;
      usageData.monthlyGrowth = mockData.monthlyGrowth;
    } catch (err) {
      error.value = err.message;
      console.error('예약율 데이터 로드 실패:', err);
    } finally {
      loading.value = false;
    }
  };

  // 차트 옵션 생성 (computed로 반응형 데이터와 연결)
  const getHourlyUsageChartOption = computed(() =>
    createHourlyUsageChartOption(usageData.hourlyUsage)
  );

  const getServiceUsageChartOption = computed(() =>
    createServiceUsageChartOption(usageData.serviceUsage)
  );

  const getServiceUsageChartOptionFemale = computed(() =>
    createServiceUsageChartOptionFemale(usageData.serviceUsage)
  );

  const getStaffUsageChartOption = computed(() =>
    createStaffUsageChartOption(usageData.staffUsage)
  );

  const getMonthlyUsageChartOption = computed(() =>
    createMonthlyUsageChartOption(usageData.monthlyUsage)
  );

  const getHeatmapChartOption = computed(() => createHeatmapChartOption(usageData.heatmapData));

  // 필터 업데이트
  const updateFilters = newFilters => {
    Object.assign(filters, newFilters);
    loadUsageData();
  };

  return {
    // 상태
    loading,
    error,
    usageData,
    filters,

    // 메서드
    loadUsageData,
    updateFilters,

    // 차트 옵션
    getHourlyUsageChartOption,
    getServiceUsageChartOption,
    getServiceUsageChartOptionFemale,
    getStaffUsageChartOption,
    getMonthlyUsageChartOption,
    getHeatmapChartOption,

    // 유틸리티
    formatPercentage,
  };
}
