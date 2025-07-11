<template>
  <div>
    <div v-if="modelValue && isLoaded">
      <div class="overlay" @click.self="close">
        <div class="modal-panel">
          <div class="modal-header">
            <div>
              <h1>등록된 스케줄</h1>
              <p class="type-label">예약</p>
            </div>
            <button class="close-btn" @click="close">×</button>
          </div>

          <div class="modal-body">
            <div class="left-detail">
              <!-- 고객명 -->
              <div class="row">
                <label>고객명</label>
                <span v-if="!isEditMode">{{ reservation.customerName || '미등록 고객' }}</span>
                <BaseForm v-else v-model="edited.customerName" type="text" />
              </div>
              <!-- 연락처 -->
              <div class="row">
                <label>연락처</label>
                <span v-if="!isEditMode">{{ reservation.customerPhone || '미등록 고객' }}</span>
                <BaseForm v-else v-model="edited.customerPhone" type="text" />
              </div>

              <!-- 예약일 -->
              <div class="row">
                <label>예약일</label>
                <span v-if="!isEditMode">
                  {{ formattedDate }} {{ formattedStartTime }} - {{ formattedEndTime }} ({{
                    reservation.duration
                  }})
                </span>
                <div v-else class="date-time-edit">
                  <div class="row">
                    <div class="date-row-inline">
                      <PrimeDatePicker
                        v-model="edited.date"
                        :show-time="false"
                        :show-button-bar="true"
                        :clearable="false"
                        hour-format="24"
                      />
                      <PrimeDatePicker
                        v-model="edited.startTime"
                        :show-time="true"
                        :time-only="true"
                        :show-button-bar="true"
                        :clearable="false"
                        hour-format="24"
                        placeholder="시간을 선택하세요"
                        @update:model-value="updateDuration"
                      />
                      <PrimeDatePicker
                        v-model="edited.endTime"
                        :show-time="true"
                        :time-only="true"
                        :show-button-bar="true"
                        :clearable="false"
                        hour-format="24"
                        placeholder="시간을 선택하세요"
                        @update:model-value="updateDuration"
                      />
                      <div class="duration-inline">
                        <p>소요 시간:</p>
                        <input class="duration-input" :value="edited.duration" readonly />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 시술 -->
              <div class="row">
                <label>시술</label>
                <span v-if="!isEditMode">{{ reservation.itemNames }}</span>
                <BaseForm v-else v-model="edited.itemNames" type="text" />
              </div>

              <!-- 담당자 -->
              <div class="row row-select">
                <label>담당자</label>
                <div class="form-control-wrapper">
                  <span v-if="!isEditMode">{{ reservation.staffName }}</span>
                  <BaseForm
                    v-else
                    v-model="edited.staffName"
                    type="select"
                    :options="staffOptions"
                    placeholder="담당자 선택"
                  />
                </div>
              </div>

              <!-- 예약 상태 -->
              <div class="row row-select">
                <label>예약 상태</label>
                <div class="form-control-wrapper">
                  <span v-if="!isEditMode">{{ reservationStatusLabel }}</span>
                  <BaseForm
                    v-else
                    v-model="edited.reservationStatusName"
                    type="select"
                    :options="statusOptions"
                    placeholder="예약 상태 선택"
                  />
                </div>
              </div>

              <!-- 특이사항 -->
              <div class="row">
                <label>특이사항</label>
                <span v-if="!isEditMode">{{ reservation.staffMemo }}</span>
                <BaseForm v-else v-model="edited.staffMemo" type="textarea" rows="3" />
              </div>

              <!-- 고객 메모 -->
              <div class="row">
                <label>고객 메모</label>
                <span v-if="!isEditMode">{{ reservation.reservationMemo }}</span>
                <BaseForm v-else v-model="edited.reservationMemo" type="textarea" rows="3" />
              </div>
            </div>

            <!-- 우측 영역 -->
            <div v-if="!readonly" class="right-box">
              <p>고객정보 확인</p>
              <p>매출 등록</p>
            </div>
          </div>

          <!-- 푸터 -->
          <div v-if="!readonly" class="modal-footer">
            <BaseButton type="error" @click="close">닫기</BaseButton>
            <template v-if="isEditMode">
              <BaseButton type="primary" @click="saveEdit">저장</BaseButton>
            </template>
            <template v-else>
              <div class="action-dropdown">
                <BaseButton type="primary" @click="toggleMenu">수정 / 삭제</BaseButton>
                <ul v-if="showMenu" class="dropdown-menu">
                  <li @click="handleEdit">수정하기</li>
                  <li @click="openDeleteConfirm">삭제하기</li>
                </ul>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
  <BaseToast ref="toast" />

  <BaseConfirm
    v-model="showConfirmModal"
    title="예약 삭제"
    message="정말 이 예약을 삭제하시겠습니까?"
    confirm-text="삭제"
    cancel-text="취소"
    confirm-type="error"
    icon-type="error"
    @confirm="handleDelete"
  />
