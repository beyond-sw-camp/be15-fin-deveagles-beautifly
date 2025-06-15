<template>
  <aside
    class="sidebar"
    :class="{
      collapsed: isCollapsed,
      'hover-expanded': isCollapsed && isHovered,
    }"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <div class="sidebar-header">
      <div class="logo">
        <div v-if="!isCollapsed || isHovered" class="logo-expanded">
          <img src="../../images/logo_negative.png" alt="Logo" class="logo-image" />
          <span class="logo-text">Beautifly</span>
        </div>
        <img
          v-else
          src="../../images/logo_negative.png"
          alt="Logo"
          class="logo-image logo-collapsed"
        />
      </div>
      <div v-if="!isCollapsed || isHovered" class="tooltip-container">
        <button class="icon-button dark" @click="toggleSidebar">
          <PinOffIcon v-if="!isCollapsed" :size="16" />
          <PinIcon v-else :size="16" />
        </button>
        <div class="tooltip tooltip-left">
          {{ !isCollapsed ? '고정 해제' : '메뉴 고정' }}
        </div>
      </div>
    </div>

    <nav class="sidebar-nav">
      <ul class="nav-list">
        <li class="nav-item">
          <router-link
            to="/"
            class="nav-link"
            :class="{ active: isActiveRoute('/') }"
            data-tooltip="홈"
          >
            <HomeIcon class="nav-icon" />
            <span v-if="!isCollapsed || isHovered" class="nav-text">홈</span>
          </router-link>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('reservation') }"
              data-tooltip="예약"
              @click="toggleGroup('reservation')"
            >
              <CalendarIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">예약</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('reservation') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('reservation')"
              class="nav-sublist"
            >
              <li>
                <router-link to="/reservation/calendar" class="nav-sublink">캘린더</router-link>
              </li>
              <li>
                <router-link to="/reservation/list" class="nav-sublink">예약 목록</router-link>
              </li>
              <li>
                <router-link to="/reservation/schedule" class="nav-sublink">일정 목록</router-link>
              </li>
              <li>
                <router-link to="/reservation/holiday" class="nav-sublink">휴무 목록</router-link>
              </li>
              <li>
                <router-link to="/reservation/requests" class="nav-sublink"
                  >예약 신청 목록</router-link
                >
              </li>
              <li>
                <router-link to="/reservation/history" class="nav-sublink"
                  >예약 변경 이력</router-link
                >
              </li>
            </ul>
          </div>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('customer') }"
              data-tooltip="고객 관리"
              @click="toggleGroup('customer')"
            >
              <UsersIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">고객 관리</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('customer') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('customer')"
              class="nav-sublist"
            >
              <li><router-link to="/customer/list" class="nav-sublink">고객 목록</router-link></li>
              <li>
                <router-link to="/customer/prepaid" class="nav-sublink">선불고객 관리</router-link>
              </li>
            </ul>
          </div>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('sales') }"
              data-tooltip="매출 관리"
              @click="toggleGroup('sales')"
            >
              <DollarIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">매출 관리</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('sales') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('sales')"
              class="nav-sublist"
            >
              <li>
                <router-link to="/sales/management" class="nav-sublink">매출 관리</router-link>
              </li>
              <li><router-link to="/sales/staff" class="nav-sublink">직원결산</router-link></li>
            </ul>
          </div>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('product') }"
              data-tooltip="상품 관리"
              @click="toggleGroup('product')"
            >
              <ShoppingBagIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">상품 관리</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('product') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('product')"
              class="nav-sublist"
            >
              <li>
                <router-link to="/product/service" class="nav-sublink">시술/상품 관리</router-link>
              </li>
              <li>
                <router-link to="/product/membership" class="nav-sublink">회원권 관리</router-link>
              </li>
            </ul>
          </div>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('analytics') }"
              data-tooltip="데이터 분석"
              @click="toggleGroup('analytics')"
            >
              <BarChartIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">데이터 분석</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('analytics') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('analytics')"
              class="nav-sublist"
            >
              <li>
                <router-link to="/analytics/usage" class="nav-sublink">이용률 분석</router-link>
              </li>
              <li>
                <router-link to="/analytics/revenue" class="nav-sublink">상점 매출분석</router-link>
              </li>
            </ul>
          </div>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('message') }"
              data-tooltip="문자"
              @click="toggleGroup('message')"
            >
              <MessageCircleIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">문자</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('message') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('message')"
              class="nav-sublist"
            >
              <li>
                <router-link to="/message/history" class="nav-sublink">문자 내역</router-link>
              </li>
              <li>
                <router-link to="/message/templates" class="nav-sublink">문자 보관함</router-link>
              </li>
              <li>
                <router-link to="/message/settings" class="nav-sublink">메시지 설정</router-link>
              </li>
              <li>
                <router-link to="/message/ab-test" class="nav-sublink">A/B테스트</router-link>
              </li>
            </ul>
          </div>
        </li>

        <li class="nav-item">
          <router-link
            to="/workflows"
            class="nav-link"
            :class="{ active: isActiveRoute('/workflows') }"
            data-tooltip="워크플로우"
          >
            <WorkflowIcon class="nav-icon" />
            <span v-if="!isCollapsed || isHovered" class="nav-text">워크플로우</span>
          </router-link>
        </li>

        <li class="nav-item">
          <router-link
            to="/campaigns"
            class="nav-link"
            :class="{ active: isActiveRoute('/campaigns') }"
            data-tooltip="캠페인"
          >
            <MegaphoneIcon class="nav-icon" />
            <span v-if="!isCollapsed || isHovered" class="nav-text">캠페인</span>
          </router-link>
        </li>

        <li class="nav-item">
          <router-link
            to="/coupons"
            class="nav-link"
            :class="{ active: isActiveRoute('/coupons') }"
            data-tooltip="쿠폰 관리"
          >
            <TagIcon class="nav-icon" />
            <span v-if="!isCollapsed || isHovered" class="nav-text">쿠폰 관리</span>
          </router-link>
        </li>

        <li class="nav-item">
          <router-link
            to="/profile-link"
            class="nav-link"
            :class="{ active: isActiveRoute('/profile-link') }"
            data-tooltip="프로필 링크"
          >
            <LinkIcon class="nav-icon" />
            <span v-if="!isCollapsed || isHovered" class="nav-text">프로필 링크</span>
          </router-link>
        </li>

        <li class="nav-item">
          <div class="nav-group">
            <button
              class="nav-link nav-toggle"
              :class="{ active: activeGroups.includes('settings') }"
              data-tooltip="매장 설정"
              @click="toggleGroup('settings')"
            >
              <SettingsIcon class="nav-icon" />
              <span v-if="!isCollapsed || isHovered" class="nav-text">매장 설정</span>
              <ChevronRightIcon
                v-if="!isCollapsed || isHovered"
                class="nav-arrow"
                :class="{ expanded: activeGroups.includes('settings') }"
              />
            </button>
            <ul
              v-if="!isCollapsed || isHovered"
              v-show="activeGroups.includes('settings')"
              class="nav-sublist"
            >
              <li>
                <router-link to="/settings/store" class="nav-sublink">매장 기본 설정</router-link>
              </li>
              <li>
                <router-link to="/settings/reservation" class="nav-sublink">예약 설정</router-link>
              </li>
              <li><router-link to="/settings/staff" class="nav-sublink">직원 관리</router-link></li>
              <li>
                <router-link to="/settings/customer-grade" class="nav-sublink"
                  >고객 등급 등록</router-link
                >
              </li>
              <li>
                <router-link to="/settings/customer-tag" class="nav-sublink"
                  >고객 태그 등록</router-link
                >
              </li>
            </ul>
          </div>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import { useRoute } from 'vue-router';
  import {
    HomeIcon,
    CalendarIcon,
    UsersIcon,
    DollarIcon,
    ShoppingBagIcon,
    BarChartIcon,
    MessageCircleIcon,
    SettingsIcon,
    MegaphoneIcon,
    TagIcon,
    LinkIcon,
    ChevronRightIcon,
    PinIcon,
    PinOffIcon,
    WorkflowIcon,
  } from '../icons/index.js';

  const route = useRoute();

  // 사이드바 상태
  const isCollapsed = ref(false);
  const isHovered = ref(false);
  const activeGroups = ref([]); // 기본적으로 모든 하위 메뉴 닫힘

  const toggleSidebar = () => {
    isCollapsed.value = !isCollapsed.value;
    // 사이드바가 접히면 모든 서브메뉴도 닫기
    if (isCollapsed.value) {
      activeGroups.value = [];
    }
    // 펼칠 때는 이전 상태 유지 (자동으로 메뉴 열지 않음)
  };

  const handleMouseEnter = () => {
    if (isCollapsed.value) {
      isHovered.value = true;
    }
  };

  const handleMouseLeave = () => {
    isHovered.value = false;
  };

  const toggleGroup = groupName => {
    if (isCollapsed.value && !isHovered.value) return; // 접힌 상태에서는 서브메뉴 토글 비활성화

    const index = activeGroups.value.indexOf(groupName);
    if (index > -1) {
      activeGroups.value.splice(index, 1);
    } else {
      activeGroups.value.push(groupName);
    }
  };

  const isActiveRoute = path => {
    return route.path === path;
  };

  // 부모 컴포넌트에 사이드바 상태 전달
  const emit = defineEmits(['sidebar-toggle']);

  watch(isCollapsed, newValue => {
    emit('sidebar-toggle', newValue);
  });
