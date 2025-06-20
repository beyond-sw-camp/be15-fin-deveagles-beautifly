<template>
  <div class="overlay">
    <div class="modal-panel">
      <div class="modal-header">
        <h1>스케줄 등록</h1>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <div class="left-detail">
          <div class="tab-buttons">
            <BaseButton :outline="tab !== 'reservation'" @click="tab = 'reservation'"
              >예약</BaseButton
            >
            <BaseButton :outline="tab !== 'plan'" @click="tab = 'plan'">일정</BaseButton>
            <BaseButton :outline="tab !== 'leave'" @click="tab = 'leave'">휴무</BaseButton>
          </div>
          <component :is="currentTabComponent" />
        </div>
      </div>

      <div class="modal-footer">
        <BaseButton outline @click="$emit('close')">등록 취소</BaseButton>
        <BaseButton @click="submit">등록</BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import ReservationForm from './ReservationForm.vue';
  import PlanForm from './PlanForm.vue';
  import LeaveForm from './LeaveForm.vue';

  const tab = ref('reservation');

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

  const submit = () => {
    alert(`${tab.value} 등록 로직 실행`);
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
    flex-direction: row;
    gap: 12px;
    justify-content: flex-end;
  }

  .tab-buttons {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
  }
</style>
