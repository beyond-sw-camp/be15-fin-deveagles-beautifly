<script setup>
  import { watch, nextTick } from 'vue';
  import BasePopover from '@/components/common/BasePopover.vue';

  const props = defineProps({
    modelValue: Boolean,
    triggerElement: Object,
  });
  const emit = defineEmits(['update:modelValue']);

  const notifications = [
    {
      sender: '네이버',
      senderColor: '#1a73e8',
      date: '6월 12일',
      content: '🎁 웅장한 선물상자가 기다리고 있어요!\n클릭해서 포인트 받기 →',
      icon: '🎁',
    },
    {
      sender: 'MY플레이스',
      senderColor: '#34a853',
      date: '6월 11일',
      content: '[쿠폰 발급 알림] [백억살롱 홍대본점] 10% 할인 쿠폰이 발급되었습니다.',
      icon: '💬',
    },
    {
      sender: '와이키키헤어',
      senderColor: '#e91e63',
      date: '6월 5일',
      content: '녹번점 방문은 만족하셨나요? 리뷰 남기고 할인 쿠폰 받기!',
      icon: '💇‍♀️',
    },
    ...Array.from({ length: 6 }, (_, i) => ({
      sender: `서비스${i + 1}`,
      senderColor: '#d32f2f',
      date: `6월 ${13 + i}일`,
      content: `📌 테스트 알림 항목입니다. ${i + 1}번째`,
      icon: '📌',
    })),
  ];

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
          <ul class="notification-list">
            <li v-for="(item, idx) in notifications" :key="'noti-' + idx" class="notification-item">
              <div class="item-icon">{{ item.icon }}</div>
              <div class="item-content">
                <div class="item-meta">
                  <span class="sender" :style="{ color: item.senderColor }">
                    {{ item.sender }}
                  </span>
                  <span class="date">{{ item.date }}</span>
                </div>
                <p class="message">{{ item.content }}</p>
              </div>
            </li>
          </ul>

          <div v-if="notifications.length === 0" class="empty-state empty-state--enhanced">
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
    transition: background-color 0.2s;
    word-break: break-word;
  }

  .notification-item:hover {
    background-color: #f9fafb;
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
