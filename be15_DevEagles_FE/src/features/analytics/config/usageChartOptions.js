/**
 * 예약율 분석 ECharts 차트 옵션 설정
 * 예약율 통계용 차트 옵션들을 분리하여 관리
 */

/**
 * 시간대별 예약율 차트 옵션
 * @param {Array} hourlyUsageData - 시간대별 예약율 데이터
 * @returns {Object} ECharts 옵션
 */
export function createHourlyUsageChartOption(hourlyUsageData) {
  if (!hourlyUsageData || hourlyUsageData.length === 0) {
    return {
      title: {
        text: '데이터가 없습니다',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#9CA3AF',
          fontSize: 14,
        },
      },
    };
  }

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
        type: 'cross',
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
        result += `<div style="font-size: 11px; color: #6B7280; margin-bottom: 8px;">총 예약율: ${total}%</div>`;
        params.forEach(param => {
          const percentage = ((param.value / total) * 100).toFixed(1);
          result += `<div style="display: flex; align-items: center; margin-bottom: 4px;">
            <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: ${param.color}; margin-right: 8px;"></span>
            <span style="flex: 1;">${param.seriesName}</span>
            <span style="font-weight: 600; margin-left: 12px;">${param.value}% (${percentage}%)</span>
          </div>`;
        });
        return result;
      },
    },
    grid: {
      top: 80,
      left: 60,
      right: 50,
      bottom: 60,
      containLabel: true,
    },
    legend: {
      data: ['남성 고객', '여성 고객'],
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
      data: hourlyUsageData.map(item => item.hour),
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
      name: '예약율 (%)',
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
        formatter: '{value}%',
      },
      splitLine: {
        lineStyle: {
          color: '#F3F4F6',
          type: 'dashed',
          opacity: 0.5,
        },
      },
      max: 100,
    },
    series: [
      {
        name: '남성 고객',
        type: 'bar',
        stack: 'gender',
        data: hourlyUsageData.map(item => item.male),
        itemStyle: {
          color: '#3B82F6',
          borderRadius: [0, 0, 0, 0],
        },
        emphasis: {
          focus: 'series',
        },
      },
      {
        name: '여성 고객',
        type: 'bar',
        stack: 'gender',
        data: hourlyUsageData.map(item => item.female),
        itemStyle: {
          color: '#EC4899',
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          focus: 'series',
        },
      },
    ],
  };
}

/**
 * 서비스별 예약율 차트 옵션
 * @param {Array} serviceUsageData - 서비스별 예약율 데이터
 * @returns {Object} ECharts 옵션
 */