</script>

<style scoped>
  @import '@/assets/css/buttons.css';
  @import '@/assets/css/tooltip.css';
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    width: 200px;
    background-color: var(--color-primary-main);
    color: var(--color-neutral-white);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
    transition: width 300ms ease;
    z-index: 1000;
  }

  .sidebar.collapsed {
    width: 50px;
  }

  .sidebar.collapsed .sidebar-header {
    justify-content: center;
    padding: 1rem 0.5rem;
  }

  .sidebar.collapsed .logo {
    justify-content: center;
  }

  .sidebar.hover-expanded {
    width: 200px;
    box-shadow: 4px 0 12px rgba(0, 0, 0, 0.15);
  }

  .sidebar-header {
    padding: 1rem;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    overflow: visible;
  }

  .logo {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .logo-expanded {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    height: 32px;
  }

  .logo-image {
    height: 24px;
    width: auto;
    object-fit: contain;
    flex-shrink: 0;
  }

  .logo-text {
    font-size: 16px;
    font-weight: 700;
    color: var(--color-neutral-white);
    letter-spacing: 0.5px;
    white-space: nowrap;
  }

  .logo-image.logo-collapsed {
    height: 24px;
    width: 24px;
    object-fit: contain;
  }

  .sidebar-nav {
    flex: 1;
    padding: 0.5rem 0;
    overflow-y: auto;
    overflow-x: hidden;
    min-height: 0; /* flex 아이템이 축소될 수 있도록 */
  }

  .nav-list {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .nav-item {
    margin-bottom: 1px;
  }

  .nav-link {
    display: flex;
    align-items: center;
    padding: 0.75rem 1rem;
    color: rgba(255, 255, 255, 0.8);
    text-decoration: none;
    transition: all 200ms ease;
    cursor: pointer;
    border: none;
    background: none;
    width: 100%;
    text-align: left;
    font-family: 'Noto Sans KR', sans-serif;
    font-size: 14px;
    position: relative;
  }

  .collapsed .nav-link {
    justify-content: center;
    padding: 0.75rem;
  }

  .hover-expanded .nav-link {
    justify-content: flex-start;
    padding: 0.75rem 1rem;
  }

  .nav-link:hover {
    background-color: rgba(255, 255, 255, 0.1);
    color: var(--color-neutral-white);
  }

  .nav-link.active {
    background-color: rgba(255, 255, 255, 0.15);
    color: var(--color-neutral-white);
    border-right: 3px solid var(--color-secondary-main);
  }

  .nav-icon {
    width: 20px;
    height: 20px;
    margin-right: 0.75rem;
    color: currentColor;
    flex-shrink: 0;
  }

  .collapsed .nav-icon {
    margin-right: 0;
  }

  .hover-expanded .nav-icon {
    margin-right: 0.75rem;
  }

  .nav-text {
    flex: 1;
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
  }

  .nav-arrow {
    width: 16px;
    height: 16px;
    transition: transform 200ms ease;
    color: rgba(255, 255, 255, 0.6);
    margin-left: auto;
  }

  .nav-arrow.expanded {
    transform: rotate(90deg);
  }

  .nav-toggle {
    justify-content: flex-start;
  }

  .collapsed .nav-toggle {
    justify-content: center;
  }

  .hover-expanded .nav-toggle {
    justify-content: flex-start;
  }

  .nav-sublist {
    list-style: none;
    margin: 0;
    padding: 0;
    background-color: rgba(0, 0, 0, 0.1);
  }

  .nav-sublink {
    display: block;
    padding: 0.5rem 1rem 0.5rem 3rem;
    color: rgba(255, 255, 255, 0.7);
    text-decoration: none;
    font-size: 13px;
    transition: all 200ms ease;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .nav-sublink:hover {
    background-color: rgba(255, 255, 255, 0.05);
    color: var(--color-neutral-white);
  }

  .nav-sublink.router-link-active {
    background-color: rgba(255, 255, 255, 0.1);
    color: var(--color-neutral-white);
    border-right: 2px solid var(--color-secondary-main);
  }

  /* 스크롤바 스타일링 */
  .sidebar-nav::-webkit-scrollbar {
    width: 6px;
  }

  .sidebar-nav::-webkit-scrollbar-track {
    background: transparent;
  }

  .sidebar-nav::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
  }

  .sidebar-nav::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.4);
  }

  /* 접힌 상태에서는 스크롤바 숨김 */
  .sidebar.collapsed .sidebar-nav::-webkit-scrollbar {
    width: 0;
  }

  .sidebar.collapsed .sidebar-nav {
    scrollbar-width: none; /* Firefox */
    -ms-overflow-style: none; /* IE and Edge */
  }

  /* 툴크 효과 (접힌 상태에서 hover 시 메뉴명 표시) */
  .collapsed .nav-link {
    position: relative;
  }

  .collapsed .nav-link:hover::after {
    content: attr(data-tooltip);
    position: absolute;
    left: 100%;
    top: 50%;
    transform: translateY(-50%);
    background-color: var(--color-neutral-dark);
    color: var(--color-neutral-white);
    padding: 0.375rem 0.75rem;
    border-radius: 0.25rem;
    font-size: 12px;
    white-space: nowrap;
    z-index: 1000;
    margin-left: 0.5rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
</style>
