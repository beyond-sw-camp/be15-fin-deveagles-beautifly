<template>
  <header class="header">
    <div class="header-left">
      <div class="quick-menu-section">
        <span class="quick-menu-label">Quick Menu</span>
        <nav class="quick-menu">
          <div class="tooltip-container">
            <button class="quick-menu-item" @click="openReservationModule">
              <CalendarIcon :size="16" />
              <span>예약</span>
            </button>
            <div class="tooltip tooltip-bottom tooltip-primary">예약 등록</div>
          </div>
          <div class="tooltip-container">
            <button class="quick-menu-item" @click="openCustomerModule">
              <UsersIcon :size="16" />
              <span>고객</span>
            </button>
            <div class="tooltip tooltip-bottom tooltip-primary">고객 목록</div>
          </div>
          <div class="tooltip-container">
            <button class="quick-menu-item" @click="openSalesModule">
              <DollarIcon :size="16" />
              <span>매출</span>
            </button>
            <div class="tooltip tooltip-bottom tooltip-primary">매출 등록</div>
          </div>
          <div class="tooltip-container">
            <button class="quick-menu-item" @click="openMessageModule">
              <MessageCircleIcon :size="16" />
              <span>문자</span>
            </button>
            <div class="tooltip tooltip-bottom tooltip-primary">문자 발신</div>
          </div>
          <div class="tooltip-container">
            <button class="quick-menu-item" @click="openAnalyticsModule">
              <BarChartIcon :size="16" />
              <span>분석</span>
            </button>
            <div class="tooltip tooltip-bottom tooltip-primary">데이터 분석</div>
          </div>
        </nav>
      </div>
    </div>

    <div class="header-right">
      <div class="header-actions">
        <!-- 검색 -->
        <div class="search-box">
          <SearchIcon :size="16" class="search-icon" />
          <input
            ref="searchInputRef"
            v-model="searchQuery"
            type="text"
            placeholder="고객명, 연락처 검색..."
            class="search-input"
            @focus="onSearchFocus"
            @blur="onSearchBlur"
          />
          <div class="search-shortcut" :class="{ visible: !isSearchFocused && !searchQuery }">
            <span class="shortcut-key">Ctrl</span>
            <span class="shortcut-plus">+</span>
            <span class="shortcut-key">K</span>
          </div>
        </div>

        <!-- 새 고객 등록 버튼 -->
        <div class="tooltip-container">
          <button class="action-btn" @click="createCustomer">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
          </button>
          <div class="tooltip tooltip-bottom tooltip-primary">새 고객 등록</div>
        </div>

        <!-- 알림 -->
        <div class="tooltip-container">
          <button class="notification-btn" @click="toggleNotifications">
            <BellIcon :size="18" />
            <span v-if="notificationCount > 0" class="notification-badge">{{
              notificationCount > 99 ? '99+' : notificationCount
            }}</span>
          </button>
          <div class="tooltip tooltip-bottom tooltip-primary">알림</div>
        </div>

        <!-- 사용자 메뉴 -->
        <div ref="userMenuRef" class="user-menu">
          <button class="user-btn" @click="toggleUserMenu">
            <div class="user-avatar">
              <span class="avatar-text">{{ userInitial }}</span>
            </div>
            <div class="user-info">
              <span class="user-name">{{ userName }}</span>
              <span class="user-role">{{ userRole }}</span>
            </div>
            <ChevronDownIcon :size="12" class="dropdown-arrow" :class="{ rotated: showUserMenu }" />
          </button>

          <Transition name="dropdown">
            <div v-if="showUserMenu" class="user-dropdown">
              <div class="dropdown-header">
                <div class="user-profile">
                  <div class="user-avatar-large">
                    <span class="avatar-text">{{ userInitial }}</span>
                  </div>
                  <div class="user-details">
                    <div class="user-name">{{ userName }}</div>
                    <div class="user-uid">{{ userUID }}</div>
                  </div>
                </div>
              </div>

              <div class="dropdown-menu">
                <router-link to="/profile" class="dropdown-item">
                  <UserIcon :size="16" class="item-icon" />
                  <span>프로필</span>
                </router-link>
                <router-link to="/settings/account" class="dropdown-item">
                  <SettingsIcon :size="16" class="item-icon" />
                  <span>계정 설정</span>
                </router-link>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item" @click="logout">
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    class="item-icon"
                  >
                    <path
                      d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9"
                    ></path>
                    <polyline points="16,17 21,12 16,7"></polyline>
                    <line x1="21" y1="12" x2="9" y2="12"></line>
                  </svg>
                  <span>로그아웃</span>
                </button>
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
  import { ref, computed, onMounted, onUnmounted } from 'vue';
  import { useRoute } from 'vue-router';
  import {
    SearchIcon,
    BellIcon,
    ChevronDownIcon,
    UserIcon,
    SettingsIcon,
    CalendarIcon,
    UsersIcon,
    DollarIcon,
    MessageCircleIcon,
    BarChartIcon,
  } from '../icons';

  const route = useRoute();
  const userMenuRef = ref(null);
  const searchInputRef = ref(null);

  // 상태
  const searchQuery = ref('');
  const showUserMenu = ref(false);
  const showNotifications = ref(false);
  const notificationCount = ref(3);
  const isSearchFocused = ref(false);

  // 사용자 정보
  const userName = ref('관리자');
  const userUID = ref('deveagles');
  const userRole = ref('매장 사장');

  const userInitial = computed(() => {
    return userName.value.charAt(0).toUpperCase();
  });

  // 검색 관련
  const onSearchFocus = () => {
    isSearchFocused.value = true;
  };

  const onSearchBlur = () => {
    isSearchFocused.value = false;
  };

  // 키보드 단축키 처리
  const handleKeydown = event => {
    // Ctrl+K 또는 Cmd+K로 검색창 포커스
    if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
      event.preventDefault();
      searchInputRef.value?.focus();
    }

    // ESC로 검색창 블러
    if (event.key === 'Escape' && isSearchFocused.value) {
      searchInputRef.value?.blur();
    }
  };

  // Quick Menu 액션들
  const openReservationModule = () => {
    console.log('예약 모듈 열기');
  };

  const openCustomerModule = () => {
    console.log('고객 모듈 열기');
  };

  const openSalesModule = () => {
    console.log('매출 모듈 열기');
  };

  const openMessageModule = () => {
    console.log('문자 모듈 열기');
  };

  const openAnalyticsModule = () => {
    console.log('분석 모듈 열기');
  };

  // 액션
  const createCustomer = () => {
    console.log('새 고객 등록');
  };

  // 메뉴 토글
  const toggleUserMenu = () => {
    showUserMenu.value = !showUserMenu.value;
  };

  const toggleNotifications = () => {
    showNotifications.value = !showNotifications.value;
  };

  // 외부 클릭 감지
  const handleClickOutside = event => {
    if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
      showUserMenu.value = false;
    }
  };

  const logout = () => {
    console.log('로그아웃');
    showUserMenu.value = false;
  };

  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
    document.addEventListener('keydown', handleKeydown);
  });

  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
    document.removeEventListener('keydown', handleKeydown);
  });
