export default [
  {
    path: '/coupons',
    name: 'CouponList',
    component: () => import('./views/CouponList.vue'),
    meta: {
      title: '쿠폰 관리',
      requiresAuth: true,
    },
  },
];
