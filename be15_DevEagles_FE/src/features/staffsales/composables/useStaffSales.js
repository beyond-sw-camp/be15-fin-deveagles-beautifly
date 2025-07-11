import dayjs from 'dayjs';

export function useStaffSales() {
  const categoryLabelMap = {
    PRODUCT: '상품-제품',
    SERVICE: '상품-시술',
    SESSION_PASS: '회원권-횟수권',
    PREPAID_PASS: '회원권-선불권',
  };

  const formatToISODate = date => dayjs(date).format('YYYY-MM-DD');

  const getFormattedDates = (searchMode, selectedMonth, selectedRange) => {
    if (searchMode === 'MONTH') {
      return {
        startDate: formatToISODate(selectedMonth),
        endDate: null,
      };
    } else {
      const [start, end] = selectedRange || [];
      return {
        startDate: formatToISODate(start),
        endDate: formatToISODate(end),
      };
    }
  };

  const formatCurrency = value => (typeof value === 'number' ? value.toLocaleString('ko-KR') : '0');

  return {
    categoryLabelMap,
    formatToISODate,
    getFormattedDates,
    formatCurrency,
  };
}
