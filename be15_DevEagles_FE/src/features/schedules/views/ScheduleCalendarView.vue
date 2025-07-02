<template>
  <div class="container">
    <div class="page-header">
      <h1 class="font-screen-title">예약 캘린더</h1>
    </div>

    <div class="filter-bar filter-align-right">
      <input
        v-model="searchText"
        type="text"
        placeholder="고객명 또는 연락처 검색"
        class="input input-search"
      />
      <select v-model="selectedService" class="input input-select">
        <option value="">시술 종류</option>
        <option value="커트">커트</option>
        <option value="염색">염색</option>
        <option value="펌">펌</option>
      </select>
      <select v-model="selectedStaff" class="input input-select">
        <option value="">담당자</option>
        <option value="최민수">최민수</option>
        <option value="이채은">이채은</option>
      </select>
      <select v-model="selectedType" class="input input-select">
        <option value="">스케줄</option>
        <option value="reservation">예약</option>
        <option value="leave">휴무</option>
        <option value="event">일정</option>
      </select>
      <button class="btn btn-primary schedule-btn" @click="handleClickScheduleRegist = true">
        스케줄 등록
      </button>
    </div>
    <div class="calendar-wrapper">
      <ScheduleCalendar
        :schedules="calendarEvents"
        @change-date-range="handleChangeDateRange"
        @click-schedule="handleClickSchedule"
      />
    </div>
    <ReservationDetailModal
      v-if="modalType === 'reservation'"
      v-model="isModalOpen"
      :reservation="selectedReservation"
    />
    <LeaveDetailModal
      v-if="modalType === 'leave'"
      v-model="isModalOpen"
      :reservation="selectedReservation"
    />
    <PlanDetailModal
      v-if="modalType === 'plan'"
      v-model="isModalOpen"
      :reservation="selectedReservation"
    />

    <ScheduleRegistModal v-if="handleClickScheduleRegist" v-model="handleClickScheduleRegist" />
  </div>
</template>

<script setup>
  import { ref, computed, watch, onMounted } from 'vue';
  import ScheduleCalendar from '@/features/schedules/components/ScheduleCalendar.vue';
  import LeaveDetailModal from '@/features/schedules/components/LeaveDetailModal.vue';
  import PlanDetailModal from '@/features/schedules/components/PlanDetailModal.vue';
  import ReservationDetailModal from '@/features/schedules/components/ReservationDetailModal.vue';
  import ScheduleRegistModal from '@/features/schedules/components/ScheduleRegistModal.vue';
  import { getCalendarSchedules } from '@/features/schedules/api/schedules';
  import { useAuthStore } from '@/store/auth';

  const authStore = useAuthStore();
  const shopId = computed(() => authStore.shopId);
  const staffId = computed(() => authStore.userId);

  const searchText = ref('');
  const selectedService = ref('');
  const selectedStaff = ref('');
  const selectedType = ref('');

  const schedules = ref([]);
  const selectedReservation = ref(null);
  const isModalOpen = ref(false);
  const modalType = ref('');
  const handleClickScheduleRegist = ref(false);

  const searchParams = ref({ from: '', to: '' });

  // 날짜를 LocalDateTime 포맷으로 변환하는 유틸 함수
  const formatToLocalDateTime = date => {
    const yyyy = date.getFullYear();
    const MM = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mm = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');
    return `${yyyy}-${MM}-${dd}T${hh}:${mm}:${ss}`;
  };

  const handleChangeDateRange = ({ from, to }) => {
    if (!searchParams.value) return;
    searchParams.value.from = formatToLocalDateTime(new Date(from));
    searchParams.value.to = formatToLocalDateTime(new Date(to));
    fetchSchedules();
  };

  // selectedType.value 값 → 서버 전송용 값으로 변환
  const getScheduleTypeParam = () => {
    switch (selectedType.value) {
      case 'reservation':
        return 'RESERVATION';
      case 'event':
        return 'PLAN_OR_REGULAR';
      case 'leave':
        return 'LEAVE_OR_REGULAR';
      default:
        return null;
    }
  };

  // 일정 상세 클릭 시
  const handleClickSchedule = id => {
    const target = schedules.value.find(item => String(item.id) === String(id));
    if (target) {
      selectedReservation.value = target;
      isModalOpen.value = true;

      if (target.scheduleType === 'RESERVATION') modalType.value = 'reservation';
      else if (target.scheduleType === 'LEAVE') modalType.value = 'leave';
      else if (target.scheduleType === 'PLAN') modalType.value = 'plan';
    }
  };

  // 일정 조회 API 호출
  const fetchSchedules = async () => {
    try {
      const { from, to, customerKeyword, itemKeyword, staffId, scheduleType } = searchParams.value;

      schedules.value = await getCalendarSchedules({
        from,
        to,
        customerKeyword,
        itemKeyword,
        staffId,
        scheduleType,
      });
    } catch (err) {
      console.error('일정 조회 실패', err);
    }
  };

  // 필터 변경 시 일정 재조회
  watch(
    [
      searchText,
      selectedService,
      selectedStaff,
      selectedType,
      () => searchParams.value.from,
      () => searchParams.value.to,
    ],
    fetchSchedules
  );

  // 최초 로딩 시 일정 조회
  onMounted(() => {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1, 0, 0, 0);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0, 23, 59, 59);

    searchParams.value.from = formatToLocalDateTime(firstDay); // ex: "2025-07-01T00:00:00"
    searchParams.value.to = formatToLocalDateTime(lastDay); // ex: "2025-07-31T23:59:59"
    console.log('[params]', searchParams.value);
    fetchSchedules();
  });

  const calendarEvents = computed(() =>
    schedules.value.map(item => ({
      id: item.id,
      title: item.title,
      start: item.startAt,
      end: item.endAt,
      allDay: item.scheduleType === 'LEAVE',
      backgroundColor: item.staffColor || 'var(--color-gray-300)',
      textColor: 'var(--color-text-primary)',
      type: item.scheduleType.toLowerCase(),
      status: item.status,
      staffName: item.staffName,
      customer: item.customerName,
      service: item.items,
      memo: item.memo,
      staffColor: item.staffColor,
    }))
  );
</script>

<style scoped>
  .calendar-wrapper {
    background-color: var(--color-neutral-white);
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  }

  .container {
    padding: 14px;
  }

  .page-header {
    margin-bottom: 32px;
  }

  .filter-bar {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 32px;
    justify-content: flex-end;
  }

  .input-search {
    width: 240px;
  }

  .input-select {
    width: 160px;
  }

  .schedule-btn {
    flex-shrink: 0;
  }
</style>
