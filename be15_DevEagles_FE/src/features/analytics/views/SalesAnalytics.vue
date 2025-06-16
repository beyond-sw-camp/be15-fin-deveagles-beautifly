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
          <h1 class="analytics-page-title">매출 통계</h1>
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
              v-model="filters.category"
              class="analytics-filter-select"
              @change="updateFilters({ category: $event.target.value })"
            >
              <option value="all">전체 서비스</option>
              <option value="hair">헤어 서비스</option>
              <option value="nail">네일 아트</option>
              <option value="skincare">스킨케어</option>
              <option value="makeup">메이크업</option>
            </select>
          </div>

          <!-- 다크모드 토글 스위치 -->
          <div class="analytics-filter-group">
            <DarkModeToggle />
          </div>
        </div>
      </div>
    </div>

    <!-- 주요 지표 카드 -->
    <div class="analytics-stats-grid">
      <StatCard
        icon="💰"
        label="총 매출"
        :value="formatCurrency(salesData.totalSales)"
        :trend="formatPercentage(salesData.monthlyGrowth)"
        trend-type="positive"
        variant="primary"
      />

      <StatCard
        icon="📊"
        label="일평균 매출"
        :value="formatCurrency(salesData.dailyAverage)"
        trend="+8.2%"
        trend-type="positive"
        variant="success"
      />

      <StatCard
        icon="🛍️"
        label="예약 수"
        :value="salesData.dailySales.reduce((sum, day) => sum + day.orders, 0).toLocaleString()"
        trend="+15.3%"
        trend-type="positive"
        variant="info"
      />

      <StatCard
        icon="💳"
        label="평균 이용금액"
        :value="
          formatCurrency(
            salesData.totalSales /
              Math.max(
                1,
                salesData.dailySales.reduce((sum, day) => sum + day.orders, 0)
              )
          )
        "
        trend="-2.1%"
        trend-type="negative"
        variant="warning"
      />
    </div>

    <!-- 차트 그리드 -->
    <div class="chart-grid">
      <!-- 일별 매출 추이 -->
      <div class="chart-container chart-full-width">
        <div class="chart-header">
          <h3 class="chart-title">서비스별 일별 매출 추이</h3>
          <p class="chart-subtitle">최근 30일간 서비스별 매출 변화</p>
        </div>
        <BaseChart
          v-if="!loading && salesData.dailySales.length > 0"
          :option="getDailySalesChartOption"
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

      <!-- 월별 매출 현황 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3 class="chart-title">월별 매출 추이</h3>
        </div>
        <BaseChart
          v-if="!loading && salesData.monthlySales.length > 0"
          :option="getMonthlySalesChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="350px"
        />
        <div v-else-if="loading" class="analytics-loading">
          <div class="analytics-loading-spinner"></div>
        </div>
      </div>

      <!-- 서비스별 매출 비율 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3 class="chart-title">서비스별 매출 점유율</h3>
        </div>
        <BaseChart
          v-if="!loading && salesData.categorySales.length > 0"
          :option="getCategorySalesChartOption"
          :loading="loading"
          :is-dark-mode="isDarkMode"
          height="350px"
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
  import DarkModeToggle from '@/components/common/DarkModeToggle.vue';
  import { useSalesAnalytics } from '../composables/useSalesAnalytics.js';
  import { useDarkMode } from '@/composables/useDarkMode.js';

  export default {
    name: 'SalesAnalytics',
    components: {
      BaseChart,
      StatCard,
      DarkModeToggle,
    },
    setup() {
      // 다크모드 상태 관리
      const { isDarkMode, isTransitioning } = useDarkMode();

      // 매출 분석 컴포저블 사용
      const {
        loading,
        error,
        salesData,
        filters,
        loadSalesData,
        updateFilters,
        getDailySalesChartOption,
        getMonthlySalesChartOption,
        getCategorySalesChartOption,
        formatCurrency,
        formatPercentage,
      } = useSalesAnalytics();

      // 차트 클릭 이벤트 핸들러
      const onChartClick = event => {
        console.log('차트 클릭:', event);
        // 차트 클릭 시 상세 정보 표시 로직 추가
      };

      // 컴포넌트 마운트 시 데이터 로드
      onMounted(() => {
        loadSalesData();
      });

      // 템플릿에서 사용할 모든 변수와 함수 반환
      return {
        // 상태
        isDarkMode,
        isTransitioning,
        loading,
        error,
        salesData,
        filters,

        // 메서드
        loadSalesData,
        updateFilters,
        onChartClick,

        // 차트 옵션
        getDailySalesChartOption,
        getMonthlySalesChartOption,
        getCategorySalesChartOption,

        // 유틸리티
        formatCurrency,
        formatPercentage,
      };
    },
  };
</script>

<style scoped>
  @import '../styles/charts.css';
  @import '../styles/analytics.css';
</style>
