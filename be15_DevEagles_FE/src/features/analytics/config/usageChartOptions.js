/**
 * 예약율 분석 ECharts 차트 옵션 설정
 * 예약율 통계용 차트 옵션들을 분리하여 관리
 */

import { createEmptyChartOption } from './baseChartOptions.js';

/**
 * 시간대별 방문객 성별 차트 옵션 (누적 막대 그래프)
 * @param {Array} hourlyUsageData - 시간대별 방문객 데이터
 * @returns {Object} ECharts 옵션
 */
export function createHourlyUsageChartOption(hourlyUsageData) {
  if (!hourlyUsageData || hourlyUsageData.length === 0) {
    return createEmptyChartOption('시간대별 방문객 데이터가 없습니다');
  }

  const maleColor = '#4F46E5'; // 남성 - 인디고
  const femaleColor = '#EC4899'; // 여성 - 핑크

  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderWidth: 0,
      borderRadius: 12,
      textStyle: {
        color: '#374151',
        fontSize: 12,
      },
      padding: [12, 16],
      extraCssText: 'box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px);',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(156, 163, 175, 0.2)',
        },
      },
      formatter: function (params) {
        if (!params || params.length === 0) return '';
        const axisValue = params[0].axisValue;
        const maleValue = params.find(p => p.seriesName === '남성')?.value || 0;
        const femaleValue = params.find(p => p.seriesName === '여성')?.value || 0;
        const total = maleValue + femaleValue;

        return `<div style="font-weight: 600; margin-bottom: 8px;">${axisValue}</div>
                <div style="margin-bottom: 4px;">남성: <strong>${maleValue}명</strong></div>
                <div style="margin-bottom: 4px;">여성: <strong>${femaleValue}명</strong></div>
                <div>총 방문객: <strong>${total}명</strong></div>`;
      },
    },
    legend: {
      data: ['남성', '여성'],
      top: 20,
      left: 'center',
      itemGap: 20,
      textStyle: {
        fontSize: 12,
        fontWeight: '500',
        color: '#374151',
      },
      itemWidth: 12,
      itemHeight: 12,
    },
    grid: {
      top: 80,
      left: 60,
      right: 50,
      bottom: 60,
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: hourlyUsageData.map(item => item.time || `${item.hour}시`),
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
        margin: 15,
        interval: 0,
      },
    },
    yAxis: {
      type: 'value',
      name: '방문객 수 (명)',
      nameTextStyle: {
        color: '#6B7280',
        fontSize: 12,
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
        formatter: '{value}명',
      },
      splitLine: {
        lineStyle: {
          color: '#F3F4F6',
          type: 'dashed',
          opacity: 0.5,
        },
      },
    },
    series: [
      {
        name: '남성',
        type: 'bar',
        stack: '방문객',
        data: hourlyUsageData.map(item => item.male || item.maleVisitors || 0),
        itemStyle: {
          color: maleColor,
          borderRadius: [0, 0, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: '#3730A3',
            shadowBlur: 10,
            shadowColor: 'rgba(79, 70, 229, 0.3)',
          },
        },
      },
      {
        name: '여성',
        type: 'bar',
        stack: '방문객',
        data: hourlyUsageData.map(item => item.female || item.femaleVisitors || 0),
        itemStyle: {
          color: femaleColor,
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: '#BE185D',
            shadowBlur: 10,
            shadowColor: 'rgba(236, 72, 153, 0.3)',
          },
        },
      },
    ],
  };
}

/**
 * 직원별 예약건수 차트 옵션
 * @param {Array} staffReservationData - 직원별 예약건수 데이터
 * @returns {Object} ECharts 옵션
 */
export function createStaffReservationChartOption(staffReservationData) {
  if (!staffReservationData || staffReservationData.length === 0) {
    return createEmptyChartOption('직원별 예약건수 데이터가 없습니다');
  }

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
      formatter: params => {
        const data = params[0];
        const item = data.data || {};
        const staff = staffReservationData[data.dataIndex] || {};

        return `<div style="font-weight: 600; margin-bottom: 8px;">${data.axisValue}</div>
                <div style="margin-bottom: 4px;">예약건수: <strong>${data.value}건</strong></div>`;
      },
    },
    grid: {
      top: 40,
      left: 60,
      right: 40,
      bottom: 60,
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: staffReservationData.map(item => item.staff),
      axisLine: {
        lineStyle: { color: '#E5E7EB' },
      },
      axisTick: {
        lineStyle: { color: '#E5E7EB' },
      },
      axisLabel: {
        color: '#6B7280',
        fontSize: 11,
        interval: 0,
        rotate: staffReservationData.length > 6 ? 45 : 0,
      },
    },
    yAxis: {
      type: 'value',
      name: '예약건수 (건)',
      nameTextStyle: {
        color: '#6B7280',
        fontSize: 12,
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#6B7280',
        formatter: '{value}건',
        fontSize: 11,
      },
      splitLine: {
        lineStyle: {
          color: '#F3F4F6',
          type: 'dashed',
        },
      },
    },
    series: [
      {
        name: '예약건수',
        type: 'bar',
        data: staffReservationData.map(item => ({
          value: item.reservationCount || 0,
          totalSales: item.totalSales || 0,
          staffId: item.staffId,
        })),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#10B981' },
              { offset: 1, color: '#047857' },
            ],
          },
          borderRadius: [6, 6, 0, 0],
        },
        emphasis: {
          focus: 'series',
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(16, 185, 129, 0.5)',
          },
        },
      },
    ],
  };
}

