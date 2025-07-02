import api from '@/plugins/axios';

// 일정 캘린더 전체 조회 API
export const getCalendarSchedules = async ({
  from,
  to,
  customerKeyword,
  itemKeyword,
  staffId,
  scheduleType,
}) => {
  const response = await api.get('/schedules/calendar', {
    params: {
      from,
      to,
      customerKeyword,
      itemKeyword,
      staffId,
      scheduleType,
    },
  });

  return response.data.data;
};
