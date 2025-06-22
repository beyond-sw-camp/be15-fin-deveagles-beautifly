export const scheduleRoutes = [
  {
    path: '/reserve/designer',
    name: 'reserveDesigner',
    component: () => import('@/features/schedules/views/DesignerListView.vue'),
  },

  {
    path: '/reserve/designer/:id',
    name: 'reserve',
    component: () => import('@/features/schedules/views/ReserveView.vue'),
  },
];
