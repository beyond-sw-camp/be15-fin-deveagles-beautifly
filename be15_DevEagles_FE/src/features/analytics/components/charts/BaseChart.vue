<template>
  <div class="base-chart" :style="{ width, height }">
    <v-chart
      ref="chartRef"
      :option="chartOption"
      :loading="loading"
      :loading-options="loadingOptions"
      :theme="theme"
      autoresize
      @click="onChartClick"
    />
  </div>
</template>

<script setup>
  import { use } from 'echarts/core';
  import { CanvasRenderer } from 'echarts/renderers';
  import { BarChart, LineChart, PieChart } from 'echarts/charts';
  import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
    DatasetComponent,
    TransformComponent,
  } from 'echarts/components';
  import VChart from 'vue-echarts';
  import { ref, computed, watch, nextTick } from 'vue';

  // ECharts 구성 요소 등록
  use([
    CanvasRenderer,
    BarChart,
    LineChart,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
    DatasetComponent,
    TransformComponent,
  ]);

  const props = defineProps({
    option: {
      type: Object,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    width: {
      type: String,
      default: '100%',
    },
    height: {
      type: String,
      default: '400px',
    },
    theme: {
      type: String,
      default: 'light',
    },
    isDarkMode: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['click']);

  const chartRef = ref(null);

  const chartOption = computed(() => {
    const isDark = props.isDarkMode;
    const baseOption = { ...props.option };

    // 다크모드 색상을 강제로 덮어쓰기
    if (isDark) {
      // X축 처리
      if (baseOption.xAxis) {
        baseOption.xAxis = {
          ...baseOption.xAxis,
          axisLabel: {
            ...baseOption.xAxis.axisLabel,
            color: '#D1D5DB',
          },
          axisLine: {
            ...baseOption.xAxis.axisLine,
            lineStyle: {
              ...baseOption.xAxis.axisLine?.lineStyle,
              color: '#4B5563',
            },
          },
          axisTick: {
            ...baseOption.xAxis.axisTick,
            lineStyle: {
              ...baseOption.xAxis.axisTick?.lineStyle,
              color: '#4B5563',
            },
          },
          splitLine: {
            ...baseOption.xAxis.splitLine,
            lineStyle: {
              ...baseOption.xAxis.splitLine?.lineStyle,
              color: '#374151',
              type: 'dashed',
            },
          },
        };
      }

      // Y축 처리
      if (baseOption.yAxis) {
        baseOption.yAxis = {
          ...baseOption.yAxis,
          axisLabel: {
            ...baseOption.yAxis.axisLabel,
            color: '#D1D5DB',
          },
          axisLine: {
            ...baseOption.yAxis.axisLine,
            lineStyle: {
              ...baseOption.yAxis.axisLine?.lineStyle,
              color: '#4B5563',
            },
          },
          axisTick: {
            ...baseOption.yAxis.axisTick,
            lineStyle: {
              ...baseOption.yAxis.axisTick?.lineStyle,
              color: '#4B5563',
            },
          },
          splitLine: {
            ...baseOption.yAxis.splitLine,
            lineStyle: {
              ...baseOption.yAxis.splitLine?.lineStyle,
              color: '#374151',
              type: 'dashed',
            },
          },
        };
      }

      // 범례 처리
      if (baseOption.legend) {
        baseOption.legend = {
          ...baseOption.legend,
          textStyle: {
            ...baseOption.legend.textStyle,
            color: '#F9FAFB',
          },
        };
      }

      // 제목 처리
      if (baseOption.title) {
        baseOption.title = {
          ...baseOption.title,
          textStyle: {
            ...baseOption.title.textStyle,
            color: '#F9FAFB',
          },
        };
      }

      // 시리즈 처리
      if (baseOption.series && Array.isArray(baseOption.series)) {
        baseOption.series = baseOption.series.map(s => ({
          ...s,
          itemStyle: {
            ...s.itemStyle,
            borderColor: '#374151',
          },
          label: {
            ...s.label,
            color: '#F9FAFB',
          },
        }));
      }

      // 툴팁 처리
      if (baseOption.tooltip) {
        baseOption.tooltip = {
          ...baseOption.tooltip,
          backgroundColor: 'rgba(31, 41, 55, 0.95)',
          borderColor: '#4B5563',
          borderWidth: 1,
          textStyle: {
            ...baseOption.tooltip.textStyle,
            color: '#F9FAFB',
          },
        };
      } else {
        // 기본 툴팁 설정 (다크모드)
        baseOption.tooltip = {
          trigger: 'axis',
          backgroundColor: 'rgba(31, 41, 55, 0.95)',
          borderColor: '#4B5563',
          borderWidth: 1,
          textStyle: {
            color: '#F9FAFB',
          },
        };
      }
    } else {
      // 라이트모드 색상 적용
      if (baseOption.xAxis) {
        baseOption.xAxis = {
          ...baseOption.xAxis,
          axisLabel: {
            ...baseOption.xAxis.axisLabel,
            color: '#6B7280',
          },
        };
      }

      if (baseOption.yAxis) {
        baseOption.yAxis = {
          ...baseOption.yAxis,
          axisLabel: {
            ...baseOption.yAxis.axisLabel,
            color: '#6B7280',
          },
        };
      }

      if (baseOption.legend) {
        baseOption.legend = {
          ...baseOption.legend,
          textStyle: {
            ...baseOption.legend.textStyle,
            color: '#374151',
          },
        };
      }

      if (baseOption.title) {
        baseOption.title = {
          ...baseOption.title,
          textStyle: {
            ...baseOption.title.textStyle,
            color: '#374151',
          },
        };
      }

      // 라이트모드 기본 툴팁 설정
      if (!baseOption.tooltip) {
        baseOption.tooltip = {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#E5E7EB',
          borderWidth: 1,
          textStyle: {
            color: '#374151',
          },
        };
      }
    }

    return {
      ...baseOption,
      backgroundColor: 'transparent',
      color: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'],
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
        ...baseOption.grid,
      },
    };
  });

  const loadingOptions = computed(() => ({
    text: '로딩 중...',
    color: '#3B82F6',
    textColor: props.isDarkMode ? '#F9FAFB' : '#374151',
    maskColor: props.isDarkMode ? 'rgba(31, 41, 55, 0.8)' : 'rgba(255, 255, 255, 0.8)',
    zlevel: 0,
  }));

  const onChartClick = event => {
    emit('click', event);
  };

  // 다크모드 변경 시 차트 부드럽게 전환
  watch(
    () => props.isDarkMode,
    async (newValue, oldValue) => {
      if (newValue !== oldValue && chartRef.value) {
        // 차트 전환 애니메이션 설정
        await nextTick();
        const chart = chartRef.value.chart;
        if (chart) {
          chart.setOption(chartOption.value, {
            notMerge: false,
            lazyUpdate: false,
            silent: false,
          });
        }
      }
    },
    { flush: 'post' }
  );
</script>

<style scoped>
  .base-chart {
    position: relative;
  }

  /* 차트 컨테이너 transition 효과 */
  .base-chart :deep(.echarts) {
    transition: opacity 0.3s ease-in-out;
  }

  /* 다크모드 전환 시 차트 부드럽게 변경 */
  .dark-mode-transitioning .base-chart :deep(.echarts) {
    transition:
      opacity 0.3s ease-in-out,
      filter 0.3s ease-in-out;
  }
</style>
