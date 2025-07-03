import { ref, computed } from 'vue';
import { createLogger } from '@/plugins/logger.js';
import couponsAPI from '../api/coupons.js';

const logger = createLogger('CouponValidation');

export function useCouponValidation() {
  // State
  const isValidating = ref(false);
  const isApplying = ref(false);
  const validationResult = ref(null);
  const applicationResult = ref(null);
  const lastError = ref(null);

  // Computed
  const isValid = computed(() => {
    return validationResult.value?.valid === true;
  });

  const canApplyCoupon = computed(() => {
    return isValid.value && !isApplying.value;
  });

  const discountInfo = computed(() => {
    if (!applicationResult.value || !applicationResult.value.success) {
      return null;
    }

    return {
      discountRate: applicationResult.value.discountRate,
      discountAmount: applicationResult.value.discountAmount,
      finalAmount: applicationResult.value.finalAmount,
    };
  });

  // Methods
  const validateCoupon = async validationData => {
    try {
      isValidating.value = true;
      lastError.value = null;

      logger.info('쿠폰 검증 시작', validationData);

      const result = await couponsAPI.validateCoupon(validationData);
      validationResult.value = result;

      logger.info('쿠폰 검증 완료', result);
      return result;
    } catch (error) {
      logger.error('쿠폰 검증 실패', error);
      lastError.value = error.message;
      validationResult.value = { valid: false, errorMessage: error.message };
      throw error;
    } finally {
      isValidating.value = false;
    }
  };

  const applyCouponSimulation = async applicationData => {
    try {
      isApplying.value = true;
      lastError.value = null;

      logger.info('쿠폰 적용 시뮬레이션 시작', applicationData);

      const result = await couponsAPI.applyCouponSimulation(applicationData);
      applicationResult.value = result;

      logger.info('쿠폰 적용 시뮬레이션 완료', result);
      return result;
    } catch (error) {
      logger.error('쿠폰 적용 시뮬레이션 실패', error);
      lastError.value = error.message;
      applicationResult.value = { success: false, errorMessage: error.message };
      throw error;
    } finally {
      isApplying.value = false;
    }
  };

  const validateAndApplyCoupon = async couponData => {
    try {
      // 1단계: 쿠폰 검증
      const validationResult = await validateCoupon({
        couponCode: couponData.couponCode,
        shopId: couponData.shopId,
        staffId: couponData.staffId,
        primaryItemId: couponData.primaryItemId,
        secondaryItemId: couponData.secondaryItemId,
      });

      if (!validationResult.valid) {
        throw new Error(validationResult.errorMessage);
      }

      // 2단계: 쿠폰 적용 시뮬레이션
      const applicationResult = await applyCouponSimulation({
        ...couponData,
        originalAmount: couponData.originalAmount,
      });

      return {
        validation: validationResult,
        application: applicationResult,
      };
    } catch (error) {
      logger.error('쿠폰 검증 및 적용 실패', error);
      throw error;
    }
  };

  const reset = () => {
    validationResult.value = null;
    applicationResult.value = null;
    lastError.value = null;
  };

  return {
    // State
    isValidating,
    isApplying,
    validationResult,
    applicationResult,
    lastError,

    // Computed
    isValid,
    canApplyCoupon,
    discountInfo,

    // Methods
    validateCoupon,
    applyCouponSimulation,
    validateAndApplyCoupon,
    reset,
  };
}
