export const MESSAGES = {
  // 공통 메시지
  COMMON: {
    CREATED: item => `${item}이 생성되었습니다.`,
    UPDATED: item => `${item}이 수정되었습니다.`,
    DELETED: item => `${item}이 삭제되었습니다.`,
    ACTIVATED: item => `${item}이 활성화되었습니다.`,
    DEACTIVATED: item => `${item}이 비활성화되었습니다.`,
    NOT_IMPLEMENTED: feature => `${feature} 기능은 준비 중입니다.`,
  },

  // 워크플로우 관련
  WORKFLOW: {
    ITEM_NAME: '워크플로우',
    CREATE_NOT_IMPLEMENTED: '워크플로우 생성 기능은 준비 중입니다.',
    DETAIL_NOT_IMPLEMENTED: '워크플로우 상세보기 기능은 준비 중입니다.',
    EDIT_NOT_IMPLEMENTED: '워크플로우 수정 기능은 준비 중입니다.',
    DELETE_CONFIRM: name => `'${name}' 워크플로우를 정말 삭제하시겠습니까?`,
  },

  // 쿠폰 관련
  COUPON: {
    ITEM_NAME: '쿠폰',
    DELETE_CONFIRM: name => `'${name}' 쿠폰을 정말 삭제하시겠습니까?`,
    EMPTY_STATE: '등록된 쿠폰이 없습니다.',
    CREATE_FIRST: '첫 번째 쿠폰 생성하기',
  },

  // 캠페인 관련
  CAMPAIGN: {
    ITEM_NAME: '캠페인',
    DELETE_CONFIRM: name => `'${name}' 캠페인을 정말 삭제하시겠습니까?`,
    EMPTY_STATE: '등록된 캠페인이 없습니다.',
    CREATE_FIRST: '첫 번째 캠페인 생성하기',
  },
};
