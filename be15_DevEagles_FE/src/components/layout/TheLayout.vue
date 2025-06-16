<template>
  <div class="layout">
    <TheSidebar @sidebar-toggle="handleSidebarToggle" />
    <div class="main-container" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <TheHeader />
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import TheSidebar from './TheSidebar.vue';
  import TheHeader from './TheHeader.vue';

  const sidebarCollapsed = ref(false);

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
