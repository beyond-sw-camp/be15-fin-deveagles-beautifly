import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import { userRoutes } from '@/features/users/route.js';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  // 예약 관련 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/reservation/calendar',
    name: 'ReservationCalendar',
    component: () => import('@/features/schedule/views/ScheduleCalendarView.vue'),
  },
  {
    path: '/reservation/list',
    name: 'ReservationList',
    component: () => import('@/features/schedule/views/ReservationListView.vue'),
  },
  {
    path: '/reservation/schedule',
    name: 'ReservationSchedule',
    component: Home,
  },
  {
    path: '/reservation/holiday',
    name: 'ReservationHoliday',
    component: Home,
  },
  {
    path: '/reservation/requests',
    name: 'ReservationRequests',
    component: () => import('@/features/schedule/views/ReservationRequestsView.vue'),
  },
  {
    path: '/reservation/history',
    name: 'ReservationHistory',
    component: () => import('@/features/schedule/views/ReservationHistoryView.vue'),
  },
  // 고객 관리 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/customer/list',
    name: 'CustomerList',
    component: Home,
  },
  {
    path: '/customer/prepaid',
    name: 'CustomerPrepaid',
    component: Home,
  },
  // 매출 관리 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/sales/management',
    name: 'SalesManagement',
    component: Home,
  },
  {
    path: '/sales/staff',
    name: 'SalesStaff',
    component: Home,
  },
  // 상품 관리 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/item/service',
    name: 'ItemService',
    component: () => import('@/features/item/view/ItemView.vue'),
  },
  {
    path: '/item/membership',
    name: 'ItemMembership',
    component: Home,
  },
  // 데이터 분석 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/analytics/usage',
    name: 'AnalyticsUsage',
    component: Home,
  },
  {
    path: '/analytics/revenue',
    name: 'AnalyticsRevenue',
    component: Home,
  },
  // 문자 관리 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/message/history',
    name: 'MessageHistory',
    component: () => import('@/features/messages/views/MessagesListView.vue'),
  },
  {
    path: '/message/templates',
    name: 'MessageTemplates',
    component: () => import('@/features/messages/views/TemplateListView.vue'),
  },
  {
    path: '/message/settings',
    name: 'MessageSettings',
    component: Home,
  },
  {
    path: '/message/ab-test',
    name: 'MessageAbTest',
    component: Home,
  },
  // 워크플로우 라우트
  {
    path: '/workflows',
    name: 'WorkflowList',
    component: () => import('@/features/workflows/views/WorkflowList.vue'),
  },
  {
    path: '/campaigns',
    name: 'CampaignList',
    component: () => import('@/features/campaigns/views/CampaignList.vue'),
  },
  {
    path: '/coupons',
    name: 'CouponList',
    component: () => import('@/features/coupons/views/CouponList.vue'),
  },
  {
    path: '/profile-link',
    name: 'ProfileLink',
    component: Home,
  },
  // 매장 설정 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/settings/store',
    name: 'SettingsStore',
    component: Home,
  },
  {
    path: '/settings/reservation',
    name: 'SettingsReservation',
    component: Home,
  },
  {
    path: '/settings/staff',
    name: 'SettingsStaff',
    component: Home,
  },
  {
    path: '/settings/customer-grade',
    name: 'SettingsCustomerGrade',
    component: Home,
  },
  {
    path: '/settings/customer-tag',
    name: 'SettingsCustomerTag',
    component: Home,
  },
  {
    path: '/settings/account',
    name: 'SettingsAccount',
    component: Home,
  },
  // 프로필 라우트 - 임시로 Home으로 라우팅
  {
    path: '/profile',
    name: 'Profile',
    component: Home,
  },
  ...userRoutes, // 추후에 밖으로 빼기
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
