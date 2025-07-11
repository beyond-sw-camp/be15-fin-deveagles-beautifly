<template>
  <BaseToast ref="toastRef" position="top-right" />
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import { useChatStore } from '@/store/useChatStore';
  import BaseToast from '@/components/common/BaseToast.vue';

  const chatStore = useChatStore();
  const toastRef = ref(null);

  onMounted(() => {
    chatStore.setToastHandler(msg => {
      console.log('[ChatToastProvider] 🔔 알림 메시지:', msg);

      // BaseToast 내부에 있는 info() 메서드 활용 → 색상 및 스타일 정상 반영됨
      toastRef.value?.success('새 메시지가 도착했습니다', {
        duration: 5000,
        closable: true,
        showIcon: true,
      });
    });
  });
</script>

<style scoped>
  /* 없음 */
</style>
