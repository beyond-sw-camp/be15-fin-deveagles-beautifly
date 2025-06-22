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
  {
    path: '/verify-owner',
    name: 'VerifyOwner',
    component: () => import('@/features/users/views/EmailVerifyView.vue'),
  },
  {
    path: '/verify-reset',
    name: 'ResetPwd',
    component: () => import('@/features/users/views/EmailVerifyPwdView.vue'),
  },
  {
    path: '/edit-pwd',
    name: 'EditPwd',
    component: () => import('@/features/users/views/EmailPwdEditView.vue'),
  },
];
