// Components
export { default as CouponList } from './views/CouponList.vue';
export { default as CouponForm } from './components/CouponForm.vue';
export { default as CouponDetailModal } from './components/CouponDetailModal.vue';
export { default as CouponSelectorModal } from './components/CouponSelectorModal.vue';
export { default as CompactCouponSelector } from './components/CompactCouponSelector.vue';

// API
export { default as couponsAPI } from './api/coupons.js';

// Composables
export { useCouponValidation } from './composables/useCouponValidation.js';

// Routes
export { default as couponRoutes } from './routes.js';
