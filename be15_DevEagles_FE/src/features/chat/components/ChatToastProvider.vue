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
    chatStore.setToastHandler(async msg => {
      console.log('[ChatToastProvider] 🔔 알림 메시지:', msg);

      // 안전하게 메시지 구성
      const title = '새 메시지';
      const content = msg?.content || '확인해보세요.';

      toastRef.value?.success(`${title}: ${content}`, {
        duration: 5000,
        closable: true,
        showIcon: true,
      });
    });
  });
</script>
