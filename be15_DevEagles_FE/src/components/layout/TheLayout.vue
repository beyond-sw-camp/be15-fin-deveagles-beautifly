<template>
  <div class="layout">
    <TheSidebar @sidebar-toggle="handleSidebarToggle" />
    <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <TheHeader />
      <main class="content">
        <router-view />
      </main>
    </div>
    <!-- 고정된 채팅 버튼 -->
    <div class="chat-button-wrapper">
      <button class="chat-inquiry-button" @click="isChatOpen = !isChatOpen">
        <img src="@/images/logo_positive.png" class="chat-icon" />
        <span>1:1 문의하기</span>
      </button>

      <!-- 버튼 "위에" 고정된 채팅창 -->
      <ChatModal v-if="isChatOpen" @close="isChatOpen = false" />
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import TheSidebar from './TheSidebar.vue';
  import TheHeader from './TheHeader.vue';
  import ChatModal from '@/features/chat/components/ChatModal.vue';

  const sidebarCollapsed = ref(false);
  const isChatOpen = ref(false);
  const handleSidebarToggle = isCollapsed => {
    sidebarCollapsed.value = isCollapsed;
  };
</script>

<style scoped>
  .layout {
    display: flex;
    height: 100vh;
    overflow: hidden;
    background-color: var(--color-gray-50);
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-left: 200px;
    transition: margin-left 300ms ease;
    min-width: 0;
    width: calc(100% - 200px);
    background-color: var(--color-gray-50);
  }

  .main-container.sidebar-collapsed {
    margin-left: 50px;
    width: calc(100% - 50px);
  }

  .content {
    flex: 1;
    padding: 1.5rem;
    overflow-y: auto;
    background-color: var(--color-gray-50);
  }

  .chat-inquiry-button {
    position: fixed;
    bottom: 0;
    right: 0;
    margin: 0;
    padding: 0.5rem 1rem;
    border-radius: 9999px 9999px 9999px 9999px; /* 둥근 모서리 하나만 */
    background-color: var(--color-primary-main);
    color: white;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    z-index: 9999;
    box-shadow: -2px -2px 8px rgba(0, 0, 0, 0.1);
  }
  .chat-icon {
    width: 16px; /* ✅ 작고 귀엽게 줄임 */
    height: 16px;
    object-fit: contain;
  }
  .chat-button-wrapper {
    position: fixed;
    bottom: 50px;
    right: 20px;
    display: flex;
    flex-direction: column-reverse;
    align-items: flex-end;
    /* ↓ 여기 줄여야 함 */
    gap: 4px;
    z-index: 3000;
  }

  @media (max-width: 768px) {
    .main-container {
      margin-left: 0;
      width: 100%;
    }

    .main-container.sidebar-collapsed {
      margin-left: 0;
      width: 100%;
    }
  }
</style>