/**
 * 요일별 시간대 히트맵 차트 옵션
 * @param {Array} heatmapData - 히트맵 데이터
 * @param {Array} hourLabels - 시간 라벨 배열
 * @returns {Object} ECharts 옵션
 */
export function createHeatmapChartOption(heatmapData, hourLabels = []) {
  if (!heatmapData || heatmapData.length === 0) {
    return createEmptyChartOption('요일별 시간대 예약율 데이터가 없습니다');
  }

  // 기본 시간 라벨 (9시-20시)
  const defaultHours = [
    '9시',
    '10시',
    '11시',
    '12시',
    '13시',
    '14시',
    '15시',
    '16시',
    '17시',
    '18시',
    '19시',
    '20시',
  ];

  // 동적 시간 라벨 사용 (데이터가 있으면 사용, 없으면 기본값)
  const hours = hourLabels && hourLabels.length > 0 ? hourLabels : defaultHours;
  const days = ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'];

  // 최대값 계산 (visualMap 범위 설정용)
  const maxValue = heatmapData.reduce((max, item) => {
    const value = Array.isArray(item) ? item[2] : item;
    return Math.max(max, Number(value) || 0);
  }, 0);

  // visualMap 최대값 설정 - 동적 조정
  let visualMapMax;
  if (maxValue <= 100) {
    visualMapMax = 100;
  } else if (maxValue <= 200) {
    visualMapMax = 200;
  } else if (maxValue <= 300) {
    visualMapMax = 300;
  } else {
    visualMapMax = Math.ceil(maxValue / 100) * 100;
  }

  // 색상 구간 설정
  const colorStops = [
    '#E8F4FD', // 0% - 매우 낮음
    '#BFDBFE', // 25% - 낮음
    '#60A5FA', // 50% - 보통
    '#3B82F6', // 75% - 높음
    '#1E40AF', // 100% - 높음
  ];

  // 100% 초과 시 더 진한 색상 추가
  if (visualMapMax > 100) {
    colorStops.push('#1E3A8A'); // 150% - 매우 높음
    colorStops.push('#172554'); // 200% - 최고
  }

  return {
    tooltip: {
      position: 'top',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      borderRadius: 8,
      textStyle: {
        color: '#374151',
        fontSize: 12,
      },
      padding: [12, 16],
      formatter: params => {
        if (!params || !params.data || !Array.isArray(params.data)) {
          return '데이터 없음';
        }

        const [hourIndex, dayIndex, value] = params.data;
        const hour = hours[hourIndex] || '시간 없음';
        const day = days[dayIndex] || '요일 없음';
        const rate = typeof value === 'number' ? value.toFixed(1) : '0.0';

        return `<div style="font-weight: 600; margin-bottom: 8px; color: #374151;">${day} ${hour}</div>
                <div style="color: #374151;">예약율: <strong style="color: #3B82F6;">${rate}%</strong></div>`;
      },
    },
    grid: {
      height: '65%',
      top: '10%',
      left: '12%',
      right: '10%',
      bottom: '25%',
    },
    xAxis: {
      type: 'category',
      data: hours,
      splitArea: {
        show: true,
        areaStyle: {
          color: ['rgba(250,250,250,0.1)', 'rgba(200,200,200,0.1)'],
        },
      },
      axisLabel: {
        color: '#6B7280',
        fontSize: 10,
        margin: 8,
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
    },
    yAxis: {
      type: 'category',
      data: days,
      splitArea: {
        show: true,
        areaStyle: {
          color: ['rgba(250,250,250,0.1)', 'rgba(200,200,200,0.1)'],
        },
      },
      axisLabel: {
        color: '#6B7280',
        fontSize: 11,
        margin: 8,
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
    },
    visualMap: {
      min: 0,
      max: visualMapMax,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '8%',
      itemWidth: 15,
      itemHeight: 180,
      inRange: {
        color: colorStops,
      },
      text: [`${visualMapMax}%`, '0%'],
      textStyle: {
        color: '#6B7280',
        fontSize: 11,
      },
      formatter: value => `${value}%`,
    },
    series: [
      {
        type: 'heatmap',
        data: heatmapData,
        label: {
          show: true,
          formatter: params => {
            const value = params.value && params.value[2];
            return typeof value === 'number' ? `${value.toFixed(1)}%` : '0%';
          },
          fontSize: 9,
          color: '#FFFFFF',
          fontWeight: 'bold',
          textShadowColor: 'rgba(0, 0, 0, 0.8)',
          textShadowBlur: 2,
        },
        itemStyle: {
          borderRadius: 4,
          borderColor: '#FFFFFF',
          borderWidth: 1,
        },
        emphasis: {
          itemStyle: {
            borderColor: '#374151',
            borderWidth: 2,
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.3)',
          },
          label: {
            fontSize: 11,
            fontWeight: 'bold',
          },
        },
      },
    ],
  };
}

/**
 * 일별 방문객 성별 차트 옵션 생성 (누적 막대 그래프)
 * @param {Array} dailyVisitorData - 일별 방문객 데이터
 * @returns {Object} ECharts 옵션
 */
export function createDailyVisitorChartOption(dailyVisitorData) {
  if (!dailyVisitorData || dailyVisitorData.length === 0) {
    return createEmptyChartOption('일별 방문객 데이터가 없습니다');
  }

  const maleColor = '#4F46E5'; // 남성 - 인디고
  const femaleColor = '#EC4899'; // 여성 - 핑크

  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderWidth: 0,
      borderRadius: 12,
      textStyle: {
        color: '#374151',
        fontSize: 12,
      },
      padding: [12, 16],
      extraCssText: 'box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1); backdrop-filter: blur(10px);',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(156, 163, 175, 0.2)',
        },
      },
      formatter: function (params) {
        if (!params || params.length === 0) return '';
        const axisValue = params[0].axisValue;
        const maleValue = params.find(p => p.seriesName === '남성')?.value || 0;
        const femaleValue = params.find(p => p.seriesName === '여성')?.value || 0;
        const total = maleValue + femaleValue;

        return `<div style="font-weight: 600; margin-bottom: 8px;">${axisValue}</div>
                <div style="margin-bottom: 4px;">남성: <strong>${maleValue}명</strong></div>
                <div style="margin-bottom: 4px;">여성: <strong>${femaleValue}명</strong></div>
                <div>총 방문객: <strong>${total}명</strong></div>`;
      },
    },
    legend: {
      data: ['남성', '여성'],
      top: 20,
      left: 'center',
      itemGap: 20,
      textStyle: {
        fontSize: 12,
        fontWeight: '500',
        color: '#374151',
      },
      itemWidth: 12,
      itemHeight: 12,
    },
    grid: {
      top: 80,
      left: 60,
      right: 50,
      bottom: 60,
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: dailyVisitorData.map(item => {
        // 날짜 형식을 간단하게 변환 (MM/DD)
        if (item.displayDate) {
          const date = new Date(item.date || item.displayDate);
          if (!isNaN(date.getTime())) {
            return `${date.getMonth() + 1}/${date.getDate()}`;
          }
        }
        return item.label || item.displayDate;
      }),
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
        margin: 15,
        // interval을 제거하여 ECharts가 자동으로 적절한 간격으로 라벨 표시
        rotate: dailyVisitorData.length > 20 ? 45 : 0,
      },
    },
    yAxis: {
      type: 'value',
      name: '방문객 수 (명)',
      nameTextStyle: {
        color: '#6B7280',
        fontSize: 12,
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
        formatter: '{value}명',
      },
      splitLine: {
        lineStyle: {
          color: '#F3F4F6',
          type: 'dashed',
          opacity: 0.5,
        },
      },
    },
    series: [
      {
        name: '남성',
        type: 'bar',
        stack: '방문객',
        data: dailyVisitorData.map(item => item.male || item.maleVisitors || 0),
        itemStyle: {
          color: maleColor,
          borderRadius: [0, 0, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: '#3730A3',
            shadowBlur: 10,
            shadowColor: 'rgba(79, 70, 229, 0.3)',
          },
        },
      },
      {
        name: '여성',
        type: 'bar',
        stack: '방문객',
        data: dailyVisitorData.map(item => item.female || item.femaleVisitors || 0),
        itemStyle: {
          color: femaleColor,
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          itemStyle: {
            color: '#BE185D',
            shadowBlur: 10,
            shadowColor: 'rgba(236, 72, 153, 0.3)',
          },
        },
      },
    ],
  };
}
