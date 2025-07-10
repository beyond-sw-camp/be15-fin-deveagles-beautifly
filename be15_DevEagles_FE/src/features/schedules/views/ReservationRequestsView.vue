<template>
  <div class="reservation-wrapper">
    <div class="page-header">
      <h1 class="font-screen-title">예약 신청 목록</h1>
    </div>

    <div class="top-bar">
      <BaseButton
        type="primary"
        @click="openConfirm('선택된 예약을 모두 확정하시겠습니까?', confirmSelected)"
      >
        예약 확정
      </BaseButton>
    </div>

    <div class="base-table-wrapper">
      <BaseTable
        :columns="columns"
        :data="reservationList"
        row-key="id"
        :striped="true"
        :hover="true"
        @row-click="handleRowClick"
      >
        <template #header-checkbox>
          <input v-model="selectAll" type="checkbox" />
        </template>

        <template #cell-checkbox="{ item }">
          <input v-model="selectedIds" type="checkbox" :value="item.id" @click.stop />
        </template>

        <template #cell-actions="{ item }">
          <div class="actions">
            <BaseButton
              outline
              type="primary"
              size="sm"
              @click.stop="
                openConfirm(`예약 ID ${item.id}를 확정하시겠습니까?`, () => confirmSingle(item.id))
              "
            >
              예약 확정
            </BaseButton>
            <BaseButton outline type="error" size="sm" @click.stop="openModal(item, 'cancel')">
              예약 취소
            </BaseButton>
          </div>
        </template>
      </BaseTable>
    </div>

    <Pagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="totalItems"
      :items-per-page="itemsPerPage"
      @page-change="handlePageChange"
      @items-per-page-change="handleItemsPerPageChange"
    />

    <BaseConfirm
      v-model="isConfirmOpen"
      title="예약 확정"
      :message="confirmMessage"
      @confirm="handleConfirm"
      @cancel="handleCancel"
    />

    <BaseModal v-model="isCancelModalOpen" title="예약 취소 사유 선택">
      <p style="text-align: center; font-size: 16px; margin-top: 12px">
        예약을 어떤 사유로 취소하시겠습니까?
      </p>
      <template #footer>
        <div style="display: flex; gap: 12px; justify-content: flex-end; flex-wrap: wrap">
          <BaseButton type="error" @click="confirmCancel('가게에 의한 예약 취소')">
            가게에 의한 예약 취소
          </BaseButton>
          <BaseButton type="error" @click="confirmCancel('고객에 의한 예약 취소')">
            고객에 의한 예약 취소
          </BaseButton>
          <BaseButton outline @click="isCancelModalOpen = false">닫기</BaseButton>
        </div>
      </template>
    </BaseModal>

    <ReservationDetailModal
      v-if="selectedReservation"
      v-model="isDetailOpen"
      :reservation="selectedReservation"
      @cancel-reservation="cancelFromDetail"
    />

    <BaseToast ref="toast" />
  </div>
</template>

<script setup>
  import { ref, computed, onMounted, watch } from 'vue';
  import Pagination from '@/components/common/Pagination.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseConfirm from '@/components/common/BaseConfirm.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import ReservationDetailModal from '@/features/schedules/components/ReservationDetailModal.vue';
  import { fetchReservationRequests } from '@/features/schedules/api/schedules';

  const columns = [
    { key: 'checkbox', title: '', width: '40px' },
    { key: 'customer', title: '고객 이름' },
    { key: 'service', title: '시술' },
    { key: 'employee', title: '담당자' },
    { key: 'date', title: '예약 날짜' },
    { key: 'actions', title: '예약 상태 변경' },
  ];

  const reservationList = ref([]);
  const totalItems = ref(0);
  const currentPage = ref(1);
  const itemsPerPage = ref(10);
  const selectedIds = ref([]);
  const toast = ref();
  const isConfirmOpen = ref(false);
  const confirmMessage = ref('');
  const isCancelModalOpen = ref(false);
  const cancelTarget = ref(null);
  const isDetailOpen = ref(false);
  const selectedReservation = ref(null);

  let confirmCallback = () => {};

  const fetchReservations = async () => {
    const res = await fetchReservationRequests({
      page: currentPage.value - 1,
      size: itemsPerPage.value,
    });

    reservationList.value = res.content.map(item => ({
      id: item.reservationId,
      customer: item.customerName ?? '미등록 고객',
      service: item.itemNames,
      employee: item.staffName,
      date: formatDate(item.reservationStartAt),
    }));
    totalItems.value = res.pagination.totalItems;
  };

  onMounted(fetchReservations);
  watch([currentPage, itemsPerPage], fetchReservations);

  function formatDate(dateStr) {
    const d = new Date(dateStr);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    return `${yyyy}.${mm}.${dd} ${hh}시`;
  }

  const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value));

  const pagedData = computed(() => reservationList.value);

  const selectAll = computed({
    get() {
      return (
        pagedData.value.length > 0 &&
        pagedData.value.every(item => selectedIds.value.includes(item.id))
      );
    },
    set(value) {
      selectedIds.value = value ? pagedData.value.map(item => item.id) : [];
    },
  });

  function handlePageChange(page) {
    currentPage.value = page;
    selectedIds.value = [];
  }

  function handleItemsPerPageChange(count) {
    itemsPerPage.value = count;
    currentPage.value = 1;
    selectedIds.value = [];
  }

  function confirmSelected() {
    reservationList.value = reservationList.value.filter(
      item => !selectedIds.value.includes(item.id)
    );
    toast.value.success(`확정 완료: ${selectedIds.value.join(', ')}`);
    selectedIds.value = [];
  }

  function confirmSingle(id) {
    reservationList.value = reservationList.value.filter(item => item.id !== id);
    toast.value.success(`예약 ID ${id} 확정 완료`);
  }

  function openConfirm(message, callback) {
    if (message.includes('선택된 예약') && selectedIds.value.length === 0) {
      toast.value.warning('예약을 하나 이상 선택해주세요.');
      return;
    }
    confirmMessage.value = message;
    confirmCallback = callback;
    isConfirmOpen.value = true;
  }

  function handleConfirm() {
    confirmCallback();
  }

  function handleCancel() {
    confirmCallback = () => {};
  }

  function openModal(item, type) {
    if (type === 'cancel') {
      cancelTarget.value = item;
      isCancelModalOpen.value = true;
    }
  }

  function confirmCancel(reason) {
    reservationList.value = reservationList.value.filter(item => item.id !== cancelTarget.value.id);
    toast.value.success(`${reason}로 예약이 취소되었습니다.`);
    isCancelModalOpen.value = false;
  }

  function handleRowClick(item) {
    selectedReservation.value = item;
    isDetailOpen.value = true;
  }

  function cancelFromDetail(reservation) {
    reservationList.value = reservationList.value.filter(item => item.id !== reservation.id);
    toast.value.success(`예약 ID ${reservation.id} 취소 완료`);
    isDetailOpen.value = false;
  }
</script>

<style scoped>
  .reservation-wrapper {
    padding: 30px;
  }

  .page-header {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .top-bar {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 20px;
  }

  .base-table-wrapper {
    background-color: var(--color-neutral-white);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 24px;
    box-sizing: border-box;
  }

  .actions {
    display: flex;
    gap: 8px;
    justify-content: flex-start;
  }
</style>