</script>

<style scoped>
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 1.5rem;
    background-color: var(--color-neutral-white);
    border-bottom: 1px solid var(--color-gray-200);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    position: sticky;
    top: 0;
    z-index: 100;
    height: 56px;
    min-height: 56px;
  }

  .header-left {
    flex: 1;
    display: flex;
    align-items: center;
  }

  .quick-menu-section {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .quick-menu-label {
    font-size: 11px;
    font-weight: 600;
    color: var(--color-gray-500);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    white-space: nowrap;
  }

  .quick-menu {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .quick-menu-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 0.75rem;
    border: none;
    border-radius: 0.5rem;
    background: none;
    color: var(--color-gray-600);
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    transition: all 150ms ease;
    white-space: nowrap;
  }

  .quick-menu-item:hover {
    background-color: var(--color-gray-50);
    color: var(--color-gray-900);
    transform: translateY(-1px);
  }

  .header-right {
    display: flex;
    align-items: center;
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .search-box {
    position: relative;
    display: flex;
    align-items: center;
  }

  .search-icon {
    position: absolute;
    left: 0.75rem;
    color: var(--color-gray-500);
    z-index: 1;
    transition: color 150ms ease;
  }

  .search-input {
    width: 240px;
    padding: 0.5rem 5rem 0.5rem 2.5rem;
    border: 1px solid var(--color-gray-300);
    border-radius: 0.5rem;
    font-size: 13px;
    background-color: var(--color-gray-50);
    transition: all 150ms ease;
    height: 36px;
  }

  .search-input:focus {
    outline: none;
    border-color: var(--color-primary-500);
    background-color: var(--color-neutral-white);
    box-shadow: 0 0 0 3px rgba(54, 79, 107, 0.08);
    width: 280px;
  }

  .search-input:focus + .search-icon,
  .search-input:focus ~ .search-icon {
    color: var(--color-primary-500);
  }

  .search-shortcut {
    position: absolute;
    right: 0.75rem;
    display: flex;
    align-items: center;
    gap: 0.125rem;
    opacity: 0;
    transition: opacity 150ms ease;
    pointer-events: none;
  }

  .search-shortcut.visible {
    opacity: 1;
  }

  .shortcut-key {
    background-color: var(--color-gray-100);
    border: 1px solid var(--color-gray-300);
    border-radius: 0.25rem;
    padding: 0.125rem 0.375rem;
    font-size: 10px;
    font-weight: 500;
    color: var(--color-gray-600);
    line-height: 1;
  }

  .shortcut-plus {
    font-size: 10px;
    color: var(--color-gray-500);
    font-weight: 500;
  }

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: none;
    border-radius: 0.5rem;
    background-color: var(--color-gray-50);
    color: var(--color-gray-600);
    cursor: pointer;
    transition: all 150ms ease;
  }

  .action-btn:hover {
    background-color: var(--color-gray-100);
    color: var(--color-gray-900);
    transform: translateY(-1px);
  }

  .notification-btn {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: none;
    border-radius: 0.5rem;
    background: none;
    color: var(--color-gray-600);
    cursor: pointer;
    transition: all 150ms ease;
  }

  .notification-btn:hover {
    background-color: var(--color-gray-50);
    color: var(--color-gray-900);
    transform: translateY(-1px);
  }

  .notification-badge {
    position: absolute;
    top: 0.125rem;
    right: 0.125rem;
    background: linear-gradient(135deg, #ff6b6b, #ee5a52);
    color: var(--color-neutral-white);
    font-size: 9px;
    font-weight: 600;
    padding: 0.125rem 0.25rem;
    border-radius: 999px;
    min-width: 14px;
    height: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
    box-shadow: 0 2px 4px rgba(255, 107, 107, 0.3);
    border: 1.5px solid var(--color-neutral-white);
  }

  .user-menu {
    position: relative;
  }

  .user-btn {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.25rem 0.5rem;
    border: none;
    border-radius: 0.5rem;
    background: none;
    cursor: pointer;
    transition: all 150ms ease;
    height: 40px;
  }

  .user-btn:hover {
    background-color: var(--color-gray-50);
    transform: translateY(-1px);
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-neutral-white);
    font-weight: 600;
    font-size: 13px;
    box-shadow: 0 2px 8px rgba(54, 79, 107, 0.15);
  }

  .user-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    line-height: 1.2;
  }

  .user-name {
    font-size: 13px;
    font-weight: 600;
    color: var(--color-gray-900);
  }

  .user-role {
    font-size: 11px;
    color: var(--color-gray-500);
    margin-top: 1px;
  }

  .dropdown-arrow {
    color: var(--color-gray-500);
    transition: transform 150ms ease;
    margin-left: 0.25rem;
  }

  .dropdown-arrow.rotated {
    transform: rotate(180deg);
  }

  .user-dropdown {
    position: absolute;
    top: calc(100% + 0.5rem);
    right: 0;
    background-color: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 0.75rem;
    box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.12);
    min-width: 240px;
    z-index: 1000;
    overflow: hidden;
  }

  .dropdown-header {
    padding: 1rem;
    background: linear-gradient(135deg, var(--color-gray-50), var(--color-gray-25));
    border-bottom: 1px solid var(--color-gray-200);
  }

  .user-profile {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .user-avatar-large {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-neutral-white);
    font-weight: 600;
    font-size: 16px;
    box-shadow: 0 4px 12px rgba(54, 79, 107, 0.2);
  }

  .user-details {
    flex: 1;
  }

  .user-details .user-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-gray-900);
  }

  .user-uid {
    font-size: 12px;
    color: var(--color-gray-500);
    margin-top: 2px;
  }

  .dropdown-menu {
    padding: 0.5rem;
  }

  .dropdown-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.625rem 0.75rem;
    border-radius: 0.5rem;
    color: var(--color-gray-700);
    text-decoration: none;
    font-size: 13px;
    font-weight: 500;
    transition: all 150ms ease;
    border: none;
    background: none;
    width: 100%;
    text-align: left;
    cursor: pointer;
  }

  .dropdown-item:hover {
    background-color: var(--color-gray-50);
    color: var(--color-gray-900);
    transform: translateY(-1px);
  }

  .item-icon {
    color: var(--color-gray-500);
    flex-shrink: 0;
  }

  .dropdown-divider {
    height: 1px;
    background-color: var(--color-gray-200);
    margin: 0.5rem 0;
  }

  /* 드롭다운 애니메이션 */
  .dropdown-enter-active,
  .dropdown-leave-active {
    transition: all 150ms ease;
    transform-origin: top right;
  }

  .dropdown-enter-from,
  .dropdown-leave-to {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }

  /* 반응형 */
  @media (max-width: 1024px) {
    .search-input {
      width: 200px;
    }

    .search-input:focus {
      width: 240px;
    }

    .quick-menu-item span {
      display: none;
    }

    .quick-menu-item {
      padding: 0.5rem;
      min-width: 36px;
      justify-content: center;
    }

    .quick-menu-label {
      display: none;
    }

    .quick-menu {
      gap: 0.25rem;
    }
  }

  @media (max-width: 768px) {
    .header {
      padding: 0 1rem;
      height: 52px;
      min-height: 52px;
    }

    .search-input {
      width: 160px;
      padding-right: 2.5rem;
    }

    .search-input:focus {
      width: 200px;
    }

    .search-shortcut {
      display: none;
    }

    .user-info {
      display: none;
    }

    .user-btn {
      gap: 0;
      padding: 0.25rem;
    }

    .quick-menu-section {
      gap: 0.5rem;
    }

    .header-actions {
      gap: 0.75rem;
    }
  }
</style>
