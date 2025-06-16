export const userRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/features/users/views/LoginView.vue'),
  },
  {
    path: '/sign-up',
    name: 'SignUp',
    component: () => import('@/features/users/views/SignUpView.vue'),
  },
];
