<script setup>
  import { ref, computed, defineProps, defineEmits } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import AutoSendSettingDrawer from '@/features/messages/components/drawer/AutoSendSettingDrawer.vue';
  import EditIcon from '@/components/icons/EditIcon.vue';
  import NewIcon from '@/components/icons/NewIcon.vue';

  const props = defineProps({
    modelValue: Boolean,
    items: Array,
  });

  const emit = defineEmits(['update:modelValue', 'update:items']);

  const show = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const selectedIndex = ref(0);
  const drawerVisible = ref(false);
  const selectedMessage = ref(null);

  function openDrawer(message, index) {
    selectedMessage.value = {
      ...message,
      parentIndex: selectedIndex.value,
      messageIndex: index,
      label: props.items[selectedIndex.value]?.label || '',
    };
    drawerVisible.value = true;
  }

  function addNewMessage(index) {
    const newMessage = {
      message: '',
      enabled: true,
      sendTime: 'immediate',
      type: 'SMS',
      label: props.items[index]?.label || '',
    };
    selectedIndex.value = index;
    selectedMessage.value = {
      ...newMessage,
      parentIndex: index,
      messageIndex: null,
    };
    drawerVisible.value = true;
  }

  function updateMessage(updated) {
    const newItems = JSON.parse(JSON.stringify(props.items));
    const messages = newItems[updated.parentIndex].messages;

    if (updated.messageIndex == null || updated.messageIndex === undefined) {
      messages.push({
        id: Date.now(),
        message: updated.message,
        enabled: updated.enabled,
        sendTime: updated.sendTime,
        type: updated.type || 'SMS',
      });
    } else {
      messages[updated.messageIndex] = {
        ...messages[updated.messageIndex],
        message: updated.message,
        enabled: updated.enabled,
        sendTime: updated.sendTime,
      };
    }

    emit('update:items', newItems);
    drawerVisible.value = false;
  }

  function getSendTimeLabel(value) {
    const map = {
      immediate: '차감 즉시',
      '1min': '1분 후',
      '5min': '5분 후',
      custom: '직접 설정',
    };
    return map[value] || '-';
  }

  function truncateMessage(message) {
    if (!message) return '(메시지 없음)';
    return message.length > 30 ? message.slice(0, 30) + '...' : message;
  }
</script>

<template>
  <BaseModal v-model="show" title="자동 발신 설정">
    <div class="auto-send-modal-body">
      <!-- 왼쪽 리스트 -->
      <div class="auto-send-list">
        <div
          v-for="(item, index) in props.items"
          :key="index"
          class="auto-send-item"
          :class="{ selected: index === selectedIndex }"
          @click="selectedIndex = index"
        >
          <div class="list-item-header">
            <span class="label">{{ item.label }}</span>
            <button class="add-icon-button" @click.stop="addNewMessage(index)">
              <NewIcon />
            </button>
          </div>
        </div>
      </div>

      <!-- 오른쪽 프리뷰 -->
      <div v-if="props.items[selectedIndex]" class="auto-send-preview">
        <div
          v-for="(message, msgIdx) in props.items[selectedIndex].messages"
          :key="msgIdx"
          class="preview-card"
        >
          <div class="preview-row">
            <span class="preview-label">전송 시간</span>
            <span class="preview-value">{{ getSendTimeLabel(message.sendTime) }}</span>
          </div>
          <div class="preview-row">
            <span class="preview-label">자동 발송</span>
            <span class="preview-value">{{ message.enabled ? '활성화됨' : '비활성화됨' }}</span>
          </div>
          <div class="preview-row">
            <span class="preview-label">메시지</span>
            <span class="preview-value">{{ truncateMessage(message.message) }}</span>
          </div>
          <div class="text-right mt-2">
            <button class="edit-icon-button" @click="openDrawer(message, msgIdx)">
              <EditIcon />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 드로어 -->
    <AutoSendSettingDrawer
      v-if="selectedMessage"
      v-model="drawerVisible"
      :item="selectedMessage"
      @save="updateMessage"
    />
  </BaseModal>
</template>

<style scoped>
  .auto-send-modal-body {
    display: flex;
    gap: 2rem;
    min-height: 400px;
  }

  .auto-send-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    max-width: 280px;
    border-right: 1px solid #e5e7eb;
    padding-right: 1rem;
  }

  .auto-send-item {
    padding: 0.75rem 1rem;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    background-color: #f9fafb;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .auto-send-item:hover {
    background-color: #f3f4f6;
  }

  .auto-send-item.selected {
    background-color: #e0f2fe;
    border-color: #38bdf8;
  }

  .list-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .label {
    font-size: 0.95rem;
    font-weight: 500;
    color: #111827;
  }

  .add-icon-button {
    background: none;
    border: none;
    padding: 0;
    margin-left: 0.25rem;
    cursor: pointer;
  }

  .auto-send-preview {
    flex: 2;
    padding: 1rem 0;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .preview-card {
    width: 100%;
    background-color: #f9fafb;
    border: 1px dashed #cbd5e1;
    border-radius: 8px;
    padding: 1.25rem;
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .preview-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    border-bottom: 1px solid #e5e7eb;
    padding-bottom: 0.5rem;
  }

  .preview-label {
    font-weight: 500;
    color: #374151;
    font-size: 0.875rem;
  }

  .preview-value {
    color: #4b5563;
    font-size: 0.875rem;
    max-width: 60%;
    text-align: right;
    word-break: break-word;
  }

  .edit-icon-button {
    background: none;
    border: none;
    padding: 0.25rem;
    cursor: pointer;
  }
</style>
