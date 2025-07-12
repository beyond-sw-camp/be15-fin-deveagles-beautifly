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
        <option value="plan">일정</option>
        <option value="regular_leave">정기 휴무</option>
        <option value="regular_plan">정기 일정</option>
      </select>
      <button class="btn btn-primary schedule-btn" @click="handleClickScheduleRegist = true">
        스케줄 등록
      </button>
    </div>
    <div class="calendar-wrapper">
      <ScheduleCalendar
        ref="calendarRef"
        :schedules="calendarEvents"
        @change-date-range="handleChangeDateRange"
        @click-schedule="handleClickSchedule"
      />
    </div>
    <ReservationDetailModal
      v-if="modalType === 'reservation'"
      :id="selectedReservation"
      v-model="isModalOpen"
    />

    <PlanDetailModal
      v-if="['plan', 'regular_plan'].includes(modalType)"
      :id="selectedReservation?.id"
      :model-value="isModalOpen"
      :type="selectedReservation?.type"
      @update:model-value="isModalOpen = false"
    />

    <LeaveDetailModal
      v-if="['leave', 'regular_leave'].includes(modalType)"
      :id="selectedReservation?.id"
      :model-value="isModalOpen"
      :type="selectedReservation?.type"
      @update:model-value="isModalOpen = false"
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
  import { getCalendarSchedules, getRegularSchedules } from '@/features/schedules/api/schedules';
  import dayjs from 'dayjs';

  const calendarRef = ref(null);

  const searchText = ref('');
  const selectedService = ref('');
  const selectedStaff = ref('');
  const selectedType = ref('');
  const schedules = ref([]);
  const regularSchedules = ref([]);
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
  };

  const getScheduleTypeParam = () => {
    switch (selectedType.value) {
      case 'reservation':
        return 'RESERVATION';
      case 'plan':
        return 'PLAN';
      case 'leave':
        return 'LEAVE';
      case 'regular_plan':
        return 'REGULAR_PLAN';
      case 'regular_leave':
        return 'REGULAR_LEAVE';
      default:
        return null;
    }
  };
  const fetchRegularSchedules = async () => {
    try {
      regularSchedules.value = [];
      const { from, to } = searchParams.value;

      const [regularPlan, regularLeave] = await Promise.all([
        getRegularSchedules({
          from: from?.split('T')[0],
          to: to?.split('T')[0],
          scheduleType: 'REGULAR_PLAN',
        }),
        getRegularSchedules({
          from: from?.split('T')[0],
          to: to?.split('T')[0],
          scheduleType: 'REGULAR_LEAVE',
        }),
      ]);

      regularSchedules.value = [...regularPlan, ...regularLeave];
    } catch (error) {
      console.error('정기 일정/휴무 조회 실패', error);
    }
  };

  // 일정 상세 클릭 시
  const handleClickSchedule = id => {
    let target =
      schedules.value.find(item => String(item.id) === String(id)) ||
      regularSchedules.value.find(item => {
        const composedId = `${item.id}-${dayjs(item.startAt).format('YYYYMMDD')}`;
        return composedId === String(id);
      });

    if (!target) return;

    isModalOpen.value = true;

    switch (target.scheduleType) {
      case 'RESERVATION':
        selectedReservation.value = target.id;
        modalType.value = 'reservation';
        break;
      case 'LEAVE':
      case 'REGULAR_LEAVE':
        selectedReservation.value = {
          id: target.id,
          type: target.scheduleType.toLowerCase(),
        };
        modalType.value = 'leave';
        break;
      case 'PLAN':
      case 'REGULAR_PLAN':
        selectedReservation.value = {
          id: target.id,
          type: target.scheduleType.toLowerCase(),
        };
        modalType.value = 'plan';
        break;
      default:
        isModalOpen.value = false;
        break;
    }
    console.log('🧪 selectedReservation', selectedReservation.value);
    console.log('🧪 modalType', modalType.value);
  };

  // 일정 조회 API 호출
  const fetchSchedules = async () => {
    try {
      const { from, to } = searchParams.value;
      const scheduleType = getScheduleTypeParam();

      schedules.value = await getCalendarSchedules({
        from,
        to,
        customerKeyword: searchText.value,
        itemKeyword: selectedService.value,
        staffId: selectedStaff.value,
        scheduleType,
      });

      await fetchRegularSchedules();
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
    () => {
      const from = new Date(searchParams.value.from);
      const to = new Date(searchParams.value.to);

      searchParams.value.from = formatToLocalDateTime(new Date(from.setHours(0, 0, 0, 0)));
      searchParams.value.to = formatToLocalDateTime(new Date(to.setHours(23, 59, 59, 999)));

      fetchSchedules();
    }
  );

  // 최초 로딩 시 일정 조회
  onMounted(() => {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1, 0, 0, 0);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0, 23, 59, 59);

    searchParams.value.from = formatToLocalDateTime(firstDay);
    searchParams.value.to = formatToLocalDateTime(lastDay);

    fetchSchedules();
  });

  const calendarEvents = computed(() => {
    const selected = selectedType.value;

    const normalEvents = schedules.value
      .filter(item => !selected || item.scheduleType.toLowerCase() === selected)
      .map(item => {
        const type = item.scheduleType.toLowerCase();
        const isAllDay = type === 'leave';

        const startDate = item.startAt.split('T')[0];
        const endDate = dayjs(item.startAt).add(1, 'day').format('YYYY-MM-DD');

        return {
          id: item.id,
          title: item.title,
          start: isAllDay ? startDate : item.startAt,
          end: isAllDay ? endDate : item.endAt,
          allDay: isAllDay,
          backgroundColor: item.staffColor || 'var(--color-gray-300)',
          textColor: 'var(--color-text-primary)',
          type,
          status: item.status,
          staffName: item.staffName,
          customer: item.customerName ?? '미등록 고객',
          service: item.items,
          memo: item.memo,
          staffColor: item.staffColor,
        };
      });

    const regularEvents = regularSchedules.value
      .filter(item => !selected || item.scheduleType.toLowerCase() === selected)
      .map(item => {
        const type = item.scheduleType.toLowerCase();
        const isAllDay = type === 'regular_leave';

        const startDate = item.startAt.split('T')[0];
        const endDate = dayjs(item.startAt).add(1, 'day').format('YYYY-MM-DD');

        return {
          id: `${item.id}-${dayjs(item.startAt).format('YYYYMMDD')}`,
          title: item.title,
          start: isAllDay ? startDate : item.startAt,
          end: isAllDay ? endDate : item.endAt,
          allDay: isAllDay,
          backgroundColor: item.staffColor || 'var(--color-gray-200)',
          textColor: 'var(--color-text-primary)',
          type,
          staffName: item.staffName,
          memo: item.memo,
          staffColor: item.staffColor,
        };
      });

    return [...normalEvents, ...regularEvents];
  });
</script>

<style scoped>
  .calendar-wrapper {
    background-color: var(--color-neutral-white);
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
    width: 100%;
  }

  .container {
    padding: 14px;
    height: auto;
    overflow: visible;
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
