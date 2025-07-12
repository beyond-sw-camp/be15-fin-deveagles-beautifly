import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '@/store/auth';
import { markNotificationAsRead } from '@/features/notifications/api/notifications.js';

export function useNotifications() {
  // 이 파일은 이전에 생성한 파일을 그대로 사용합니다.
  // 단, unreadCount를 계산하는 computed 속성이 추가되었는지 확인합니다.
  const realtimeNotifications = ref([]);
  const historicalNotifications = ref([]); // NotificationList에서 관리하던 것을 이곳으로 이동
  const isSseConnected = ref(false);
  const authStore = useAuthStore();
  let eventSource = null;

  const allNotifications = computed(() => {
    const historicalIds = new Set(historicalNotifications.value.map(n => n.notificationId));
    const uniqueRealtimeNotifications = realtimeNotifications.value.filter(
      n => !historicalIds.has(n.notificationId)
    );
    return [...uniqueRealtimeNotifications, ...historicalNotifications.value];
  });

  const unreadCount = computed(() => {
    return allNotifications.value.filter(n => !n.read).length;
  });

  const connect = () => {
    if (
      !authStore.isAuthenticated ||
      (eventSource && eventSource.readyState !== EventSource.CLOSED)
    ) {
      return;
    }
    const sseUrl = `${import.meta.env.VITE_API_BASE_URL}/notifications/subscribe`;
    eventSource = new EventSource(sseUrl, { withCredentials: true });

    eventSource.onopen = () => (isSseConnected.value = true);
    eventSource.addEventListener('notification', event => {
      try {
        realtimeNotifications.value.unshift(JSON.parse(event.data));
      } catch (e) {
        console.error('SSE 데이터 파싱 오류', e);
      }
    });
    eventSource.onerror = error => {
      isSseConnected.value = false;
      eventSource.close();
      setTimeout(connect, 5000);
    };
  };

  const disconnect = () => {
    if (eventSource) eventSource.close();
  };

  const handleMarkAsRead = async notification => {
    if (notification.read) return;
    try {
      await markNotificationAsRead(notification.notificationId);
      notification.read = true;
    } catch (error) {
      console.error('알림 읽음 처리 실패', error);
    }
  };

  // 이제 이 훅이 직접 과거 알림을 관리합니다.
  const setHistoricalNotifications = notifications => {
    historicalNotifications.value = notifications;
  };

  onMounted(connect);
  onUnmounted(disconnect);

  return {
    allNotifications,
    unreadCount,
    isSseConnected,
    handleMarkAsRead,
    setHistoricalNotifications, // NotificationList에서 사용할 수 있도록 노출
  };
}
