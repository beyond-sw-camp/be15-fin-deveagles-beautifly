/**
 * ECharts 차트 옵션 설정
 * 차트별 옵션을 분리하여 관리
 */

/**
 * 일별 매출 차트 기본 옵션
 * @param {Array} dailySalesData - 일별 매출 데이터
 * @returns {Object} ECharts 옵션
 */
export function createDailySalesChartOption(dailySalesData) {
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
        type: 'line',
        lineStyle: {
          color: 'rgba(156, 163, 175, 0.3)',
          width: 1,
          type: 'dashed',
        },
      },
      formatter: function (params) {
        let result = `<div style="font-weight: 600; margin-bottom: 8px;">${params[0].axisValue}</div>`;
        let total = 0;
        params.forEach(param => {
          total += param.value;
        });
        result += `<div style="font-size: 11px; color: #6B7280; margin-bottom: 8px;">총 매출: ${total.toLocaleString()}원</div>`;
        params.forEach(param => {
          const percentage = ((param.value / total) * 100).toFixed(1);
          result += `<div style="display: flex; align-items: center; margin-bottom: 4px;">
            <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: ${param.color}; margin-right: 8px;"></span>
            <span style="flex: 1;">${param.seriesName}</span>
            <span style="font-weight: 600; margin-left: 12px;">${param.value.toLocaleString()}원 (${percentage}%)</span>
          </div>`;
        });
        return result;
      },
    },
    grid: {
      top: 80,
      left: 50,
      right: 50,
      bottom: 50,
      containLabel: true,
    },
    legend: {
      data: ['헤어 서비스', '네일 아트', '스킨케어', '메이크업', '기타'],
      top: 35,
      left: 'center',
      itemGap: 20,
      textStyle: {
        color: '#4B5563',
        fontSize: 11,
        fontWeight: '500',
      },
      itemWidth: 12,
      itemHeight: 12,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dailySalesData.map(item => {
        const date = new Date(item.date);
        return `${date.getMonth() + 1}/${date.getDate()}`;
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
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
        formatter: value => {
          if (value >= 1000000) return `${(value / 1000000).toFixed(1)}M`;
          if (value >= 1000) return `${(value / 1000).toFixed(0)}K`;
          return value;
        },
      },
      splitLine: {
        lineStyle: {
          color: '#F3F4F6',
          type: 'dashed',
          opacity: 0.5,
        },
      },
    },
    series: createDailySalesSeries(dailySalesData),
  };
}

/**
 * 월별 매출 차트 기본 옵션
 * @param {Array} monthlySalesData - 월별 매출 데이터
 * @returns {Object} ECharts 옵션
 */
export function createMonthlySalesChartOption(monthlySalesData) {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
      formatter: params => {
        const param = params[0];
        return `<div style="font-weight: 600; margin-bottom: 8px;">${param.axisValue}</div>
                <div style="display: flex; align-items: center;">
                  <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: ${param.color}; margin-right: 8px;"></span>
                  <span style="flex: 1;">월 매출</span>
                  <span style="font-weight: 600; margin-left: 12px;">${param.value.toLocaleString()}원</span>
                </div>`;
      },
    },
    xAxis: {
      type: 'category',
      data: monthlySalesData.map(item => {
        const [year, month] = item.month.split('-');
        return `${month}월`;
      }),
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisTick: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280' },
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisTick: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: {
        color: '#6B7280',
        formatter: value => `${(value / 1000000).toFixed(1)}M`,
      },
      splitLine: { lineStyle: { color: '#F3F4F6' } },
    },
    series: [
      {
        type: 'bar',
        data: monthlySalesData.map(item => item.sales),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#FF6B6B' },
              { offset: 0.5, color: '#4ECDC4' },
              { offset: 1, color: '#45B7D1' },
            ],
          },
          borderRadius: [6, 6, 0, 0],
        },
      },
    ],
  };
}

/**
 * 서비스별 매출 파이 차트 옵션
 * @param {Array} categorySalesData - 서비스별 매출 데이터
 * @returns {Object} ECharts 옵션
 */
