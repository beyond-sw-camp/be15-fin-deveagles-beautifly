<template>
  <div class="layout">
    <TheSidebar @sidebar-toggle="handleSidebarToggle" />
    <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <TheHeader />
      <main class="content">
        <router-view />
      </main>
    </div>
    <!-- ✅ 채팅 아이콘 버튼 -->
    <div class="chat-button-wrapper">
      <button class="chat-inquiry-button" @click="toggleChat">
        <img src="@/images/logo_positive.png" class="chat-icon" alt="채팅 아이콘" />
        <span>1:1 문의하기</span>
      </button>
    </div>

    <!-- ✅ 채팅 모달 -->
    <ChatModal v-if="isChatOpen" @close="toggleChat" />
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

  function toggleChat() {
    isChatOpen.value = !isChatOpen.value;
  }
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

  .chat-button-wrapper {
    position: fixed;
    bottom: 10px;
    right: 10px;
    display: flex;
    flex-direction: column-reverse;
    align-items: flex-end;
    gap: 4px;
    z-index: 3000;
  }

  .chat-inquiry-button {
    background-color: var(--color-primary-main);
    color: white;
    display: flex;
    align-items: center;
    padding: 0.75rem;
    border-radius: 9999px;
    gap: 0.5rem;
    transition: width 0.3s ease;
    overflow: hidden;
    width: 44px;
  }
  .chat-inquiry-button span {
    white-space: nowrap;
    opacity: 0;
    transition: opacity 0.2s ease;
  }
  .chat-inquiry-button:hover {
    width: 160px;
  }
  .chat-inquiry-button:hover span {
    opacity: 1;
  }
  .chat-icon {
    width: 18px;
    height: 18px;
    object-fit: contain;
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