export function createServiceUsageChartOption(serviceUsageData) {
  if (!serviceUsageData || serviceUsageData.length === 0) {
    return {
      title: {
        text: '데이터가 없습니다',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#9CA3AF',
          fontSize: 14,
        },
      },
    };
  }

  return {
    title: {
      text: '서비스별 예약율 (남성)',
      left: 'center',
      top: 20,
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#374151',
      },
    },
    color: ['#3B82F6', '#1D4ED8', '#2563EB', '#1E40AF', '#1E3A8A'],
    tooltip: {
      trigger: 'item',
      formatter: params => {
        const data = serviceUsageData[params.dataIndex];
        return `<div style="font-weight: 600; margin-bottom: 8px;">${params.name}</div>
                <div style="margin-bottom: 4px;">남성 예약율: <strong>${params.value}%</strong></div>
                <div style="margin-bottom: 4px;">전체 예약율: ${data.utilization}%</div>
                <div style="margin-bottom: 4px;">사용시간: ${data.usedHours}/${data.totalHours}시간</div>
                <div>평균 소요: ${data.averageDuration}분</div>`;
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
        center: ['50%', '55%'],
        data: serviceUsageData.map(item => ({
          name: item.name,
          value: item.male,
        })),
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          formatter: '{b}\n{d}%',
          fontSize: 11,
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
 * 서비스별 예약율 차트 옵션 (여성)
 * @param {Array} serviceUsageData - 서비스별 예약율 데이터
 * @returns {Object} ECharts 옵션
 */
export function createServiceUsageChartOptionFemale(serviceUsageData) {
  if (!serviceUsageData || serviceUsageData.length === 0) {
    return {
      title: {
        text: '데이터가 없습니다',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#9CA3AF',
          fontSize: 14,
        },
      },
    };
  }

  return {
    title: {
      text: '서비스별 예약율 (여성)',
      left: 'center',
      top: 20,
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#374151',
      },
    },
    color: ['#EC4899', '#DB2777', '#BE185D', '#9D174D', '#831843'],
    tooltip: {
      trigger: 'item',
      formatter: params => {
        const data = serviceUsageData[params.dataIndex];
        return `<div style="font-weight: 600; margin-bottom: 8px;">${params.name}</div>
                <div style="margin-bottom: 4px;">여성 예약율: <strong>${params.value}%</strong></div>
                <div style="margin-bottom: 4px;">전체 예약율: ${data.utilization}%</div>
                <div style="margin-bottom: 4px;">사용시간: ${data.usedHours}/${data.totalHours}시간</div>
                <div>평균 소요: ${data.averageDuration}분</div>`;
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
        center: ['50%', '55%'],
        data: serviceUsageData.map(item => ({
          name: item.name,
          value: item.female,
        })),
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          formatter: '{b}\n{d}%',
          fontSize: 11,
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
 * 직원별 가동률 차트 옵션
 * @param {Array} staffUsageData - 직원별 가동률 데이터
 * @returns {Object} ECharts 옵션
 */
export function createStaffUsageChartOption(staffUsageData) {
  if (!staffUsageData || staffUsageData.length === 0) {
    return {
      title: {
        text: '데이터가 없습니다',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#9CA3AF',
          fontSize: 14,
        },
      },
    };
  }

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
      },
      formatter: params => {
        let result = `<div style="font-weight: 600; margin-bottom: 8px;">${params[0].axisValue}</div>`;
        let total = 0;
        params.forEach(param => {
          total += param.value;
        });
        const data = staffUsageData[params[0].dataIndex];
        result += `<div style="font-size: 11px; color: #6B7280; margin-bottom: 8px;">총 가동률: ${total}%</div>`;
        params.forEach(param => {
          const percentage = ((param.value / total) * 100).toFixed(1);
          result += `<div style="display: flex; align-items: center; margin-bottom: 4px;">
            <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: ${param.color}; margin-right: 8px;"></span>
            <span style="flex: 1;">${param.seriesName}</span>
            <span style="font-weight: 600; margin-left: 12px;">${param.value}% (${percentage}%)</span>
          </div>`;
        });
        result += `<div style="margin-top: 8px; padding-top: 8px; border-top: 1px solid #E5E7EB;">
          <div style="margin-bottom: 4px;">전문분야: ${data.speciality}</div>
          <div style="margin-bottom: 4px;">활동시간: ${data.activeHours}/${data.workingHours}시간</div>
          <div style="margin-bottom: 4px;">예약건수: ${data.appointments}건</div>
          <div>평점: ${data.rating}점</div>
        </div>`;
        return result;
      },
    },
    legend: {
      data: ['남성 고객', '여성 고객'],
      top: 25,
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
    grid: {
      top: 70,
      left: 60,
      right: 40,
      bottom: 60,
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: staffUsageData.map(item => item.name),
      axisLine: {
        lineStyle: { color: '#E5E7EB' },
      },
      axisTick: {
        lineStyle: { color: '#E5E7EB' },
      },
      axisLabel: {
        color: '#6B7280',
        fontSize: 11,
      },
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#6B7280',
        formatter: '{value}%',
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
        name: '남성 고객',
        type: 'bar',
        stack: 'gender',
        data: staffUsageData.map(item => item.male),
        itemStyle: {
          color: '#3B82F6',
          borderRadius: [0, 0, 0, 0],
        },
        emphasis: {
          focus: 'series',
        },
      },
      {
        name: '여성 고객',
        type: 'bar',
        stack: 'gender',
        data: staffUsageData.map(item => item.female),
        itemStyle: {
          color: '#EC4899',
          borderRadius: [6, 6, 0, 0],
        },
        emphasis: {
          focus: 'series',
        },
      },
    ],
  };
}

/**
 * 월별 예약율 추이 차트 옵션
 * @param {Array} monthlyUsageData - 월별 예약율 데이터
 * @returns {Object} ECharts 옵션
 */
