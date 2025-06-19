export default [
  {
    path: '/customer/list',
    name: 'CustomerList',
    component: () => import('./views/CustomerListView.vue'),
    meta: { title: '고객 목록' },
  },
];
