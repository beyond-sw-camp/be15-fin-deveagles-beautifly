export const customerGradeOptions = [
  { value: '', text: '전체 고객' },
  { value: 'new', text: '신규 고객' },
  { value: 'growing', text: '성장 고객' },
  { value: 'loyal', text: '충성 고객' },
  { value: 'vip', text: 'VIP 고객' },
  { value: 'at_risk', text: '이탈 위험 고객' },
  { value: 'dormant', text: '휴면 고객' },
];

export const customerGradeTagOptions = [
  { tag_name: '신규 고객', color_code: '#00BFFF' },
  { tag_name: '성장 고객', color_code: '#32CD32' },
  { tag_name: '충성 고객', color_code: '#FFD700' },
  { tag_name: 'VIP 고객', color_code: '#FF69B4' },
  { tag_name: '이탈 위험 고객', color_code: '#FF6347' },
  { tag_name: '휴면 고객', color_code: '#808080' },
];

export const triggerCategories = [
  {
    value: 'lifecycle',
    icon: '👤',
    title: '고객 생애주기 관리',
    description: '신규부터 VIP까지 고객 단계별 맞춤 관리',
  },
  {
    value: 'periodic',
    icon: '🔄',
    title: '정기 리텐션',
    description: '방문 주기와 시술 기반 정기적 재방문 유도',
  },
  {
    value: 'special',
    icon: '🎉',
    title: '특별 이벤트',
    description: '생일, 기념일 등 특별한 날 기반 마케팅',
  },
  {
    value: 'prevention',
    icon: '🚨',
    title: '이탈 방지',
    description: 'AI 분석 기반 이탈 위험 고객 재활성화',
  },
];

export const triggerOptions = [
  // 고객 생애주기 관리
  {
    value: 'new-customer-welcome',
    icon: '👋',
    title: '신규 고객 환영',
    description: '신규 고객 등록 후 환영 메시지 발송',
    category: 'lifecycle',
  },
  {
    value: 'new-customer-followup',
    icon: '📞',
    title: '신규 고객 팔로업',
    description: '신규 고객 등록 후 지정 일수 후 팔로업',
    category: 'lifecycle',
  },
  {
    value: 'vip-attention-needed',
    icon: '👑',
    title: 'VIP 고객 특별 관리',
    description: 'VIP 고객의 방문 패턴 변화 감지 시',
    category: 'lifecycle',
  },
  // 정기 리텐션
  {
    value: 'visit-cycle',
    icon: '📅',
    title: '방문 주기 기반',
    description: '마지막 방문일로부터 평균 방문주기가 지난 경우',
    category: 'periodic',
  },
  {
    value: 'specific-treatment',
    icon: '💄',
    title: '특정 시술 후',
    description: '고객이 특정 시술을 받은 후 일정 기간이 지난 경우',
    category: 'periodic',
  },
  // 특별 이벤트
  {
    value: 'birthday',
    icon: '🎂',
    title: '생일 이벤트',
    description: '고객 생일 전후 지정된 기간에 실행',
    category: 'special',
  },
  {
    value: 'first-visit-anniversary',
    icon: '🌟',
    title: '첫 방문 기념일',
    description: '고객의 첫 방문 기념일에 실행',
    category: 'special',
  },
  {
    value: 'visit-milestone',
    icon: '🏆',
    title: '방문 횟수 기념',
    description: '특정 방문 횟수 달성 시 실행',
    category: 'special',
  },
  {
    value: 'amount-milestone',
    icon: '💎',
    title: '누적 금액 기념',
    description: '누적 결제 금액 달성 시 실행',
    category: 'special',
  },
  // 이탈 방지
  {
    value: 'new-customer-at-risk',
    icon: '⚠️',
    title: '신규 고객 이탈 위험',
    description: '신규 고객 30일 이상 미방문 시',
    category: 'prevention',
  },
  {
    value: 'reactivation-needed',
    icon: '🔄',
    title: '재활성화 필요',
    description: '60일 이상 미방문 고객 대상',
    category: 'prevention',
  },
  {
    value: 'growing-delayed',
    icon: '📈',
    title: '성장 고객 케어',
    description: '성장 고객의 예상 방문일 지연 시',
    category: 'prevention',
  },
  {
    value: 'loyal-delayed',
    icon: '💝',
    title: '충성 고객 케어',
    description: '충성 고객의 예상 방문일 지연 시',
    category: 'prevention',
  },
  {
    value: 'churn-risk-high',
    icon: '🆘',
    title: '고위험 이탈 예측',
    description: 'AI 모델이 높은 이탈 위험도 예측 시',
    category: 'prevention',
  },
];

export const actionOptions = [
  {
    value: 'message-only',
    icon: '💬',
    title: '메시지만 발송',
    description: '선택한 템플릿으로 메시지를 발송합니다',
  },
  {
    value: 'coupon-message',
    icon: '🎫',
    title: '쿠폰과 메시지 발송',
    description: '쿠폰과 함께 메시지를 발송합니다',
  },
  {
    value: 'system-notification',
    icon: '🔔',
    title: '시스템 알림',
    description: '내부 알림 시스템으로 알림을 발송합니다',
  },
];

export const treatmentOptions = [
  { value: 'facial-basic', text: '기본 페이셜' },
  { value: 'facial-premium', text: '프리미엄 페이셜' },
  { value: 'massage', text: '마사지' },
  { value: 'nail-basic', text: '기본 네일' },
  { value: 'nail-gel', text: '젤 네일' },
  { value: 'hair-cut', text: '헤어 컷' },
  { value: 'hair-perm', text: '헤어 펌' },
  { value: 'hair-color', text: '헤어 컬러' },
];

export const messageTemplateOptions = [
  { value: 'welcome', text: '신규 고객 환영 메시지' },
  { value: 'revisit', text: '재방문 유도 메시지' },
  { value: 'coupon', text: '쿠폰 발송 메시지' },
  { value: 'birthday', text: '생일 축하 메시지' },
  { value: 'follow-up', text: '시술 후 관리 메시지' },
];

export const couponOptions = [
  { value: 'discount-10', text: '10% 할인 쿠폰' },
  { value: 'discount-20', text: '20% 할인 쿠폰' },
  { value: 'free-service', text: '무료 서비스 쿠폰' },
  { value: 'birthday-special', text: '생일 특별 쿠폰' },
];
