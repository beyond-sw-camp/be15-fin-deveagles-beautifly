<template>
  <div
    class="analytics-page"
    :class="{
      dark: isDarkMode,
      transitioning: isTransitioning,
    }"
  >
    <!-- 페이지 헤더 -->
    <div class="analytics-header">
      <div class="analytics-header-content">
        <div>
          <h1 class="analytics-page-title">예약율 통계</h1>
        </div>

        <!-- 필터 섹션 -->
        <div class="analytics-filters">
          <div class="analytics-filter-group">
            <select
              v-model="filters.period"
              class="analytics-filter-select"
              @change="updateFilters({ period: $event.target.value })"
            >
              <option value="7d">최근 7일</option>
              <option value="30d">최근 30일</option>
              <option value="90d">최근 90일</option>
              <option value="1y">최근 1년</option>
            </select>
          </div>

          <div class="analytics-filter-group">
            <select
              v-model="filters.viewType"
              class="analytics-filter-select"
              @change="updateFilters({ viewType: $event.target.value })"
            >
              <option value="hourly">시간대별</option>
              <option value="daily">일별</option>
              <option value="weekly">주별</option>
              <option value="monthly">월별</option>
            </select>
          </div>

          <!-- 다크모드 토글 스위치 -->
          <div class="analytics-filter-group">
            <LocalDarkModeToggle />
          </div>
        </div>
      </div>
    </div>

    <!-- 주요 지표 카드 -->
    <div class="analytics-stats-grid">
      <StatCard
        icon="📊"
        label="전체 예약율"
        :value="usageData.overallUtilization + '%'"
        :trend="formatPercentage(usageData.utilizationGrowth)"
        trend-type="positive"
        variant="primary"
      />

      <StatCard
        icon="⏰"
        label="평균 이용시간"
        :value="usageData.averageUsageTime + '분'"
        trend="+12.3%"
        trend-type="positive"
        variant="success"
      />

      <StatCard
        icon="👥"
        label="직원 가동률"
        :value="usageData.staffUtilization + '%'"
        trend="+5.7%"
        trend-type="positive"
        variant="info"
      />

      <StatCard
        icon="🏆"
        label="피크 시간 예약율"
        :value="usageData.peakHourUtilization + '%'"
        trend="-1.2%"
        trend-type="negative"
        variant="warning"
      />
    </div>

    <!-- 차트 그리드 -->
    <div class="chart-grid">
      <!-- 시간대별 예약율 -->
      <div class="chart-container chart-full-width">
        <div class="chart-header">
          <h3 class="chart-title">시간대별 예약율 분석</h3>
          <p class="chart-subtitle">일일 운영시간 동안의 예약율 패턴</p>
        </div>
        <BaseChart
          v-if="!loading && usageData.hourlyUsage.length > 0"
          :option="getHourlyUsageChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="320px"
          @click="onChartClick"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
          <span class="analytics-loading-text">데이터를 불러오는 중...</span>
        </div>
      </div>

      <!-- 서비스별 예약율 (남성) -->
      <div class="chart-container">
        <div class="chart-header">
          <h3 class="chart-title">서비스별 예약율 (남성)</h3>
        </div>
        <BaseChart
          v-if="!loading && usageData.serviceUsage.length > 0"
          :option="getServiceUsageChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="350px"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
        </div>
      </div>

      <!-- 서비스별 예약율 (여성) -->
      <div class="chart-container">
        <div class="chart-header">
          <h3 class="chart-title">서비스별 예약율 (여성)</h3>
        </div>
        <BaseChart
          v-if="!loading && usageData.serviceUsage.length > 0"
          :option="getServiceUsageChartOptionFemale"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="350px"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
        </div>
      </div>

      <!-- 직원별 가동률 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3 class="chart-title">직원별 가동률</h3>
        </div>
        <BaseChart
          v-if="!loading && usageData.staffUsage.length > 0"
          :option="getStaffUsageChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="350px"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
        </div>
      </div>

      <!-- 월별 예약율 추이 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3 class="chart-title">월별 예약율 추이</h3>
        </div>
        <BaseChart
          v-if="!loading && usageData.monthlyUsage.length > 0"
          :option="getMonthlyUsageChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="350px"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
        </div>
      </div>

      <!-- 피크 시간 히트맵 -->
      <div class="chart-container chart-full-width">
        <div class="chart-header">
          <h3 class="chart-title">요일별 시간대 예약율 히트맵</h3>
          <p class="chart-subtitle">요일과 시간대에 따른 예약율 패턴</p>
        </div>
        <BaseChart
          v-if="!loading && usageData.heatmapData.length > 0"
          :option="getHeatmapChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="280px"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { onMounted } from 'vue';
  import BaseChart from '../components/charts/BaseChart.vue';
  import StatCard from '../components/StatCard.vue';
  import LocalDarkModeToggle from '../components/LocalDarkModeToggle.vue';
  import { useUsageAnalytics } from '../composables/useUsageAnalytics.js';
  import { useLocalDarkMode } from '../composables/useLocalDarkMode.js';

  export default {
    name: 'UsageAnalytics',
    components: {
      BaseChart,
      StatCard,
      LocalDarkModeToggle,
    },
    setup() {
      // 로컬 다크모드 상태 관리
      const { isDarkMode, isTransitioning, initializeLocalDarkMode } = useLocalDarkMode();

      // 이용률 분석 컴포저블 사용
      const {
        loading,
        error,
        usageData,
        filters,
        loadUsageData,
        updateFilters,
        getHourlyUsageChartOption,
        getServiceUsageChartOption,
        getServiceUsageChartOptionFemale,
        getStaffUsageChartOption,
        getMonthlyUsageChartOption,
        getHeatmapChartOption,
        formatPercentage,
      } = useUsageAnalytics();

      // 차트 클릭 이벤트 핸들러
      const onChartClick = event => {
        console.log('차트 클릭:', event);
        // 차트 클릭 시 상세 정보 표시 로직 추가
      };

      // 컴포넌트 마운트 시 데이터 로드 및 로컬 다크모드 초기화
      onMounted(() => {
        loadUsageData();
        initializeLocalDarkMode();
      });

      // 템플릿에서 사용할 모든 변수와 함수 반환
      return {
        // 상태
        isDarkMode,
        isTransitioning,
        loading,
        error,
        usageData,
        filters,

        // 메서드
        loadUsageData,
        updateFilters,
        onChartClick,

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
    },
  };
</script>

<style scoped>
  @import '../styles/charts.css';
  @import '../styles/analytics.css';
  @import '../styles/usage.css';
</style>
