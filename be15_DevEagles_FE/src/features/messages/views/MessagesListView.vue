<script setup>
  import { ref, computed, nextTick } from 'vue';
  import MessageItem from '../components/MessageItem.vue';
  import MessageSendModal from '../components/modal/MessageSendModal.vue';
  import SendConfirmModal from '../components/modal/SendConfirmModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import Pagination from '@/components/common/Pagaination.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import EditIcon from '@/components/icons/EditIcon.vue';
  import { SendHorizonalIcon, ClockIcon, MessageCircleIcon } from 'lucide-vue-next';

  const messages = ref([
    {
      id: 1,
      title: '예약 안내',
      content: '고객님 예약이 완료되었습니다.',
      status: 'sent',
      date: '2024-06-10',
    },
    {
      id: 2,
      title: '시술 전 안내',
      content: '시술 전 주의사항 안내드립니다.',
      status: 'reserved',
      date: '2024-06-12',
    },
    {
      id: 3,
      title: '리뷰 요청',
      content: '리뷰를 남겨주시면 감사하겠습니다.',
      status: 'sent',
      date: '2024-06-09',
    },
  ]);

  const searchKeyword = ref('');
  const statusFilter = ref('all');
  const currentPage = ref(1);
  const itemsPerPage = 10;

  const showDeleteConfirm = ref(false);
  const selectedMessage = ref(null);
  const triggerElement = ref(null);
  const showSendModal = ref(false);
  const showSendConfirm = ref(false);
  const messageToSend = ref('');
  const toast = ref(null);

  const filteredMessages = computed(() => {
    let result = [...messages.value];
    const keyword = searchKeyword.value.trim().toLowerCase();
    if (keyword) {
      result = result.filter(
        msg =>
          msg.title.toLowerCase().includes(keyword) || msg.content.toLowerCase().includes(keyword)
      );
    }
    if (statusFilter.value !== 'all') {
      result = result.filter(msg => msg.status === statusFilter.value);
    }
    return result;
  });

  const paginatedMessages = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return filteredMessages.value.slice(start, start + itemsPerPage);
  });

  const totalPages = computed(() => Math.ceil(filteredMessages.value.length / itemsPerPage));

  function onSearchChange(value) {
    searchKeyword.value = value;
    currentPage.value = 1;
  }
  function onStatusChange(value) {
    statusFilter.value = value;
    currentPage.value = 1;
  }
  function handleDelete(msg, event) {
    selectedMessage.value = msg;
    triggerElement.value = event.currentTarget;
    showDeleteConfirm.value = true;
  }
  function confirmDelete() {
    messages.value = messages.value.filter(m => m.id !== selectedMessage.value.id);
    showDeleteConfirm.value = false;
  }
  function cancelDelete() {
    showDeleteConfirm.value = false;
  }
  function handleSendRequest(content) {
    messageToSend.value = content;
    showSendModal.value = false;
    nextTick(() => {
      showSendConfirm.value = true;
    });
  }
  function handleSendConfirm() {
    showSendConfirm.value = false;
    nextTick(() => {
      toast.value?.success('메시지를 보냈습니다.');
    });
    messageToSend.value = '';
  }
</script>

