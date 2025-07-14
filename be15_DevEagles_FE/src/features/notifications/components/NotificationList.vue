<script setup>
  import { ref, computed, onMounted, watch, nextTick } from 'vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import { useAuthStore } from '@/store/auth';
  import { getMyNotifications } from '@/features/notifications/api/notifications.js';
  import { useNotifications } from '@/features/notifications/composables/useNotifications.js';

  const props = defineProps({
    modelValue: Boolean,
    triggerElement: Object,
  });
  const emit = defineEmits(['update:modelValue']);

  const authStore = useAuthStore();
  const historicalNotifications = ref([]);
  const isLoading = ref(false);

  // [수정] Composables의 반환값을 안전하게 받습니다.
  const notificationState = useNotifications();

  // [수정] computed 속성을 방어적으로 재작성하여 런타임 에러를 방지합니다.
  const allNotifications = computed(() => {
    // useNotifications 훅에서 반환된 객체나 그 내부의 ref가 아직 준비되지 않았을 경우를 대비합니다.
    const rtNotifications = notificationState?.realtimeNotifications?.value || [];
    const histNotifications = historicalNotifications.value || [];

    const historicalIds = new Set(histNotifications.map(n => n.notificationId));

    const uniqueRealtimeNotifications = rtNotifications.filter(
      n => !historicalIds.has(n.notificationId)
    );

    return [...uniqueRealtimeNotifications, ...histNotifications];
  });

  // 알림 타입에 따른 아이콘과 발신자, 색상을 반환하는 헬퍼 함수
  const getNotificationDetails = type => {
    switch (type) {
      case 'RESERVATION':
        return { icon: '📅', sender: '예약 시스템', color: '#3b82f6' };
      case 'ANALYSIS':
        return { icon: '📈', sender: '데이터 분석', color: '#10b981' };
      case 'NOTICE':
        return { icon: '📢', sender: '공지사항', color: '#f97316' };
      default:
        return { icon: '🔔', sender: '시스템', color: '#6b7280' };
    }
  };

  // 날짜 형식을 'n월 n일' 등으로 변환하는 함수
  const formatDate = dateString => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getMonth() + 1}월 ${date.getDate()}일`;
  };

  // [수정] 읽음 처리 함수를 안전하게 호출합니다.
  const handleItemClick = item => {
    if (notificationState && typeof notificationState.handleMarkAsRead === 'function') {
      notificationState.handleMarkAsRead(item);
    }
  };

  // 과거 알림 내역을 불러오는 함수
  const fetchHistoricalNotifications = async () => {
    if (isLoading.value || !authStore.isAuthenticated) return;
    isLoading.value = true;
    try {
      const response = await getMyNotifications({ page: 0, size: 20 });
      historicalNotifications.value = response.data.content;
    } catch (err) {
      console.error('과거 알림 목록 조회에 실패했습니다:', err);
    } finally {
      isLoading.value = false;
    }
  };

  onMounted(() => {
    fetchHistoricalNotifications();
  });

  // 팝오버의 안정적인 렌더링을 위한 watch 로직은 그대로 유지합니다.
  watch(
    () => props.modelValue,
    async newVal => {
      if (newVal) {
        await nextTick();
        requestAnimationFrame(() => {
          window.dispatchEvent(new Event('resize'));
        });
      }
    }
  );
</script>

<template>
  <BasePopover
    :model-value="modelValue"
    :trigger-element="triggerElement"
    placement="bottom"
    size="md"
    :show-actions="false"
    :mask-closable="true"
    class="z-50"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <template #default>
      <div class="notification-wrapper">
        <div class="notification-header">
          <span class="icon">🔔</span>
          <h3 class="title">새 알림</h3>
        </div>

        <div class="notification-scroll custom-scrollbar">
          <div v-if="isLoading" class="empty-state">
            <p class="empty-text">알림을 불러오는 중입니다...</p>
          </div>

          <ul v-else-if="allNotifications.length > 0" class="notification-list">
            <li
              v-for="item in allNotifications"
              :key="item.notificationId"
              :class="['notification-item', { 'is-read': item.read }]"
              @click="handleItemClick(item)"
            >
              <div class="item-icon">
                {{ getNotificationDetails(item.type).icon }}
              </div>
              <div class="item-content">
                <div class="item-meta">
                  <span class="sender" :style="{ color: getNotificationDetails(item.type).color }">
                    {{ getNotificationDetails(item.type).sender }}
                  </span>
                  <span class="date">{{ formatDate(item.createdAt) }}</span>
                </div>
                <p class="message">{{ item.content }}</p>
              </div>
            </li>
          </ul>

          <div v-else class="empty-state empty-state--enhanced">
            <div class="empty-icon">📭</div>
            <p class="empty-text">알림이 없습니다.</p>
          </div>
        </div>
      </div>
    </template>
  </BasePopover>
</template>

<style scoped>
  .notification-wrapper {
    width: 100%;
    max-width: 340px;
    background-color: #fff;
    border-radius: 12px;
    box-sizing: border-box;
    overflow: hidden;
    padding: 0;
  }

  .notification-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px;
    border-bottom: 1px solid #e5e7eb;
  }

  .title {
    font-size: 14px;
    font-weight: 600;
    color: #111827;
  }

  .notification-scroll {
    max-height: 340px;
    overflow-y: auto;
  }

  .notification-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 8px 12px 16px;
  }

  .notification-item {
    display: flex;
    gap: 12px;
    padding: 14px 16px;
    border-radius: 12px;
    background-color: #fefefe;
    border: 1px solid #e0e0e0;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
    transition:
      background-color 0.2s,
      opacity 0.2s;
    word-break: break-word;
    cursor: pointer;
  }

  .notification-item:hover {
    background-color: #f9fafb;
  }

  .notification-item.is-read {
    opacity: 0.65;
  }

  .item-icon {
    font-size: 20px;
    line-height: 1;
    width: 32px;
    text-align: center;
    margin-top: 4px;
  }

  .item-content {
    flex: 1;
    min-width: 0;
  }

  .item-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    margin-bottom: 6px;
  }

  .sender {
    font-weight: 600;
  }

  .date {
    color: #9ca3af;
  }

  .message {
    font-size: 13.5px;
    color: #374151;
    line-height: 1.5;
    white-space: pre-line;
  }

  .empty-state {
    padding: 40px 20px;
    text-align: center;
    color: #6b7280;
  }

  .empty-state--enhanced .empty-icon {
    font-size: 2rem;
    margin-bottom: 8px;
  }

  .custom-scrollbar {
    scrollbar-width: thin;
    scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
  }

  .custom-scrollbar::-webkit-scrollbar {
    width: 5px;
  }

  .custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.3);
    border-radius: 6px;
  }

  .custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.5);
  }
</style>
