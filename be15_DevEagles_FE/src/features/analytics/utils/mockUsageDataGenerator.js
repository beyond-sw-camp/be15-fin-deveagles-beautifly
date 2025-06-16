/**
 * 뷰티샵 예약율 분석용 목 데이터 생성기
 * 실제 API 연동 전까지 사용하는 가짜 데이터
 */

/**
 * 시간대별 예약율 데이터 생성
 * @returns {Array} 시간대별 예약율 데이터 배열
 */
export function generateHourlyUsageData() {
  const hours = [];
  const operatingHours = [9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20];

  operatingHours.forEach(hour => {
    // 피크 시간대 (11-13시, 18-20시)에 높은 예약율
    let baseUtilization;
    if (hour >= 11 && hour <= 13) {
      baseUtilization = 85 + Math.random() * 10; // 85-95%
    } else if (hour >= 18 && hour <= 20) {
      baseUtilization = 80 + Math.random() * 15; // 80-95%
    } else if (hour >= 14 && hour <= 17) {
      baseUtilization = 65 + Math.random() * 15; // 65-80%
    } else {
      baseUtilization = 45 + Math.random() * 20; // 45-65%
    }

    hours.push({
      hour: `${hour}:00`,
      utilization: Math.round(baseUtilization),
      appointments: Math.floor(Math.random() * 8) + 3,
      male: Math.round(baseUtilization * (0.3 + Math.random() * 0.2)), // 30-50%
      female: Math.round(baseUtilization * (0.5 + Math.random() * 0.2)), // 50-70%
      hairService: Math.round(baseUtilization * 0.4),
      nailService: Math.round(baseUtilization * 0.25),
      skincare: Math.round(baseUtilization * 0.2),
      makeup: Math.round(baseUtilization * 0.15),
    });
  });

  return hours;
}

/**
 * 서비스별 예약율 데이터 생성
 * @returns {Array} 서비스별 예약율 데이터 배열
 */
export function generateServiceUsageData() {
  return [
    {
      name: '헤어 서비스',
      utilization: 78,
      male: 25,
      female: 53,
      totalHours: 320,
      usedHours: 250,
      averageDuration: 90,
      bookingRate: 85,
    },
    {
      name: '네일 아트',
      utilization: 65,
      male: 8,
      female: 57,
      totalHours: 280,
      usedHours: 182,
      averageDuration: 75,
      bookingRate: 72,
    },
    {
      name: '스킨케어',
      utilization: 72,
      male: 20,
      female: 52,
      totalHours: 200,
      usedHours: 144,
      averageDuration: 60,
      bookingRate: 80,
    },
    {
      name: '메이크업',
      utilization: 58,
      male: 15,
      female: 43,
      totalHours: 160,
      usedHours: 93,
      averageDuration: 45,
      bookingRate: 68,
    },
    {
      name: '기타 서비스',
      utilization: 45,
      male: 18,
      female: 27,
      totalHours: 120,
      usedHours: 54,
      averageDuration: 30,
      bookingRate: 55,
    },
  ];
}

/**
 * 직원별 가동률 데이터 생성
 * @returns {Array} 직원별 가동률 데이터 배열
 */
export function generateStaffUsageData() {
  return [
    {
      name: '김민지',
      utilization: 87,
      male: 35,
      female: 52,
      workingHours: 8,
      activeHours: 6.96,
      speciality: '헤어',
      appointments: 12,
      rating: 4.8,
    },
    {
      name: '박서연',
      utilization: 82,
      male: 15,
      female: 67,
      workingHours: 8,
      activeHours: 6.56,
      speciality: '네일',
      appointments: 10,
      rating: 4.9,
    },
    {
      name: '이지은',
      utilization: 75,
      male: 25,
      female: 50,
      workingHours: 7,
      activeHours: 5.25,
      speciality: '스킨케어',
      appointments: 8,
      rating: 4.7,
    },
    {
      name: '최하늘',
      utilization: 68,
      male: 20,
      female: 48,
      workingHours: 6,
      activeHours: 4.08,
      speciality: '메이크업',
      appointments: 6,
      rating: 4.6,
    },
    {
      name: '정유진',
      utilization: 73,
      male: 30,
      female: 43,
      workingHours: 8,
      activeHours: 5.84,
      speciality: '헤어',
      appointments: 9,
      rating: 4.5,
    },
  ];
}

/**
 * 월별 예약율 추이 데이터 생성
 * @returns {Array} 월별 예약율 데이터 배열
 */
