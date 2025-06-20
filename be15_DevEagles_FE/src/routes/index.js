import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import { userRoutes } from '@/features/users/route.js';
import { staffRoutes } from '@/features/staffs/route.js';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  // 예약 관련 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/schedule/calendar',
    name: 'ScheduleCalendar',
    component: () => import('@/features/schedules/views/ScheduleCalendarView.vue'),
  },
  {
    path: '/reservation/list',
    name: 'ReservationList',
    component: () => import('@/features/schedules/views/ReservationListView.vue'),
  },
  {
    path: '/schedule/plan',
    name: 'ReservationSchedule',
    component: () => import('@/features/schedules/views/SchedulePlanView.vue'),
  },
  {
    path: '/schedule/leave',
    name: 'ScheduleLeave',
    component: () => import('@/features/schedules/views/ScheduleLeaveView.vue'),
  },
  {
    path: '/reservation/requests',
    name: 'ReservationRequests',
    component: () => import('@/features/schedules/views/ReservationRequestsView.vue'),
  },
  {
    path: '/reservation/history',
    name: 'ReservationHistory',
    component: () => import('@/features/schedules/views/ReservationHistoryView.vue'),
  },
  // 고객 관리 라우트 - 임시로 모두 Home으로 라우팅
  {
    path: '/customer/list',
    name: 'CustomerList',
    component: () => import('@/features/customer/views/CustomerListView.vue'),
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
    component: () => import('@/features/staffsales/view/StaffSalesListView.vue'),
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
    component: () => import('@/features/membership/view/MembershipView.vue'),
  },
  // 데이터 분석 라우트
  {
    path: '/analytics/sales',
    name: 'SalesAnalytics',
    component: () => import('@/features/analytics/views/SalesAnalytics.vue'),
  },
  {
    path: '/analytics/usage',
    name: 'AnalyticsUsage',
    component: () => import('@/features/analytics/views/UsageAnalytics.vue'),
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
    component: () => import('@/features/messages/views/SettingsView.vue'),
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
    component: () => import('@/features/users/views/StoreSettingView.vue'),
  },
  {
    path: '/settings/reservation',
    name: 'SettingsReservation',
    component: () => import('@/features/schedules/views/SettingsReservation.vue'),
  },
  {
    path: '/settings/staff',
    name: 'SettingsStaff',
    component: () => import('@/features/staffs/views/StaffListView.vue'),
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
    component: () => import('@/features/users/views/AccountSettingView.vue'),
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/features/users/views/ProfileSettingView.vue'),
  },
  ...userRoutes, // 추후에 밖으로 빼기
  ...staffRoutes,
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