export function createMonthlyUsageChartOption(monthlyUsageData) {
  if (!monthlyUsageData || monthlyUsageData.length === 0) {
    return {
      title: {
        text: '데이터가 없습니다',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#9CA3AF',
          fontSize: 14,
        },
      },
    };
  }

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
      },
      formatter: params => {
        let result = `<div style="font-weight: 600; margin-bottom: 8px;">${params[0].axisValue}</div>`;
        let total = 0;
        params.forEach(param => {
          total += param.value;
        });
        result += `<div style="font-size: 11px; color: #6B7280; margin-bottom: 8px;">총 예약율: ${total}%</div>`;
        params.forEach(param => {
          const percentage = ((param.value / total) * 100).toFixed(1);
          result += `<div style="display: flex; align-items: center; margin-bottom: 4px;">
            <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: ${param.color}; margin-right: 8px;"></span>
            <span style="flex: 1;">${param.seriesName}</span>
            <span style="font-weight: 600; margin-left: 12px;">${param.value}% (${percentage}%)</span>
          </div>`;
        });
        return result;
      },
    },
    grid: {
      top: 80,
      left: 60,
      right: 60,
      bottom: 60,
      containLabel: true,
    },
    legend: {
      data: ['남성 고객', '여성 고객'],
      top: 35,
      left: 'center',
      itemGap: 20,
      textStyle: {
        color: '#4B5563',
        fontSize: 11,
        fontWeight: '500',
      },
    },
    xAxis: {
      type: 'category',
      data: monthlyUsageData.map(item => item.month),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#9CA3AF',
        fontSize: 11,
      },
    },
    yAxis: {
      type: 'value',
      name: '예약율 (%)',
      max: 100,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#9CA3AF',
        formatter: '{value}%',
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
        name: '남성 고객',
        type: 'bar',
        stack: 'gender',
        data: monthlyUsageData.map(item => item.male),
        itemStyle: {
          color: '#3B82F6',
          borderRadius: [0, 0, 0, 0],
        },
        emphasis: {
          focus: 'series',
        },
      },
      {
        name: '여성 고객',
        type: 'bar',
        stack: 'gender',
        data: monthlyUsageData.map(item => item.female),
        itemStyle: {
          color: '#EC4899',
          borderRadius: [4, 4, 0, 0],
        },
        emphasis: {
          focus: 'series',
        },
      },
    ],
  };
}

/**
 * 요일별 시간대 히트맵 차트 옵션
 * @param {Array} heatmapData - 히트맵 데이터
 * @returns {Object} ECharts 옵션
 */
export function createHeatmapChartOption(heatmapData) {
  if (!heatmapData || heatmapData.length === 0) {
    return {
      title: {
        text: '데이터가 없습니다',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#9CA3AF',
          fontSize: 14,
        },
      },
    };
  }

  const hours = [
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
  const days = ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'];

  return {
    tooltip: {
      position: 'top',
      formatter: params => {
        const [hourIndex, dayIndex, value] = params.data;
        return `<div style="font-weight: 600; margin-bottom: 8px;">${days[dayIndex]} ${hours[hourIndex]}</div>
                <div>예약율: <strong>${value}%</strong></div>`;
      },
    },
    grid: {
      height: '70%',
      top: '10%',
      left: '15%',
      right: '10%',
    },
    xAxis: {
      type: 'category',
      data: hours,
      splitArea: {
        show: true,
      },
      axisLabel: {
        color: '#6B7280',
        fontSize: 10,
      },
    },
    yAxis: {
      type: 'category',
      data: days,
      splitArea: {
        show: true,
      },
      axisLabel: {
        color: '#6B7280',
        fontSize: 11,
      },
    },
    visualMap: {
      min: 0,
      max: 100,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '5%',
      inRange: {
        color: ['#E8F4FD', '#3B82F6', '#1E40AF'],
      },
      text: ['높음', '낮음'],
      textStyle: {
        color: '#6B7280',
        fontSize: 11,
      },
    },
    series: [
      {
        type: 'heatmap',
        data: heatmapData,
        label: {
          show: true,
          formatter: '{c}%',
          fontSize: 10,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };
}
