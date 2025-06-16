import { useDarkMode } from '@/composables/useDarkMode.js';

export default {
  install(app) {
    // 앱 시작 시 다크모드 초기화
    const { initializeDarkMode, watchSystemDarkMode } = useDarkMode();

    // 다크모드 상태 복원 및 시스템 설정 감지 시작
    initializeDarkMode();
    watchSystemDarkMode();

    // 전역 속성으로 다크모드 함수들 제공 (필요시)
    app.config.globalProperties.$darkMode = useDarkMode();
  },
};
