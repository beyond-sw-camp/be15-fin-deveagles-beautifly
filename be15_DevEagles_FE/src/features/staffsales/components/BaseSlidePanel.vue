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

      <!-- 푸터 (선택적) -->
      <div v-if="$slots.footer" class="footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup>
  defineProps({
    title: String,
  });
  defineEmits(['close']);
</script>

<style scoped>
  .overlay {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 999;
    display: flex;
    justify-content: flex-end;
  }

  .panel {
    width: 480px;
    height: 100%;
    background-color: white;
    display: flex;
    flex-direction: column;
    box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px;
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

  .body {
    padding: 24px;
    flex: 1;
    overflow-y: auto;
  }

  .footer {
    padding: 16px 24px;
    border-top: 1px solid #eee;
  }
</style>
