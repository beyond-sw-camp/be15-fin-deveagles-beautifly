<script setup>
  import { ref, computed, nextTick } from 'vue';
  import MessageItem from '../components/MessageItem.vue';
  import MessageStats from '../components/MessageStats.vue';
  import MessageSendModal from '../components/modal/MessageSendModal.vue';
  import SendConfirmModal from '../components/modal/SendConfirmModal.vue';
  import ReservationSendModal from '../components/modal/ReservationSendModal.vue';
  import TemplateSelectDrawer from '@/features/messages/components/TemplateSelectDrawer.vue';
  import CustomerSelectDrawer from '@/features/messages/components/CustomerSelectDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import MessageDetailModal from '@/features/messages/components/modal/MessageDetailModal.vue';
  import EditReservationModal from '@/features/messages/components/modal/EditReservationModal.vue';
  import BaseModal from '@/components/common/BaseModal.vue';

  const messages = ref([
    {
      id: 1,
      title: '예약 안내',
      content: '고객님 예약이 완료되었습니다.',
      receiver: '김민지',
      status: 'sent',
      date: '2024-06-10',
    },
    {
      id: 2,
      title: '시술 전 안내',
      content: '시술 전 주의사항 안내드립니다.',
      receiver: '박지훈',
      status: 'reserved',
      date: '2024-06-12',
    },
    {
      id: 3,
      title: '리뷰 요청',
      content: '리뷰를 남겨주시면 감사하겠습니다.',
      receiver: '이예진',
      status: 'sent',
      date: '2024-06-09',
    },
  ]);

  const templateList = ref([
    { id: 1, name: '예약 안내', content: '고객님 예약이 확정되었습니다.' },
    { id: 2, name: '시술 전 안내', content: '시술 전 주의사항을 확인해주세요.' },
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
  const showReserveModal = ref(false);

  const showTemplateDrawer = ref(false);
  const showCustomerDrawer = ref(false);

  const selectedCustomers = ref([]);
  const messageToSend = ref('');
  const toast = ref(null);

  const columns = [
    { key: 'title', title: '제목', width: '18%', headerClass: 'text-center' },
    { key: 'content', title: '내용', width: '35%', headerClass: 'text-center' },
    { key: 'receiver', title: '수신자', width: '12%', headerClass: 'text-center' },
    { key: 'status', title: '상태', width: '10%', headerClass: 'text-center' },
    { key: 'date', title: '날짜', width: '15%', headerClass: 'text-center' },
    { key: 'actions', title: '관리', width: '10%', headerClass: 'text-center' },
  ];

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
  const showDetailModal = ref(false);
  const showEditModal = ref(false);
  const messageToEdit = ref(null);

  function handleEditMessage(msg) {
    messageToEdit.value = null;
    nextTick(() => {
      messageToEdit.value = msg;
      showEditModal.value = true;
    });
  }
  function handleShowDetail(msg) {
    selectedMessage.value = msg;
    showDetailModal.value = true;
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

  function handleReserveRequest(content) {
    messageToSend.value = JSON.stringify(content);
    showSendModal.value = false;
    nextTick(() => {
      showReserveModal.value = true;
    });
  }

  function handleSendConfirm() {
    showSendConfirm.value = false;
    nextTick(() => {
      toast.value.success('메시지를 보냈습니다.');
    });
    messageToSend.value = '';
  }

  function handleReserveConfirm({ content, date }) {
    showReserveModal.value = false;
    messageToSend.value = '';
    toast.value.success('예약 요청이 완료되었습니다.');
  }

  function handleOpenTemplateDrawer() {
    showTemplateDrawer.value = true;
  }

  function handleOpenCustomerDrawer() {
    showCustomerDrawer.value = true;
  }

  function handleTemplateSelect(template) {
    messageToSend.value = template.content;
    showTemplateDrawer.value = false;
  }

  function handleCustomerSelect(customers) {
    selectedCustomers.value = customers;
    showCustomerDrawer.value = false;
  }

  function handleEditConfirm(updated) {
    const idx = messages.value.findIndex(m => m.id === updated.id);
    if (idx !== -1) {
      messages.value[idx] = { ...messages.value[idx], ...updated };
    }
    showEditModal.value = false;
    toast.value.success('예약 메시지가 수정되었습니다.');
  }
</script>

<template>
  <div class="message-list-view">
    <div class="message-list-header">
      <h2 class="font-section-title text-dark">보낸 메시지 목록</h2>
    </div>

    <MessageStats :messages="messages" />

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
        메시지 보내기
      </BaseButton>
    </div>

    <BaseCard>
      <BaseTable :columns="columns" :data="paginatedMessages">
        <template #body>
          <MessageItem
            v-for="msg in paginatedMessages"
            :key="msg.id"
            :message="msg"
            @delete="handleDelete"
            @show-detail="handleShowDetail"
            @edit="handleEditMessage"
          />
        </template>
      </BaseTable>
    </BaseCard>

    <Pagination
      v-if="totalPages > 1"
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="filteredMessages.length"
      :items-per-page="itemsPerPage"
      @page-change="page => (currentPage.value = page)"
    />

    <BaseModal
      :model-value="showDeleteConfirm"
      @update:model-value="val => (showDeleteConfirm = val)"
    >
      <template #title>메시지 삭제</template>

      <div class="modal-body">정말 삭제하시겠습니까?</div>

      <template #footer>
        <BaseButton type="primary" @click="cancelDelete">취소</BaseButton>
        <BaseButton type="error" @click="confirmDelete">삭제</BaseButton>
      </template>
    </BaseModal>

    <MessageSendModal
      :model-value="showSendModal"
      :template-content="messageToSend"
      :customers="selectedCustomers"
      @update:model-value="val => (showSendModal = val)"
      @request-send="handleSendRequest"
      @request-reserve="handleReserveRequest"
      @open-template="handleOpenTemplateDrawer"
      @open-customer="handleOpenCustomerDrawer"
    />

    <TemplateSelectDrawer
      v-model="showTemplateDrawer"
      :templates="templateList"
      @select="handleTemplateSelect"
    />

    <CustomerSelectDrawer v-model="showCustomerDrawer" @select="handleCustomerSelect" />

    <ReservationSendModal
      :model-value="showReserveModal"
      :message-content="messageToSend"
      @update:model-value="val => (showReserveModal = val)"
      @confirm="handleReserveConfirm"
    />

    <SendConfirmModal
      :model-value="showSendConfirm"
      @update:model-value="val => (showSendConfirm = val)"
      @confirm="handleSendConfirm"
    />
    <MessageDetailModal
      :model-value="showDetailModal"
      :message="selectedMessage"
      @update:model-value="val => (showDetailModal = val)"
    />

    <EditReservationModal
      v-if="showEditModal && messageToEdit"
      :model-value="showEditModal"
      :message="messageToEdit"
      @update:model-value="val => (showEditModal = val)"
      @confirm="handleEditConfirm"
    />
    <BaseToast ref="toast" />
  </div>
</template>

<style scoped>
  .message-list-view {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
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
</style>

<style>
  table thead th {
    text-align: center !important;
  }
</style>
