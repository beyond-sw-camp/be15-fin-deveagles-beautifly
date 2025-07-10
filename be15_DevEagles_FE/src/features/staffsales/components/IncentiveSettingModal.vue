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
                { text: '직원별 설정', value: 'staff' },
              ]"
            />
          </div>

          <div v-if="mode === 'bulk'" class="section">
            <h4>전체 직원</h4>
            <div class="incentive-group">
              <IncentiveSettingTable v-model="bulk.SERVICE" label="시술 (SERVICE)" />
              <IncentiveSettingTable v-model="bulk.PRODUCT" label="제품 (PRODUCT)" />
              <IncentiveSettingTable v-model="bulk.PREPAID_PASS" label="선불권 (PREPAID_PASS)" />
              <IncentiveSettingTable v-model="bulk.SESSION_PASS" label="횟수권 (SESSION_PASS)" />
            </div>
          </div>

          <div v-else class="section">
            <BaseForm
              v-model="staff.staff"
              type="select"
              label="직원 선택"
              :options="staffOptions"
              placeholder="직원을 선택하세요"
            />
            <div class="incentive-group">
              <IncentiveSettingTable v-model="staff.SERVICE" label="시술 (SERVICE)" />
              <IncentiveSettingTable v-model="staff.PRODUCT" label="제품 (PRODUCT)" />
              <IncentiveSettingTable v-model="staff.PREPAID_PASS" label="선불권 (PREPAID_PASS)" />
              <IncentiveSettingTable v-model="staff.SESSION_PASS" label="횟수권 (SESSION_PASS)" />
            </div>
          </div>
        </div>
      </template>
    </transition>
  </div>
</template>

<script setup>
  import { computed, ref, watch } from 'vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';
  import IncentiveSettingTable from '@/features/staffsales/components/IncentiveSettingTable.vue';

  defineExpose({ handleSave });

  const props = defineProps({
    incentiveData: Object,
  });

  const staffOptions = computed(() => {
    return props.incentiveData.staffList.map(staff => ({
      text: staff.staffName,
      value: staff.staffId,
    }));
  });

  const incentiveEnabled = ref(props.incentiveData.incentiveEnabled);
  const mode = ref(props.incentiveData.incentiveType === 'BULK' ? 'bulk' : 'staff');

  const bulk = ref({
    SERVICE: {},
    PRODUCT: {},
    PREPAID_PASS: {},
    SESSION_PASS: {},
  });

  if (mode.value === 'bulk') {
    const bulkItem = props.incentiveData.incentiveList.find(i => i.staffId === null);
    if (bulkItem) {
      Object.assign(bulk.value, transformIncentivesToProductMap(bulkItem.incentives));
    }
  }

  const staff = ref({
    staff: '',
    SERVICE: {},
    PRODUCT: {},
    PREPAID_PASS: {},
    SESSION_PASS: {},
  });

  watch(
    () => staff.value.staff,
    staffId => {
      const matched = props.incentiveData.incentiveList.find(i => i.staffId === staffId);
      const incentives =
        matched?.incentives ??
        props.incentiveData.incentiveList.find(i => i.staffId === null)?.incentives;

      if (incentives) {
        Object.assign(staff.value, {
          ...staff.value,
          ...transformIncentivesToProductMap(incentives),
        });
      }
    }
  );

  const transformIncentivesToProductMap = incentives => {
    const result = {
      SERVICE: {},
      PRODUCT: {},
      PREPAID_PASS: {},
      SESSION_PASS: {},
    };

    for (const [payMethod, categoryValues] of Object.entries(incentives || {})) {
      if (!categoryValues) continue;

      result.SERVICE[payMethod] = categoryValues.service ?? 0;
      result.PRODUCT[payMethod] = categoryValues.product ?? 0;
      result.PREPAID_PASS[payMethod] = categoryValues.prepaidPass ?? 0;
      result.SESSION_PASS[payMethod] = categoryValues.sessionPass ?? 0;
    }

    return result;
  };

  function handleSave() {
    // TODO: API 연동 또는 부모 emit 등 실제 저장 처리
    console.log('저장 완료:', {
      enabled: incentiveEnabled.value,
      mode: mode.value,
      bulk: bulk.value,
      staff: staff.value,
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
