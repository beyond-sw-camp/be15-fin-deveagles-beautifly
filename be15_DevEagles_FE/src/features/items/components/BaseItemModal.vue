<template>
  <div class="overlay" @click.self="$emit('close')">
    <div class="panel">
      <!-- 헤더 -->
      <div class="header">
        <h2 class="title">{{ title }}</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <!-- 본문 -->
      <div class="body">
        <slot />
      </div>

      <!-- 푸터 -->
      <div class="footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup>
  import { onMounted, onBeforeUnmount } from 'vue';

  const props = defineProps({
    title: String,
  });

  const emit = defineEmits(['close']);

  const handleKeyDown = e => {
    if (e.key === 'Escape') {
      emit('close');
    }
  };

  onMounted(() => {
    window.addEventListener('keydown', handleKeyDown);
  });

  onBeforeUnmount(() => {
    window.removeEventListener('keydown', handleKeyDown);
  });
</script>

<style scoped>
  /* 오버레이 */
  .overlay {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 999;
    display: flex;
    justify-content: flex-end;
  }

  /* 패널 */
  .panel {
    width: 480px;
    height: 100%;
    background-color: white;
    display: flex;
    flex-direction: column;
    box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
  }

  /* 헤더 */
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 22px;
    border-bottom: 1px solid #eee;
  }
  .title {
    font-size: 18px;
    font-weight: bold;
  }
  .close-btn {
    font-size: 24px;
    background: none;
    border: none;
    cursor: pointer;
  }

  /* 본문 */
  .body {
    padding: 24px;
    flex: 1;
    overflow-y: auto;
  }

  /* 푸터 */
  .footer {
    padding: 16px 24px;
    border-top: 1px solid #eee;
  }
  .footer-left,
  .footer-right {
    display: flex;
    gap: 8px;
  }

  .cancel,
  .submit {
    padding: 8px 16px;
    background: white;
    border: 1px solid #aaa;
    border-radius: 4px;
    cursor: pointer;
  }
</style>
