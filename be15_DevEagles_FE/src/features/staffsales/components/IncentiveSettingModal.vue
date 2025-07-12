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
              @update:model-value="v => emit('update:selectedStaffId', v)"
            />
            <div v-if="selectedStaffId" class="incentive-group">
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
  import { getIncentives, setIncentive } from '@/features/staffsales/api/staffsales.js';

  const props = defineProps({
    incentiveData: Object,
    toast: Object,
    selectedStaffId: Number,
  });

  const emit = defineEmits(['saved', 'update:selectedStaffId']);

  const staffOptions = computed(
    () =>
      props.incentiveData?.staffList?.map(i => ({
        text: i.staffName ?? '기본값',
        value: i.staffId,
      })) ?? []
  );

  const incentiveEnabled = ref(props.incentiveData.incentiveEnabled);
  const mode = ref(props.incentiveData.incentiveType === 'BULK' ? 'bulk' : 'staff');

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

  const handleSave = async () => {
    const basePayload = {
      isActive: incentiveEnabled.value,
    };
    try {
      if (!incentiveEnabled.value) {
        await setIncentive(basePayload);
        props.toast?.success?.('인센티브 설정이 완료되었습니다.');
        return;
      }
      const type = mode.value === 'bulk' ? 'BULK' : 'STAFF';
      const target = mode.value === 'bulk' ? bulk.value : staff.value;

      const incentives = {};

      for (const category of ['SERVICE', 'PRODUCT', 'SESSION_PASS', 'PREPAID_PASS']) {
        const categoryKey = category.toLowerCase(); // 깔끔하게 미리 처리
        const categoryData = target[category] || {};

        for (const method of Object.keys(categoryData)) {
          if (!incentives[method]) {
            incentives[method] = {};
          }
          incentives[method][categoryKey] = categoryData[method];
        }
      }

      const incentiveInfo = {
        staffId: mode.value === 'staff' ? target.staff : null,
        incentives,
      };

      const payload = {
        ...basePayload,
        type,
        incentiveInfo,
      };
      await setIncentive(payload);
      const updated = await getIncentives();
      emit('saved', updated);
      props.toast?.success?.('인센티브 설정이 완료되었습니다.');
    } catch (err) {
      const message = err.message !== null ? err.message : '인센티브 설정에 실패했습니다.';
      props.toast?.error?.(message);
      console.error(`인센티브 설정 실패`, err);
    }
  };

  watch(
    () => props.selectedStaffId,
    staffId => {
      if (!staffId) return;

      const list = props.incentiveData?.incentiveList ?? [];
      const matched = list.find(i => i.staffId === staffId);
      const fallback = list.find(i => i.staffId === null);

      const incentives = matched?.incentives ?? fallback?.incentives;
      if (!incentives) return;

      const mapped = transformIncentivesToProductMap(incentives);
      staff.value = {
        staff: staffId,
        ...mapped,
      };

      console.log('mapped', mapped);
      console.log('staff.value', staff.value);
    },
    { immediate: true }
  );
  defineExpose({ handleSave });
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
