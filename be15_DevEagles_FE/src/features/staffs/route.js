export const staffRoutes = [
  {
    path: '/staff/:id',
    name: 'StaffDetail',
    component: () => import('@/features/staffs/views/StaffDetailView.vue'),
    props: true, //params 컴포넌트 자동 전달
  },
];
