import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useChatStore = defineStore('chat', () => {
  const messages = ref([]);
  const isChatModalOpen = ref(false);
  const currentRoomId = ref(null);
  const currentUserId = ref(null);
  const subscribedRoomId = ref(null);

  const toastQueue = ref([]); // ✅ 큐
  const isShowingToast = ref(false); // ✅ 현재 표시 여부
  let toastHandler = null; // ✅ 핸들러 직접 할당 (ref 아님)

  const addMessage = msg => {
    messages.value = [...messages.value, msg];
  };

  const setToastHandler = handlerFn => {
    toastHandler = handlerFn;
  };

  const triggerToast = msg => {
    toastQueue.value.push(msg);
    processToastQueue();
  };

  const processToastQueue = async () => {
    if (isShowingToast.value || toastQueue.value.length === 0 || !toastHandler) return;

    isShowingToast.value = true;
    const msg = toastQueue.value.shift();

    try {
      await toastHandler(msg); // 반드시 Promise 반환
    } catch (e) {
      console.warn('❗ 알림 표시 중 오류', e);
    } finally {
      isShowingToast.value = false;
      processToastQueue();
    }
  };

  return {
    messages,
    isChatModalOpen,
    currentRoomId,
    currentUserId,
    subscribedRoomId,
    addMessage,
    setToastHandler,
    triggerToast,
    setChatModalOpen: val => (isChatModalOpen.value = val),
    setCurrentRoomId: id => (currentRoomId.value = id),
    setCurrentUserId: id => (currentUserId.value = id),
    setSubscribedRoomId: id => (subscribedRoomId.value = id),
    clearMessages: () => (messages.value = []),
    resetChatState: () => {
      messages.value = [];
      currentRoomId.value = null;
      subscribedRoomId.value = null;
      currentUserId.value = null;
      toastHandler = null;
      toastQueue.value = [];
      isShowingToast.value = false;
    },
  };
});