export function generateMonthlyUsageData() {
  return [
    { month: '1월', utilization: 65, appointments: 380, male: 25, female: 40, growth: -2.1 },
    { month: '2월', utilization: 70, appointments: 420, male: 28, female: 42, growth: 7.7 },
    { month: '3월', utilization: 68, appointments: 395, male: 26, female: 42, growth: -2.9 },
    { month: '4월', utilization: 75, appointments: 450, male: 30, female: 45, growth: 10.3 },
    { month: '5월', utilization: 78, appointments: 485, male: 32, female: 46, growth: 4.0 },
    { month: '6월', utilization: 82, appointments: 520, male: 34, female: 48, growth: 5.1 },
    { month: '7월', utilization: 85, appointments: 565, male: 35, female: 50, growth: 3.7 },
    { month: '8월', utilization: 83, appointments: 545, male: 33, female: 50, growth: -2.4 },
    { month: '9월', utilization: 80, appointments: 510, male: 32, female: 48, growth: -3.6 },
    { month: '10월', utilization: 84, appointments: 555, male: 34, female: 50, growth: 5.0 },
    { month: '11월', utilization: 87, appointments: 590, male: 36, female: 51, growth: 3.6 },
    { month: '12월', utilization: 89, appointments: 625, male: 38, female: 51, growth: 2.3 },
  ];
}

/**
 * 히트맵 데이터 생성 (요일별 시간대)
 * @returns {Array} 히트맵 데이터 배열
 */
export function generateHeatmapData() {
  const days = ['월', '화', '수', '목', '금', '토', '일'];
  const hours = ['9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20'];
  const data = [];

  days.forEach((day, dayIndex) => {
    hours.forEach((hour, hourIndex) => {
      let utilization;

      // 주말(토,일) 패턴
      if (dayIndex >= 5) {
        if (hourIndex >= 2 && hourIndex <= 7) {
          // 11-16시
          utilization = 85 + Math.random() * 10;
        } else {
          utilization = 60 + Math.random() * 20;
        }
      }
      // 평일 패턴
      else {
        if ((hourIndex >= 2 && hourIndex <= 4) || (hourIndex >= 9 && hourIndex <= 11)) {
          // 11-13시, 18-20시
          utilization = 80 + Math.random() * 15;
        } else if (hourIndex >= 5 && hourIndex <= 8) {
          // 14-17시
          utilization = 65 + Math.random() * 15;
        } else {
          utilization = 45 + Math.random() * 20;
        }
      }

      data.push([hourIndex, dayIndex, Math.round(utilization)]);
    });
  });

  return data;
}

/**
 * 피크 시간대 데이터 생성
 * @returns {Array} 피크 시간대 데이터 배열
 */
export function generatePeakHoursData() {
  return [
    { hour: 11, rate: 92 },
    { hour: 12, rate: 89 },
    { hour: 13, rate: 87 },
    { hour: 18, rate: 94 },
    { hour: 19, rate: 91 },
    { hour: 20, rate: 85 },
  ];
}

/**
 * 서비스별 평균 소요시간 데이터 생성
 * @returns {Array} 서비스별 소요시간 데이터 배열
 */
export function generateServiceDurationData() {
  return [
    { name: '헤어 커트', duration: 45 },
    { name: '헤어 컬러링', duration: 120 },
    { name: '네일 아트', duration: 75 },
    { name: '젤 네일', duration: 90 },
    { name: '페이셜 케어', duration: 60 },
    { name: '마사지', duration: 90 },
    { name: '메이크업', duration: 45 },
    { name: '브라이덜 메이크업', duration: 120 },
  ];
}

/**
 * 월별 성장률 데이터 생성
 * @returns {Array} 월별 성장률 데이터 배열
 */
export function generateMonthlyGrowthData() {
  return [
    { month: '10월', rate: 5.2 },
    { month: '11월', rate: 3.8 },
    { month: '12월', rate: 7.1 },
    { month: '1월', rate: -2.3 },
    { month: '2월', rate: 4.5 },
  ];
}

/**
 * 전체 예약율 목 데이터 생성
 * @returns {Object} 모든 예약율 분석 데이터
 */
export function generateMockUsageData() {
  const hourlyUsage = generateHourlyUsageData();
  const serviceUsage = generateServiceUsageData();
  const staffUsage = generateStaffUsageData();
  const monthlyUsage = generateMonthlyUsageData();
  const heatmapData = generateHeatmapData();
  const peakHours = generatePeakHoursData();
  const serviceDuration = generateServiceDurationData();
  const monthlyGrowth = generateMonthlyGrowthData();

  // 전체 예약율 계산
  const overallUtilization = Math.round(
    hourlyUsage.reduce((sum, hour) => sum + hour.utilization, 0) / hourlyUsage.length
  );

  // 직원 평균 가동률 계산
  const staffUtilization = Math.round(
    staffUsage.reduce((sum, staff) => sum + staff.utilization, 0) / staffUsage.length
  );

  // 피크 시간 예약율 계산
  const peakHourUtilization = Math.round(
    peakHours.reduce((sum, peak) => sum + peak.rate, 0) / peakHours.length
  );

  return {
    overallUtilization,
    utilizationGrowth: 8.5,
    averageUsageTime: 72,
    staffUtilization,
    peakHourUtilization,
    hourlyUsage,
    serviceUsage,
    staffUsage,
    monthlyUsage,
    heatmapData,
    peakHours,
    serviceDuration,
    monthlyGrowth,
  };
}
