export const scheduleRoutes = [
  {
    path: '/schedule/calendar',
    name: 'ScheduleCalendar',
    component: () => import('@/features/schedule/views/ScheduleCalendarView.vue'),
    meta: {
      layout: 'default',
      requiresAuth: true,
      title: '예약 캘린더',
      description: '예약, 일정, 휴무를 한눈에 관리할 수 있어요.',
    },
  },
];
