export const userRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/features/users/views/LoginView.vue'),
  },
];
