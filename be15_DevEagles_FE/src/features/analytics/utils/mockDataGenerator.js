/**
 * 뷰티샵 매출 분석용 목 데이터 생성기
 * 실제 API 연동 전까지 사용하는 가짜 데이터
 */

/**
 * 일별 매출 데이터 생성
 * @param {number} days - 생성할 일수
 * @returns {Array} 일별 매출 데이터 배열
 */
export function generateDailySalesData(days = 30) {
  const today = new Date();
  const dailySales = [];

  for (let i = days; i >= 0; i--) {
    const date = new Date(today.getTime() - i * 24 * 60 * 60 * 1000);
    const hairSales = Math.floor(Math.random() * 200000) + 50000;
    const nailSales = Math.floor(Math.random() * 150000) + 30000;
    const skincareSales = Math.floor(Math.random() * 180000) + 40000;
    const makeupSales = Math.floor(Math.random() * 120000) + 20000;
    const otherSales = Math.floor(Math.random() * 80000) + 15000;

    dailySales.push({
      date: date.toISOString().split('T')[0],
      hair: hairSales,
      nail: nailSales,
      skincare: skincareSales,
      makeup: makeupSales,
      other: otherSales,
      total: hairSales + nailSales + skincareSales + makeupSales + otherSales,
      orders: Math.floor(Math.random() * 25) + 8,
    });
  }

  return dailySales;
}

/**
 * 월별 매출 데이터 생성
 * @returns {Array} 월별 매출 데이터 배열
 */
export function generateMonthlySalesData() {
  return [
    { month: '2024-01', sales: 8500000, orders: 280 },
    { month: '2024-02', sales: 9200000, orders: 310 },
    { month: '2024-03', sales: 7800000, orders: 265 },
    { month: '2024-04', sales: 10600000, orders: 340 },
    { month: '2024-05', sales: 11200000, orders: 365 },
    { month: '2024-06', sales: 12800000, orders: 410 },
    { month: '2024-07', sales: 14200000, orders: 445 },
    { month: '2024-08', sales: 13600000, orders: 425 },
    { month: '2024-09', sales: 15200000, orders: 485 },
    { month: '2024-10', sales: 16100000, orders: 520 },
    { month: '2024-11', sales: 17500000, orders: 560 },
    { month: '2024-12', sales: 19800000, orders: 625 },
  ];
}

/**
 * 서비스별 매출 데이터 생성
 * @returns {Array} 서비스별 매출 데이터 배열
 */
export function generateCategorySalesData() {
  return [
    { name: '헤어 서비스', value: 6800000, percentage: 38.2 },
    { name: '네일 아트', value: 4200000, percentage: 23.6 },
    { name: '스킨케어', value: 3800000, percentage: 21.3 },
    { name: '메이크업', value: 2100000, percentage: 11.8 },
    { name: '기타 서비스', value: 900000, percentage: 5.1 },
  ];
}

/**
 * 인기 서비스 데이터 생성
 * @returns {Array} 인기 서비스 데이터 배열
 */
export function generateTopProductsData() {
  return [
    { name: '헤어 커트 + 드라이', sales: 1800000, quantity: 120 },
    { name: '젤 네일 + 아트', sales: 1400000, quantity: 85 },
    { name: '딥 클렌징 페이셜', sales: 1200000, quantity: 45 },
    { name: '펌 + 트리트먼트', sales: 1100000, quantity: 32 },
    { name: '브라이덜 메이크업', sales: 900000, quantity: 15 },
  ];
}

/**
 * 전체 목 데이터 생성
 * @returns {Object} 모든 매출 분석 데이터
 */
export function generateMockAnalyticsData() {
  const dailySales = generateDailySalesData();
  const monthlySales = generateMonthlySalesData();
  const categorySales = generateCategorySalesData();
  const topProducts = generateTopProductsData();

  const totalSales = dailySales.reduce((sum, day) => sum + day.total, 0);
  const dailyAverage = totalSales / dailySales.length;

  return {
    dailySales,
    monthlySales,
    categorySales,
    topProducts,
    totalSales,
    monthlyGrowth: 15.8, // 고정값, 실제로는 계산 필요
    dailyAverage,
  };
}
