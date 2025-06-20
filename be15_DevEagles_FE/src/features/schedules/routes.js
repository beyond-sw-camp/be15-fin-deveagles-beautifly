export const scheduleRoutes = [
  {
    path: '/reservation/designer',
    name: 'reservationDesigner',
    component: () => import('@/features/schedules/views/DesignerListView.vue'),
  },
];
