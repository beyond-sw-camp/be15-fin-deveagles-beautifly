<template>
  <div class="overlay" @click.self="close">
    <div class="modal-panel">
      <!-- 헤더 -->
      <div class="modal-header">
        <h1>스케줄 등록</h1>
        <button class="close-btn" @click="close">&times;</button>
      </div>

      <!-- 바디 -->
      <div class="modal-body">
        <div class="left-detail">
          <div class="tab-buttons">
            <BaseButton :outline="tab !== 'reservation'" @click="tab = 'reservation'"
              >예약</BaseButton
            >
            <BaseButton :outline="tab !== 'plan'" @click="tab = 'plan'">일정</BaseButton>
            <BaseButton :outline="tab !== 'leave'" @click="tab = 'leave'">휴무</BaseButton>
          </div>
          <component :is="currentTabComponent" ref="formRef" />
        </div>
      </div>

      <!-- 푸터 -->
      <div class="modal-footer">
        <BaseButton outline @click="close">등록 취소</BaseButton>
        <BaseButton @click="submit">등록</BaseButton>
      </div>
    </div>
  </div>

  <BaseConfirm
    v-model="showConfirm"
    title="예약 확인"
    message="해당 예약을 등록하시겠습니까?"
    confirm-text="등록"
    cancel-text="취소"
    @confirm="handleConfirm"
  />

  <BaseToast ref="toast" />
</template>

<script setup>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import ReservationForm from './ReservationForm.vue';
  import PlanForm from './PlanForm.vue';
  import LeaveForm from './LeaveForm.vue';
  import { createReservation } from '@/features/schedules/api/schedules.js';
  import BaseConfirm from '@/components/common/BaseConfirm.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import dayjs from 'dayjs';

  const showConfirm = ref(false);
  let confirmCallback = null;

  const formRef = ref();
  const toast = ref(null);

  const props = defineProps({
    modelValue: { type: Boolean, required: true },
    defaultTab: { type: String, default: 'reservation' },
  });

  const emit = defineEmits(['update:modelValue']);

  const tab = ref(props.defaultTab);

  const currentTabComponent = computed(() => {
    switch (tab.value) {
      case 'plan':
        return PlanForm;
      case 'leave':
        return LeaveForm;
      default:
        return ReservationForm;
    }
  });

  const submit = async () => {
    const data = formRef.value?.form;

    if (!data) {
      console.log('formRef or form is undefined');
      return;
    }

    const requiredFields = [data.staffId, data.date, data.startTime, data.endTime];
    const isValid = requiredFields.every(Boolean);

    if (!isValid) {
      toast.value?.error('담당자, 날짜, 시작/종료 시간을 모두 입력해주세요.');
      return;
    }

    confirmCallback = async () => {
      const date = new Date(data.date);
      const startTime = new Date(data.startTime);
      const endTime = new Date(data.endTime);

      await submitReservation(
        {
          ...data,
          date,
          startTime,
          endTime,
        },
        !data.customerId
      );
    };
    showConfirm.value = true;
  };

  const handleConfirm = async () => {
    if (typeof confirmCallback === 'function') {
      await confirmCallback();
    }
  };

  const formatDateTime = (date, time) => {
    if (!(date instanceof Date) || !(time instanceof Date)) return '';

    return dayjs(date)
      .set('hour', dayjs(time).hour())
      .set('minute', dayjs(time).minute())
      .set('second', 0)
      .set('millisecond', 0)
      .format('YYYY-MM-DDTHH:mm:ss');
  };

  const submitReservation = async (formData, isAnonymous) => {
    try {
      const payload = {
        customerId: isAnonymous ? null : formData.customerId,
        staffId: formData.staffId,
        secondaryItemIds: formData.selectedItems?.map(item => item.id) || [],
        reservationDate: dayjs(formData.date).format('YYYY-MM-DD'),
        reservationStartAt: formatDateTime(formData.date, formData.startTime),
        reservationEndAt: formatDateTime(formData.date, formData.endTime),
        reservationMemo: formData.memo || '',
        staffMemo: formData.staffMemo || '',
      };
      await createReservation(payload);

      close();
    } catch (err) {
      toast.value?.error('예약 등록 중 오류가 발생했습니다.');
      console.error(err);
    }
  };

  const close = () => {
    emit('update:modelValue', false);
  };

  const handleEsc = e => {
    if (e.key === 'Escape') close();
  };

  onMounted(() => window.addEventListener('keydown', handleEsc));
  onBeforeUnmount(() => window.removeEventListener('keydown', handleEsc));
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

  .modal-footer {
    margin-top: 32px;
    display: flex;
    flex-direction: row;
    gap: 12px;
    justify-content: flex-end;
  }

  .tab-buttons {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
  }

  .btn-outline.btn-primary {
    background: transparent;
    border: 1px solid var(--color-primary-main);
    color: var(--color-primary-main);
  }

  .btn-outline.btn-primary:hover {
    background: var(--color-primary-50);
  }

  .btn-outline.btn-primary:active {
    background: var(--color-primary-100);
  }
</style>