</template>

<script setup>
  import { ref, defineProps, defineEmits, computed, watch, onMounted, onBeforeUnmount } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import { fetchReservationDetail, deleteReservation } from '@/features/schedules/api/schedules.js';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseConfirm from '@/components/common/BaseConfirm.vue';

  const toast = ref(null);
  // ✅ props 정의 및 변수로 선언
  const props = defineProps({
    modelValue: Boolean,
    id: Number,
    readonly: {
      type: Boolean,
      default: false,
    },
  });
  const openDeleteConfirm = () => {
    showMenu.value = false;
    showConfirmModal.value = true;
  };

  // ✅ emits
  const emit = defineEmits(['update:modelValue', 'cancelReservation']);

  // ✅ 내부 상태 변수들
  const reservation = ref({});
  const edited = ref({
    customerName: '',
    customerPhone: '',
    itemNames: '',
    staffName: '',
    reservationStatusName: '',
    staffMemo: '',
    reservationMemo: '',
    date: '',
    startTime: null,
    endTime: null,
    duration: '',
  });
  const isEditMode = ref(false);
  const showMenu = ref(false);
  const isLoaded = ref(false);

  // ✅ 예약 상태, 담당자 옵션
  const statusOptions = [
    { text: '예약 확정', value: 'CONFIRMED' },
    { text: '예약 대기', value: 'PENDING' },
    { text: '노쇼', value: 'NO_SHOW' },
    { text: '고객에 의한 취소', value: 'CBC' },
    { text: '가게에 의한 취소', value: 'CBS' },
    { text: '결제 완료', value: 'PAID' },
  ];

  const reservationStatusMap = {
    CONFIRMED: '예약 확정',
    PENDING: '예약 대기',
    NO_SHOW: '노쇼',
    CBC: '고객 취소',
    CBS: '매장 취소',
    PAID: '결제 완료',
  };

  const reservationStatusLabel = computed(() => {
    const code = reservation.value?.reservationStatusName;
    return reservationStatusMap[code] ?? code ?? '';
  });

  const staffOptions = [
    { text: '디자이너 A', value: '디자이너 A' },
    { text: '디자이너 B', value: '디자이너 B' },
  ];

  // ✅ props 변경 감지하여 상세조회
  watch(
    [() => props.modelValue, () => props.id],
    async ([modelValue, id]) => {
      console.log('🔥 watch triggered');
      console.log('👉 modelValue:', modelValue, '👉 id:', id);

      if (modelValue && id) {
        try {
          const res = await fetchReservationDetail(id);
          console.log('✅ fetchReservationDetail 응답:', res);

          const start = new Date(res.reservationStartAt);
          const end = new Date(res.reservationEndAt);

          reservation.value = {
            ...res,
            duration: calculateDuration(start, end),
          };

          // 🔍 reservation.value 콘솔 확인
          console.log('📦 reservation.value 세팅됨:', reservation.value);

          edited.value = {
            customerName: res.customerName ?? '',
            customerPhone: res.customerPhone ?? '',
            itemNames: res.itemNames ?? '',
            staffName: res.staffName ?? '',
            reservationStatusName: res.reservationStatusName ?? '',
            staffMemo: res.staffMemo ?? '',
            reservationMemo: res.reservationMemo ?? '',
            date: start,
            startTime: start,
            endTime: end,
            duration: calculateDuration(start, end),
          };
          isLoaded.value = true;
        } catch (e) {
          console.error('❌ 상세 조회 실패:', e);
          isLoaded.value = false;
        }
      } else {
        console.warn('⚠️ 조건 불충족 - modelValue or id 없음');
        isLoaded.value = false;
      }
    },
    { immediate: true }
  );

  // ✅ 포맷된 시간 계산용 computed
  const formattedDate = computed(() => {
    return reservation.value.reservationStartAt?.split('T')[0] ?? '';
  });
  const formattedStartTime = computed(() => {
    return reservation.value.reservationStartAt?.split('T')[1]?.slice(0, 5);
  });
  const formattedEndTime = computed(() => {
    return reservation.value.reservationEndAt?.split('T')[1]?.slice(0, 5);
  });

  // ✅ 소요 시간 계산 함수
  const calculateDuration = (start, end) => {
    const diffMs = end - start;
    if (diffMs > 0) {
      const totalMinutes = Math.floor(diffMs / 60000);
      const hours = Math.floor(totalMinutes / 60);
      const minutes = totalMinutes % 60;

      if (hours > 0 && minutes > 0) {
        return `${hours}시간 ${minutes}분 소요`;
      } else if (hours > 0) {
        return `${hours}시간 소요`;
      } else if (minutes > 0) {
        return `${minutes}분 소요`;
      }
    }
    return '';
  };

  onMounted(() => {
    window.addEventListener('keydown', handleKeyDown);
  });

  onBeforeUnmount(() => {
    window.removeEventListener('keydown', handleKeyDown);
  });

  const handleKeyDown = e => {
    if (e.key === 'Escape' || e.key === 'Esc') {
      close();
    }
  };

  // ✅ 이벤트 핸들러
  const close = () => {
    emit('update:modelValue', false);
    isEditMode.value = false;
    showMenu.value = false;
  };

  const toggleMenu = () => (showMenu.value = !showMenu.value);
  const handleEdit = () => {
    const res = reservation.value;
    const start = new Date(res.reservationStartAt);
    const end = new Date(res.reservationEndAt);
    edited.value = {
      customerName: res.customerName ?? '',
      customerPhone: res.customerPhone ?? '',
      itemNames: res.itemNames ?? '',
      staffName: res.staffName ?? '',
      reservationStatusName: res.reservationStatusName ?? '',
      staffMemo: res.staffMemo ?? '',
      reservationMemo: res.reservationMemo ?? '',
      date: start.toISOString().split('T')[0],
      startTime: start,
      endTime: end,
      duration: calculateDuration(start, end),
    };
    isEditMode.value = true;
  };

  const showConfirmModal = ref(false);
  const handleDelete = async () => {
    if (!props.id) return;

    try {
      await deleteReservation(props.id);
      toast.value?.success('삭제가 완료되었습니다.');
      close();
    } catch (e) {
      console.error('❌ 삭제 실패:', e);
      toast.value?.error('삭제 중 오류가 발생했습니다.');
    }
  };

  const saveEdit = () => {
    alert('수정 요청: ' + JSON.stringify(edited.value, null, 2));
    isEditMode.value = false;
  };

  const updateDuration = () => {
    if (edited.value.startTime instanceof Date && edited.value.endTime instanceof Date) {
      edited.value.duration = calculateDuration(edited.value.startTime, edited.value.endTime);
    }
  };
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }

  .modal-panel {
    position: fixed;
    top: 0;
    left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    background: var(--color-neutral-white);
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow-y: auto;
  }

  .date-time-edit {
    display: flex;
    gap: 8px;
    align-items: center;
    flex-wrap: nowrap;
  }

  .date-row-inline {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
    flex: 1;
    margin-left: 24px;
  }

  .duration-inline {
    display: flex;
    align-items: center;
    gap: 4px;
    white-space: nowrap;
  }

  .duration-label {
    font-size: 13px;
    white-space: nowrap;
  }

  .duration-input {
    width: 60px;
    text-align: left;
    padding: 6px 8px;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    margin-left: 4px;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .modal-header h1 {
    font-size: 20px;
    font-weight: bold;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
  }

  .modal-body {
    display: flex;
    gap: 32px;
    flex: 1;
  }

  .left-detail {
    flex: 1;
  }

  .row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 14px;
  }

  .row label {
    width: 100px;
    font-weight: bold;
    color: var(--color-gray-800);
    padding-top: 6px;
    line-height: 1.5;
  }

  .row span,
  .row input,
  .row textarea {
    font-size: 14px;
    line-height: 1.5;
    padding: 6px 8px;
    vertical-align: middle;
    width: 100%;
    max-width: 400px;
    box-sizing: border-box;
  }

  .row input,
  .row textarea {
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
  }

  .row textarea {
    resize: vertical;
  }

  .form-control-wrapper {
    flex: 1;
    display: flex;
    align-items: flex-start;
  }

  .form-control-wrapper :deep(.input) {
    width: 100%;
    max-width: 300px;
  }

  .right-box {
    width: 200px;
    padding: 12px;
    border-left: 1px solid var(--color-gray-200);
  }

  .right-box p {
    margin-bottom: 16px;
    font-weight: 500;
    color: var(--color-gray-700);
  }

  .modal-footer {
    margin-top: 32px;
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }

  .action-dropdown {
    position: relative;
  }

  .dropdown-menu {
    position: absolute;
    bottom: 40px;
    right: 0;
    background: var(--color-neutral-white);
    border: 1px solid var(--color-gray-300);
    border-radius: 6px;
    list-style: none;
    padding: 8px 0;
    width: 120px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
    z-index: 10;
  }

  .dropdown-menu li {
    padding: 8px 12px;
    cursor: pointer;
    color: var(--color-gray-800);
  }

  .dropdown-menu li:hover {
    background: var(--color-gray-100);
  }

  .type-label {
    margin-top: 4px;
    font-size: 18px;
    font-weight: 500;
    color: var(--color-gray-500);
  }
</style>
