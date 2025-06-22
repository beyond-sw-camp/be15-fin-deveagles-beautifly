<template>
  <div class="incentive-modal">
    <div class="toggle-row">
      <span>인센티브 설정</span>
      <BaseToggleSwitch v-model="incentiveEnabled" />
    </div>
    <transition name="slide-fade">
      <template v-if="incentiveEnabled">
        <div class="incentive-section">
          <div class="radio-wrapper">
            <BaseForm
              v-model="mode"
              type="radio"
              :options="[
                { text: '일괄설정', value: 'bulk' },
                { text: '직원별 설정', value: 'individual' },
              ]"
            />
          </div>

          <div v-if="mode === 'bulk'" class="section">
            <h4>전체 직원</h4>
            <div class="incentive-group">
              <IncentiveSettingTable v-model="bulk.treatment" label="시술 판매 인센티브" />
              <IncentiveSettingTable v-model="bulk.membership" label="회원권 판매 인센티브" />
            </div>
          </div>

          <div v-else class="section">
            <BaseForm
              v-model="individual.staff"
              type="select"
              label="직원 선택"
              :options="staffOptions"
              placeholder="직원을 선택하세요"
            />
            <div class="incentive-group">
              <IncentiveSettingTable v-model="individual.treatment" label="시술 판매 인센티브" />
              <IncentiveSettingTable v-model="individual.membership" label="회원권 판매 인센티브" />
            </div>
          </div>
        </div>
      </template>
    </transition>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';
  import IncentiveSettingTable from '@/features/staffsales/components/IncentiveSettingTable.vue';

  defineExpose({ handleSave });

  const incentiveEnabled = ref(true);
  const mode = ref('bulk');

  const bulk = ref({
    treatment: {},
    membership: {},
  });

  const individual = ref({
    staff: '',
    treatment: {},
    membership: {},
  });

  const staffOptions = [
    { text: '홍길동', value: '홍길동' },
    { text: '이수민', value: '이수민' },
    { text: '박민준', value: '박민준' },
    { text: '김지연', value: '김지연' },
    { text: '최윤호', value: '최윤호' },
  ];

  function handleSave() {
    // TODO: API 연동 또는 부모 emit 등 실제 저장 처리
    console.log('저장 완료:', {
      enabled: incentiveEnabled.value,
      mode: mode.value,
      bulk: bulk.value,
      individual: individual.value,
    });
  }
</script>

<style scoped>
  .incentive-modal {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  .modal-title {
    font-size: 20px;
    font-weight: bold;
  }
  .toggle-row {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .section {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .incentive-group {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  .radio-wrapper {
    display: flex;
    align-items: center;
    height: 40px;
    font-size: 13px;
  }
  .radio-wrapper label {
    font-size: 13px;
    margin: 0;
  }
  .radio-wrapper :deep(.radio-group) {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 16px;
  }
  .radio-wrapper :deep(.radio-group .radio) {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  .radio-wrapper :deep(.radio-group label),
  .radio-wrapper :deep(.radio-group input[type='radio']) {
    font-size: 13px;
  }
  .slide-fade-enter-active,
  .slide-fade-leave-active {
    transition: all 0.3s ease;
    overflow: hidden;
  }

  .slide-fade-enter-from,
  .slide-fade-leave-to {
    max-height: 0;
    opacity: 0;
  }

  .slide-fade-enter-to,
  .slide-fade-leave-from {
    max-height: 1000px; /* 충분히 큰 값 */
    opacity: 1;
  }

  .footer-btn-row {
    display: flex;
    justify-content: flex-end;
  }
</style>
