<template>
  <div style="padding: 24px">
    <div class="page-header">
      <h1 class="font-screen-title">예약 목록 전체 조회</h1>
    </div>

    <!-- 필터 영역 -->
    <div class="filter-bar">
      <input
        v-model="searchText"
        type="text"
        placeholder="고객명 또는 연락처 검색"
        class="input input-search"
      />
      <select v-model="selectedDate" class="input input-select">
        <option value="">날짜</option>
        <option value="today">오늘</option>
        <option value="thisWeek">이번 주</option>
        <option value="thisMonth">이번 달</option>
      </select>

      <select v-model="selectedEmployee" class="input input-select">
        <option value="">담당자</option>
        <option value="박미글">박미글</option>
        <option value="이팀장">이팀장</option>
      </select>

      <select v-model="selectedService" class="input input-select">
        <option value="">시술 종류</option>
        <option value="커트">커트</option>
        <option value="염색">염색</option>
        <option value="펌">펌</option>
      </select>

      <select v-model="selectedStatus" class="input input-select">
        <option value="">예약 상태</option>
        <option value="예약 대기">예약 대기</option>
        <option value="예약 확정">예약 확정</option>
        <option value="노쇼">노쇼</option>
        <option value="고객에 의한 예약 취소">고객에 의한 예약 취소</option>
        <option value="가게에 의한 예약 취소">가게에 의한 예약 취소</option>
      </select>
    </div>

    <!-- 테이블 -->
    <BaseTable
      :columns="columns"
      :data="filteredReservations"
      :striped="true"
      :hover="true"
      row-key="id"
    >
      <template #cell-date="{ value }">
        <div class="text-center">{{ formatDate(value) }}</div>
      </template>

      <template #cell-status="{ value }">
        <span
          class="badge"
          :class="{
            'badge-success': value === '예약 확정',
            'badge-warning': value === '예약 대기',
            'badge-error': value.includes('취소') || value === '노쇼',
          }"
        >
          {{ value }}
        </span>
      </template>

      <template #cell-prepaidUsed="{ value }">
        <div class="text-center">{{ value ? '○' : '×' }}</div>
      </template>

      <template #cell-actions="{ item }">
        <div
          v-if="item.status === '예약 대기' || item.status === '예약 확정'"
          class="action-buttons"
        >
          <BaseButton outline type="primary" size="sm" @click="openModal(item, 'confirm')"
            >예약 확정</BaseButton
          >
          <BaseButton outline type="error" size="sm" @click="openModal(item, 'cancel')"
            >예약 취소</BaseButton
          >
        </div>
      </template>
    </BaseTable>

    <BasePagination
      :current-page="1"
      :total-pages="3"
      :total-items="30"
      :items-per-page="10"
      @page-change="page => {}"
      @items-per-page-change="count => {}"
    />

    <BaseModal v-model="isModalOpen" :title="modalTitle">
      <template v-if="modalType === 'confirm'">
        <p style="text-align: center; font-size: 16px">해당 예약을 확정하시겠습니까?</p>
      </template>
      <template v-else>
        <p style="text-align: center; font-size: 16px">예약을 어떤 사유로 취소하시겠습니까?</p>
      </template>

      <template #footer>
        <div style="display: flex; gap: 12px; justify-content: flex-end; flex-wrap: wrap">
          <BaseButton v-if="modalType === 'confirm'" type="primary" @click="onConfirm"
            >예</BaseButton
          >
          <template v-else>
            <BaseButton type="error" @click="confirmCancel('가게에 의한 예약 취소')"
              >가게에 의한 예약 취소</BaseButton
            >
            <BaseButton type="error" @click="confirmCancel('고객에 의한 예약 취소')"
              >고객에 의한 예약 취소</BaseButton
            >
          </template>
          <BaseButton outline @click="onCancel">닫기</BaseButton>
        </div>
      </template>
    </BaseModal>

    <!-- Toast -->
    <BaseToast ref="toast" />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BasePagination from '@/components/common/Pagaination.vue';

  const searchText = ref('');
  const selectedDate = ref('');
  const selectedEmployee = ref('');
  const selectedService = ref('');
  const selectedStatus = ref('');

  const columns = [
    { key: 'name', title: '고객 이름', width: '120px' },
    { key: 'service', title: '시술', width: '100px' },
    { key: 'employee', title: '담당자', width: '100px' },
    { key: 'date', title: '예약 날짜', width: '160px' },
    { key: 'status', title: '예약 상태', width: '140px' },
    { key: 'prepaidUsed', title: '선불권 사용 여부', width: '140px' },
    { key: 'actions', title: '예약 상태 변경', width: '200px' },
  ];

  const reservations = ref([
    {
      id: 1,
      name: '김미글',
      service: '염색',
      employee: '박미글',
      date: '2025-06-08T14:00:00',
      status: '예약 대기',
      prepaidUsed: true,
    },
    {
      id: 2,
      name: '이예정',
      service: '커트',
      employee: '이팀장',
      date: '2025-06-09T11:00:00',
      status: '예약 확정',
      prepaidUsed: false,
    },
    {
      id: 3,
      name: '장현수',
      service: '펌',
      employee: '박미글',
      date: '2025-06-10T15:00:00',
      status: '노쇼',
      prepaidUsed: false,
    },
  ]);

  const filteredReservations = computed(() => {
    const now = new Date();

    return reservations.value.filter(r => {
      const matchText =
        !searchText.value ||
        r.name.includes(searchText.value) ||
        (r.phone && r.phone.includes(searchText.value)); // 연락처 검색 확장 (옵션)

      const matchEmployee = !selectedEmployee.value || r.employee.includes(selectedEmployee.value);

      const matchService = !selectedService.value || r.service.includes(selectedService.value);

      const matchStatus = !selectedStatus.value || r.status === selectedStatus.value;

      const reservationDate = new Date(r.date);
      let matchDate = true;

      if (selectedDate.value === 'today') {
        const today = now.toISOString().split('T')[0];
        const resDate = reservationDate.toISOString().split('T')[0];
        matchDate = today === resDate;
      } else if (selectedDate.value === 'thisWeek') {
        const startOfWeek = new Date(now);
        startOfWeek.setDate(now.getDate() - now.getDay()); // 이번 주 일요일
        startOfWeek.setHours(0, 0, 0, 0);

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6); // 이번 주 토요일
        endOfWeek.setHours(23, 59, 59, 999);

        matchDate = reservationDate >= startOfWeek && reservationDate <= endOfWeek;
      } else if (selectedDate.value === 'thisMonth') {
        matchDate =
          reservationDate.getFullYear() === now.getFullYear() &&
          reservationDate.getMonth() === now.getMonth();
      }

      return matchText && matchEmployee && matchService && matchStatus && matchDate;
    });
  });

  const isModalOpen = ref(false);
  const modalType = ref('confirm');
  const modalTitle = ref('');
  const toast = ref(null);
  let selectedReservation = null;

  function openModal(item, type) {
    selectedReservation = item;
    modalType.value = type;
    modalTitle.value = type === 'confirm' ? '예약 확정' : '예약 취소 사유 선택';
    isModalOpen.value = true;
  }

  function onConfirm() {
    if (!selectedReservation) return;
    selectedReservation.status = '예약 확정';
    toast.value.success('예약이 확정되었습니다.');
    isModalOpen.value = false;
  }

  function confirmCancel(reason) {
    if (!selectedReservation) return;
    selectedReservation.status = reason;
    toast.value.success(`예약이 취소되었습니다.`);
    isModalOpen.value = false;
  }

  function onCancel() {
    isModalOpen.value = false;
  }

  function formatDate(dateStr) {
    const d = new Date(dateStr);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    return `${yyyy}.${mm}.${dd} ${hh}시`;
  }
</script>

<style scoped>
  .page-header {
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .filter-bar {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-wrap: wrap;
    margin-bottom: 24px;
  }

  .input-search {
    width: 200px;
    padding: 8px;
    border-radius: 6px;
    border: 1px solid #ccc;
  }

  .input-select {
    width: 160px;
    padding: 8px;
    border-radius: 6px;
    border: 1px solid #ccc;
  }

  .badge {
    display: inline-block;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
    white-space: nowrap;
    line-height: 1.2;
  }
  .badge-success {
    background-color: #e6f9ed;
    color: #1a7f37;
  }
  .badge-warning {
    background-color: #fff8e1;
    color: #c38e00;
  }
  .badge-error {
    background-color: #fdecea;
    color: #d93025;
  }

  .table td,
  .table th {
    padding: 8px 12px;
    white-space: nowrap;
    vertical-align: middle;
  }

  .action-buttons {
    display: flex;
    gap: 8px;
    flex-wrap: nowrap;
    align-items: center;
  }
</style>
