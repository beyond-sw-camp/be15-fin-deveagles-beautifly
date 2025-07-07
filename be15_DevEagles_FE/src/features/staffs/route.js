export const staffRoutes = [
  {
    path: '/settings/staff/:staffId',
    name: 'StaffDetail',
    component: () => import('@/features/staffs/views/StaffDetailView.vue'),
    props: true, //params 컴포넌트 자동 전달
  },
  {
    path: '/settings/staff/regist',
    name: 'StaffRegist',
    component: () => import('@/features/staffs/views/StaffRegistView.vue'),
    props: true, //params 컴포넌트 자동 전달
  },
];
