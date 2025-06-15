export const MESSAGES = {
  // 한국어 받침 확인 함수
  _hasConsonant: word => {
    const lastChar = word.slice(-1);
    const code = lastChar.charCodeAt(0);

    // 한글이 아닌 경우 (영어, 숫자 등)
    if (code < 0xac00 || code > 0xd7a3) {
      // 영어 자음으로 끝나는 경우
      return /[bcdfghjklmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ]/.test(lastChar);
    }

    // 한글인 경우: 받침이 있으면 true, 없으면 false
    return (code - 0xac00) % 28 !== 0;
  },

  // 공통 메시지
  COMMON: {
    CREATED: item => {
      const hasConsonant = MESSAGES._hasConsonant(item);
      return hasConsonant ? `${item}이 생성되었습니다.` : `${item}가 생성되었습니다.`;
    },
    UPDATED: item => {
      const hasConsonant = MESSAGES._hasConsonant(item);
      return hasConsonant ? `${item}이 수정되었습니다.` : `${item}가 수정되었습니다.`;
    },
    DELETED: item => {
      const hasConsonant = MESSAGES._hasConsonant(item);
      return hasConsonant ? `${item}이 삭제되었습니다.` : `${item}가 삭제되었습니다.`;
    },
    ACTIVATED: item => {
      const hasConsonant = MESSAGES._hasConsonant(item);
      return hasConsonant ? `${item}이 활성화되었습니다.` : `${item}가 활성화되었습니다.`;
    },
    DEACTIVATED: item => {
      const hasConsonant = MESSAGES._hasConsonant(item);
      return hasConsonant ? `${item}이 비활성화되었습니다.` : `${item}가 비활성화되었습니다.`;
    },
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
