<template>
  <div class="layout">
    <TheSidebar @sidebar-toggle="handleSidebarToggle" />
    <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <TheHeader />
      <main class="content">
        <router-view />
      </main>
    </div>
    <div class="chat-button-wrapper">
      <button
        class="chat-inquiry-button"
        @mouseenter="isHovered = true"
        @mouseleave="isHovered = false"
        @click="toggleChat"
      >
        <MessageCircleIcon class="chat-icon" />
        <transition name="fade">
          <span v-if="isHovered" class="chat-label">1:1 문의하기</span>
        </transition>
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
  import { MessageCircleIcon } from '@/components/icons/index.js';

  const sidebarCollapsed = ref(false);
  const isHovered = ref(false);
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
    align-items: flex-end;
    z-index: 3000;
  }

  .chat-inquiry-button {
    background-color: var(--color-primary-main);
    color: white;
    border: none;
    border-radius: 9999px;
    padding: 0.75rem 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    height: 44px;
    transition: width 0.3s ease;
  }

  .chat-icon {
    width: 18px;
    height: 18px;
    color: white;
    flex-shrink: 0;
  }

  .chat-label {
    white-space: nowrap;
  }

  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.2s ease;
  }

  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
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
