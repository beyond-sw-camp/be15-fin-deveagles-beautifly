/**
 * API 에러 처리 유틸리티
 */
class ApiErrorHandler {
  /**
   * HTTP 에러를 처리하여 사용자 친화적 에러 메시지 반환
   */
  static handleError(error) {
    if (error.response) {
      return this.handleHttpError(error.response);
    } else if (error.request) {
      return new Error('네트워크 연결을 확인해주세요.');
    } else {
      return new Error(error.message || '예상치 못한 오류가 발생했습니다.');
    }
  }

  /**
   * HTTP 상태 코드별 에러 처리
   */
  static handleHttpError(response) {
    const { status, data } = response;

    const errorMessages = {
      400: '요청 데이터를 확인해주세요.',
      401: '로그인이 필요합니다.',
      403: '접근 권한이 없습니다.',
      404: '요청한 데이터를 찾을 수 없습니다.',
      409: '중복된 데이터입니다.',
      422: '입력 데이터를 확인해주세요.',
      500: '서버에 일시적인 문제가 발생했습니다.',
      502: '서버 연결에 문제가 있습니다.',
      503: '서비스를 일시적으로 사용할 수 없습니다.',
    };

    const message = data?.message || errorMessages[status] || '알 수 없는 오류가 발생했습니다.';
    return new Error(message);
  }

  /**
   * 쿠폰별 특화 에러 메시지
   */
  static handleCouponError(error, context = '') {
    const baseError = this.handleError(error);

    const couponContextMessages = {
      create: '쿠폰 생성에 실패했습니다.',
      update: '쿠폰 수정에 실패했습니다.',
      delete: '쿠폰 삭제에 실패했습니다.',
      toggle: '쿠폰 상태 변경에 실패했습니다.',
      validate: '쿠폰 검증에 실패했습니다.',
      apply: '쿠폰 적용에 실패했습니다.',
      list: '쿠폰 목록을 불러오는데 실패했습니다.',
    };

    const contextMessage = couponContextMessages[context];
    if (contextMessage && !baseError.message.includes('쿠폰')) {
      return new Error(`${contextMessage} ${baseError.message}`);
    }

    return baseError;
  }
}

export default ApiErrorHandler;
