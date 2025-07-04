import { ref } from 'vue';
import { getLogger, getErrorLogger, getPerformanceLogger } from '@/plugins/LoggerManager.js';
import { useToast } from '@/composables/useToast';
import couponsAPI from '../api/coupons.js';
import ApiErrorHandler from '../utils/ApiErrorHandler.js';

const logger = getLogger('CouponAPI');
const errorLogger = getErrorLogger();
const perfLogger = getPerformanceLogger();

export function useCouponApi() {
  const { showToast } = useToast();
  const loading = ref(false);

  const executeWithErrorHandling = async (apiCall, context, successMessage = null) => {
    const startTime = performance.now();

    try {
      loading.value = true;
      logger.info(`${context} 시작`);

      const result = await apiCall();

      const duration = performance.now() - startTime;
      perfLogger.measure(context, Math.round(duration));
      logger.info(`${context} 완료`);

      if (successMessage) {
        showToast(successMessage, 'success');
      }

      return result;
    } catch (error) {
      const handledError = ApiErrorHandler.handleCouponError(error, context.toLowerCase());

      errorLogger.apiError(context, error, {
        duration: Math.round(performance.now() - startTime),
      });

      showToast(handledError.message, 'error');
      throw handledError;
    } finally {
      loading.value = false;
    }
  };

  const fetchCoupons = async (searchParams = {}) => {
    return executeWithErrorHandling(() => couponsAPI.getCoupons(searchParams), '쿠폰 목록 조회');
  };

  const createCoupon = async couponData => {
    return executeWithErrorHandling(
      () => couponsAPI.createCoupon(couponData),
      '쿠폰 생성',
      '쿠폰이 성공적으로 생성되었습니다.'
    );
  };

  const deleteCoupon = async id => {
    return executeWithErrorHandling(
      () => couponsAPI.deleteCoupon(id),
      '쿠폰 삭제',
      '쿠폰이 삭제되었습니다.'
    );
  };

  const toggleCouponStatus = async id => {
    const result = await executeWithErrorHandling(
      () => couponsAPI.toggleCouponStatus(id),
      '쿠폰 상태 토글'
    );

    // 상태별 메시지
    const statusMessage = result.isActive ? '활성화' : '비활성화';
    showToast(`쿠폰이 ${statusMessage}되었습니다.`, 'success');

    return result;
  };

  const getCouponById = async id => {
    return executeWithErrorHandling(() => couponsAPI.getCouponById(id), '쿠폰 조회');
  };

  const getCouponByCode = async code => {
    return executeWithErrorHandling(() => couponsAPI.getCouponByCode(code), '쿠폰 코드 조회');
  };

  const validateCoupon = async validationData => {
    return executeWithErrorHandling(() => couponsAPI.validateCoupon(validationData), '쿠폰 검증');
  };

  const applyCouponSimulation = async applicationData => {
    return executeWithErrorHandling(
      () => couponsAPI.applyCouponSimulation(applicationData),
      '쿠폰 적용 시뮬레이션'
    );
  };

  return {
    // State
    loading,

    // API Methods
    fetchCoupons,
    createCoupon,
    deleteCoupon,
    toggleCouponStatus,
    getCouponById,
    getCouponByCode,
    validateCoupon,
    applyCouponSimulation,
  };
}
