import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import { staffRoutes } from '@/features/staffs/route.js';
import { scheduleRoutes } from '@/features/schedules/routes.js';
import { userRoutes } from '@/features/users/route.js';

const routes = [
  // 레이아웃이 없는 관련 페이지 (최상위)
  // {
  //   path: '/login',
  //   name: 'Login',
  //   component: () => import('@/features/users/views/LoginView.vue'),
  // },
  // {
  //   path: '/sign-up',
  //   name: 'SignUp',
  //   component: () => import('@/features/users/views/SignUpView.vue'),
  // },

  // 스케줄 관련 라우트 (레이아웃 없음)
  ...userRoutes,
  ...scheduleRoutes,
  ...staffRoutes,

  // 레이아웃이 있는 모든 페이지들 (중첩 라우팅)
  {
    path: '/',
    component: () => import('@/components/layout/TheLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: Home,
      },
      {
        path: 'schedule/calendar',
        name: 'ScheduleCalendar',
        component: () => import('@/features/schedules/views/ScheduleCalendarView.vue'),
      },
      {
        path: 'reservation/list',
        name: 'ReservationList',
        component: () => import('@/features/schedules/views/ReservationListView.vue'),
      },
      {
        path: 'schedule/plan',
        name: 'ReservationSchedule',
        component: () => import('@/features/schedules/views/SchedulePlanView.vue'),
      },
      {
        path: 'schedule/leave',
        name: 'ScheduleLeave',
        component: () => import('@/features/schedules/views/ScheduleLeaveView.vue'),
      },
      {
        path: 'reservation/requests',
        name: 'ReservationRequests',
        component: () => import('@/features/schedules/views/ReservationRequestsView.vue'),
      },
      {
        path: 'reservation/history',
        name: 'ReservationHistory',
        component: () => import('@/features/schedules/views/ReservationHistoryView.vue'),
      },
      {
        path: 'customer/list',
        name: 'CustomerList',
        component: () => import('@/features/customer/views/CustomerListView.vue'),
      },
      {
        path: 'customer/prepaid',
        name: 'CustomerPrepaid',
        component: Home,
      },
      {
        path: 'sales/management',
        name: 'SalesManagement',
        component: () => import('@/features/sales/views/SalesView.vue'),
      },
      {
        path: 'sales/staff',
        name: 'SalesStaff',
        component: () => import('@/features/staffsales/view/StaffSalesListView.vue'),
      },
      {
        path: 'item/service',
        name: 'ItemService',
        component: () => import('@/features/items/views/ItemView.vue'),
      },
      {
        path: 'item/membership',
        name: 'ItemMembership',
        component: () => import('@/features/membership/view/MembershipView.vue'),
      },
      {
        path: 'analytics/sales',
        name: 'SalesAnalytics',
        component: () => import('@/features/analytics/views/SalesAnalytics.vue'),
      },
      {
        path: 'analytics/usage',
        name: 'AnalyticsUsage',
        component: () => import('@/features/analytics/views/UsageAnalytics.vue'),
      },
      {
        path: 'analytics/revenue',
        name: 'AnalyticsRevenue',
        component: Home,
      },
      {
        path: 'message/history',
        name: 'MessageHistory',
        component: () => import('@/features/messages/views/MessagesListView.vue'),
      },
      {
        path: 'message/templates',
        name: 'MessageTemplates',
        component: () => import('@/features/messages/views/TemplateListView.vue'),
      },
      {
        path: 'message/settings',
        name: 'MessageSettings',
        component: () => import('@/features/messages/views/SettingsView.vue'),
      },
      {
        path: 'message/ab-test',
        name: 'MessageAbTest',
        component: Home,
      },
      {
        path: 'workflows',
        name: 'WorkflowList',
        component: () => import('@/features/workflows/views/WorkflowList.vue'),
      },
      {
        path: 'campaigns',
        name: 'CampaignList',
        component: () => import('@/features/campaigns/views/CampaignList.vue'),
      },
      {
        path: 'campaigns/:id',
        name: 'CampaignDetail',
        component: () => import('@/features/campaigns/views/CampaignDetail.vue'),
      },
      {
        path: 'coupons',
        name: 'CouponList',
        component: () => import('@/features/coupons/views/CouponList.vue'),
      },
      {
        path: 'profile-link',
        name: 'ProfileLink',
        component: Home,
      },
      {
        path: 'settings/store',
        name: 'SettingsStore',
        component: () => import('@/features/users/views/StoreSettingView.vue'),
      },
      {
        path: 'settings/reservation',
        name: 'SettingsReservation',
        component: () => import('@/features/schedules/views/SettingsReservation.vue'),
      },
      {
        path: 'settings/staff',
        name: 'SettingsStaff',
        component: () => import('@/features/staffs/views/StaffListView.vue'),
      },
      {
        path: 'settings/customer-grade',
        name: 'SettingsCustomerGrade',
        component: Home,
      },
      {
        path: 'settings/customer-tag',
        name: 'SettingsCustomerTag',
        component: Home,
      },
      {
        path: 'settings/account',
        name: 'SettingsAccount',
        component: () => import('@/features/users/views/AccountSettingView.vue'),
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/features/users/views/ProfileSettingView.vue'),
      },
      ...staffRoutes.map(route => ({
        ...route,
        path: route.path.startsWith('/') ? route.path.slice(1) : route.path,
      })),
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
