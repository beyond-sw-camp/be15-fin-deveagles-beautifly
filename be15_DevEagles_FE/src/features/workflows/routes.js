export default [
  {
    path: '/workflows',
    name: 'WorkflowList',
    component: () => import('./views/WorkflowList.vue'),
    meta: {
      title: '마케팅 자동화',
      requiresAuth: true,
    },
  },
  {
    path: '/workflows/create',
    name: 'WorkflowCreate',
    component: () => import('./views/WorkflowCreate.vue'),
    meta: {
      title: '워크플로우 생성',
      requiresAuth: true,
    },
  },
];
