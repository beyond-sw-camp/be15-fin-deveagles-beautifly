import api from '@/plugins/axios.js';

/**
 * (관리자용) 현재 로그인된 사용자의 매장 정보를 조회합니다.
 * 인증(토큰)이 필요한 API입니다.
 * @returns {Promise} shop 정보 API 응답
 */
export const getShopInfo = () => {
  return api.get('/shops');
};

/**
 * (공개용) shopId를 기반으로 공개 프로필 정보를 조회합니다.
 * 인증이 필요 없는 공개 API입니다.
 * @param {string | number} shopId - 조회할 매장의 고유 ID
 * @returns {Promise} shop 정보 API 응답
 */
export const getPublicShopInfo = shopId => {
  // 백엔드에 '/shops/p/{shopId}' 엔드포인트가 필요합니다.
  return api.get(`/shops/p/${shopId}`);
};