export function createCategorySalesChartOption(categorySalesData) {
  return {
    color: ['#FFD700', '#FF69B4', '#00BFFF', '#1E90FF', '#00CED1'],
    tooltip: {
      trigger: 'item',
      formatter: params => {
        const percentage = params.percent;
        const value = params.value;
        return `<div style="font-weight: 600; margin-bottom: 8px;">${params.name}</div>
                <div style="display: flex; align-items: center;">
                  <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: ${params.color}; margin-right: 8px;"></span>
                  <span style="flex: 1;">매출</span>
                  <span style="font-weight: 600; margin-left: 12px;">${value.toLocaleString()}원 (${percentage}%)</span>
                </div>`;
      },
    },
    legend: {
      bottom: '10px',
      left: 'center',
      itemGap: 20,
      textStyle: {
        fontSize: 12,
        fontWeight: '500',
      },
      itemWidth: 12,
      itemHeight: 12,
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        data: categorySalesData.map(item => ({
          name: item.name,
          value: item.value,
        })),
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          formatter: '{b}: {d}%',
          fontSize: 12,
          fontWeight: 'bold',
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };
}

/**
 * 일별 매출 시리즈 데이터 생성
 * @param {Array} dailySalesData - 일별 매출 데이터
 * @returns {Array} 시리즈 배열
 */
function createDailySalesSeries(dailySalesData) {
  const services = [
    { name: '헤어 서비스', color: '#FFD700', dataKey: 'hair' },
    { name: '네일 아트', color: '#FF69B4', dataKey: 'nail' },
    { name: '스킨케어', color: '#00BFFF', dataKey: 'skincare' },
    { name: '메이크업', color: '#1E90FF', dataKey: 'makeup' },
    { name: '기타', color: '#00CED1', dataKey: 'other' },
  ];

  return services.map(service => ({
    name: service.name,
    type: 'line',
    stack: 'Total',
    smooth: 0.6,
    symbol: 'none',
    lineStyle: {
      width: 0,
      color: service.color,
    },
    itemStyle: {
      color: service.color,
    },
    data: dailySalesData.map(item => item[service.dataKey]),
    areaStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: createGradientColors(service.color),
      },
    },
  }));
}

/**
 * 그라데이션 색상 생성
 * @param {string} baseColor - 기본 색상
 * @returns {Array} 그라데이션 색상 배열
 */
function createGradientColors(baseColor) {
  const colorMap = {
    '#FFD700': [
      { offset: 0, color: 'rgba(255, 215, 0, 0.9)' },
      { offset: 0.3, color: 'rgba(255, 193, 7, 0.8)' },
      { offset: 0.7, color: 'rgba(255, 152, 0, 0.7)' },
      { offset: 1, color: 'rgba(255, 111, 97, 0.6)' },
    ],
    '#FF69B4': [
      { offset: 0, color: 'rgba(255, 105, 180, 0.9)' },
      { offset: 0.3, color: 'rgba(255, 20, 147, 0.8)' },
      { offset: 0.7, color: 'rgba(199, 21, 133, 0.7)' },
      { offset: 1, color: 'rgba(147, 51, 234, 0.6)' },
    ],
    '#00BFFF': [
      { offset: 0, color: 'rgba(0, 191, 255, 0.9)' },
      { offset: 0.3, color: 'rgba(30, 144, 255, 0.8)' },
      { offset: 0.7, color: 'rgba(65, 105, 225, 0.7)' },
      { offset: 1, color: 'rgba(75, 0, 130, 0.6)' },
    ],
    '#1E90FF': [
      { offset: 0, color: 'rgba(30, 144, 255, 0.9)' },
      { offset: 0.3, color: 'rgba(70, 130, 180, 0.8)' },
      { offset: 0.7, color: 'rgba(100, 149, 237, 0.7)' },
      { offset: 1, color: 'rgba(135, 206, 250, 0.6)' },
    ],
    '#00CED1': [
      { offset: 0, color: 'rgba(0, 206, 209, 0.9)' },
      { offset: 0.3, color: 'rgba(72, 209, 204, 0.8)' },
      { offset: 0.7, color: 'rgba(175, 238, 238, 0.7)' },
      { offset: 1, color: 'rgba(224, 255, 255, 0.6)' },
    ],
  };

  return colorMap[baseColor] || colorMap['#FFD700'];
}