<template>
  <div class="message-list-view">
    <div class="message-list-header">
      <h2 class="font-section-title text-dark">메시지 목록</h2>
    </div>

    <!-- 상단 통계 -->
    <div class="message-stats-bar">
      <div class="stat-card">
        <SendHorizonalIcon class="stat-icon icon-blue" />
        <div class="stat-info">
          <div class="stat-value">{{ messages.filter(m => m.status === 'sent').length }}</div>
          <div class="stat-label">발송 완료</div>
        </div>
      </div>
      <div class="stat-card">
        <ClockIcon class="stat-icon icon-gray" />
        <div class="stat-info">
          <div class="stat-value">{{ messages.filter(m => m.status === 'reserved').length }}</div>
          <div class="stat-label">예약 문자</div>
        </div>
      </div>
      <div class="stat-card">
        <MessageCircleIcon class="stat-icon icon-brown" />
        <div class="stat-info">
          <div class="stat-value">{{ messages.length }}</div>
          <div class="stat-label">전체 메시지</div>
        </div>
      </div>
    </div>

    <div class="message-filter-row with-button">
      <div class="filter-control">
        <label class="filter-label" for="message-status">메시지 상태</label>
        <select
          id="message-status"
          v-model="statusFilter"
          class="filter-select short"
          @change="onStatusChange($event.target.value)"
        >
          <option value="all">전체</option>
          <option value="sent">발송 완료</option>
          <option value="reserved">예약 문자</option>
        </select>
      </div>
      <BaseButton class="icon-button" type="primary" size="sm" @click="showSendModal = true">
        <SendHorizonalIcon :size="16" />
      </BaseButton>
    </div>

    <div class="message-list-table">
      <div class="message-list-header-row">
        <span class="message-list-col message-title">제목</span>
        <span class="message-list-col message-content">내용</span>
        <span class="message-list-col message-status">상태</span>
        <span class="message-list-col message-date">날짜</span>
        <span class="message-list-col message-actions">관리</span>
      </div>
      <MessageItem v-for="msg in paginatedMessages" :key="msg.id" :message="msg">
        <template #actions>
          <div
            v-if="msg.status === 'reserved'"
            style="display: flex; gap: 8px; justify-content: center"
          >
            <BaseButton type="ghost" size="sm" class="icon-button">
              <EditIcon :size="16" />
            </BaseButton>
            <BaseButton
              type="ghost"
              size="sm"
              class="icon-button"
              @click="event => handleDelete(msg, event)"
            >
              <TrashIcon :size="16" color="var(--color-error-600)" />
            </BaseButton>
          </div>
        </template>
      </MessageItem>
    </div>

    <Pagination
      v-if="totalPages > 1"
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="filteredMessages.length"
      :items-per-page="itemsPerPage"
      @page-change="page => (currentPage.value = page)"
    />

    <BasePopover
      v-model="showDeleteConfirm"
      title="메시지 삭제"
      message="정말 삭제하시겠습니까?"
      confirm-text="삭제"
      cancel-text="취소"
      confirm-type="error"
      :trigger-element="triggerElement"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />

    <MessageSendModal
      :model-value="showSendModal"
      @update:model-value="val => (showSendModal = val)"
      @request-send="handleSendRequest"
    />

    <SendConfirmModal
      :model-value="showSendConfirm"
      @update:model-value="val => (showSendConfirm = val)"
      @confirm="handleSendConfirm"
    />

    <BaseToast ref="toast" />
  </div>
</template>

<style scoped>
  @import '@/assets/base.css';
  @import '@/assets/css/components.css';
  @import '@/assets/css/styleguide.css';
  @import '@/assets/css/tooltip.css';

  .message-list-view {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }
  .message-list-view h2 {
    margin-bottom: 24px;
    text-align: left;
  }
  .message-stats-bar {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
    margin-bottom: 16px;
  }
  .stat-card {
    display: flex;
    align-items: center;
    gap: 12px;
    background-color: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 8px;
    padding: 16px 24px;
    min-width: 200px;
    flex: none;
  }
  .stat-icon {
    width: 32px;
    height: 32px;
    flex-shrink: 0;
  }
  .icon-blue {
    color: var(--color-primary-main);
  }
  .icon-gray {
    color: var(--color-gray-500);
  }
  .icon-brown {
    color: var(--color-gray-800);
  }
  .stat-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  .stat-value {
    font-size: 20px;
    font-weight: 700;
    color: var(--color-gray-900);
  }
  .stat-label {
    font-size: 14px;
    color: var(--color-gray-700);
  }
  .filter-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-gray-600);
    white-space: nowrap;
  }
  .filter-select {
    padding: 6px 10px;
    font-size: 14px;
    border: 1px solid var(--color-gray-300);
    border-radius: 6px;
    background-color: var(--color-neutral-white);
    max-width: 100px;
  }
  .message-list-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 1.5rem;
  }
  .message-filter-row.with-button {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 16px;
  }
  .filter-control {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .message-list-table {
    width: 100%;
    border-top: 1px solid var(--color-gray-200);
    background-color: var(--color-neutral-white);
    border-radius: 8px;
    overflow: hidden;
  }
  .message-list-header-row,
  .message-list-row {
    display: flex;
    font-size: 14px;
    color: var(--color-gray-700);
    border-bottom: 1px solid var(--color-gray-200);
    padding: 12px 16px;
  }
  .message-list-header-row {
    background-color: var(--color-gray-50);
    font-weight: 600;
  }
  .message-list-col {
    flex: 1;
    padding: 0 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
  }
  .message-title {
    flex: 1.5;
    justify-content: center;
    text-align: center;
  }
  .message-content {
    flex: 3;
    justify-content: center;
    text-align: center;
  }
  .message-status {
    flex: 1;
    justify-content: flex-start;
    text-align: left;
  }
  .message-date {
    flex: 1;
  }
  .message-actions {
    flex: 1;
  }
</style>
