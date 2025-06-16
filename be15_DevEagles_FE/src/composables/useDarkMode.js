import { ref, nextTick } from 'vue';

// 전역 다크모드 상태 (여러 컴포넌트에서 공유)
const isDarkMode = ref(false);
const isTransitioning = ref(false);

export function useDarkMode() {
  // 부드러운 다크모드 토글 함수
  const toggleDarkMode = async () => {
    // 전환 시작
    isTransitioning.value = true;

    // 모든 transition-enabled 요소에 클래스 추가
    document.documentElement.classList.add('dark-mode-transitioning');

    // 짧은 딜레이로 모든 컴포넌트가 준비되도록 함
    await new Promise(resolve => setTimeout(resolve, 50));

    // 다크모드 상태 변경
    isDarkMode.value = !isDarkMode.value;
    localStorage.setItem('darkMode', isDarkMode.value.toString());

    // DOM 업데이트 대기
    await nextTick();

    // 다크모드 클래스 적용/제거
    if (isDarkMode.value) {
      document.documentElement.classList.add('dark');
      document.body.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
      document.body.classList.remove('dark');
    }

    // transition 완료 후 정리
    setTimeout(() => {
      isTransitioning.value = false;
      document.documentElement.classList.remove('dark-mode-transitioning');
    }, 300); // transition duration과 동일하게 설정
  };

  // 다크모드 강제 설정
  const setDarkMode = enabled => {
    isDarkMode.value = enabled;
    localStorage.setItem('darkMode', enabled.toString());

    if (enabled) {
      document.documentElement.classList.add('dark');
      document.body.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
      document.body.classList.remove('dark');
    }
  };

  // 초기화 함수 (앱 시작 시 한 번만 호출)
  const initializeDarkMode = () => {
    // 로컬 스토리지에서 다크모드 상태 복원
    const savedDarkMode = localStorage.getItem('darkMode');
    if (savedDarkMode === 'true') {
      setDarkMode(true);
    } else if (savedDarkMode === 'false') {
      setDarkMode(false);
    } else {
      // 저장된 값이 없으면 시스템 설정 확인
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      setDarkMode(prefersDark);
    }
  };

  // 시스템 다크모드 변경 감지
  const watchSystemDarkMode = () => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    mediaQuery.addEventListener('change', e => {
      // 사용자가 수동으로 설정하지 않은 경우에만 시스템 설정 따라감
      const hasUserPreference = localStorage.getItem('darkMode') !== null;
      if (!hasUserPreference) {
        setDarkMode(e.matches);
      }
    });
  };

  return {
    // 상태
    isDarkMode,
    isTransitioning,

    // 메서드
    toggleDarkMode,
    setDarkMode,
    initializeDarkMode,
    watchSystemDarkMode,
  };
}
