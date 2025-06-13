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
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-left: 240px;
    transition: margin-left 300ms ease;
    min-width: 0;
    width: calc(100% - 240px);
  }

  .main-container.sidebar-collapsed {
    margin-left: 60px;
    width: calc(100% - 60px);
  }

  .content {
    flex: 1;
    padding: 1.5rem;
    overflow-y: auto;
    background-color: var(--color-neutral-bg);
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
