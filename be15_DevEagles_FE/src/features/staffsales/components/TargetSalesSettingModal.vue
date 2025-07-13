<template>
  <div>
    <div class="section">
      <label class="form-label">목표년월</label>
      <PrimeDatePicker v-model="month" view="month" date-format="yy-mm" placeholder="2025-06" />
    </div>

    <div class="section">
      <label class="form-label">목표매출 설정</label>
      <div class="radio-wrapper">
        <BaseForm
          v-model="mode"
          type="radio"
          :options="[
            { text: '일괄 설정', value: 'BULK' },
            { text: '직원별 설정', value: 'STAFF' },
          ]"
        />
      </div>
    </div>

    <!-- 일감 설정 -->
    <div v-if="mode === 'BULK'" class="form-section">
      <div v-for="(label, key) in categoryLabels" :key="key" class="input-row">
        <label class="form-label">{{ label }}</label>
        <BaseForm v-model="targetForm[key]" type="number" suffix="원" placeholder="0원" />
      </div>
    </div>

    <!-- 직원별 설정 -->
    <div v-else class="form-section">
      <div class="input-row">
        <label class="form-label">직원 선택</label>
        <BaseForm
          v-model="selectedStaffId"
          type="select"
          :options="[
            { text: '직원을 선택하세요', value: '', disabled: true },
            ...staffList.map(staff => ({ text: staff.name, value: staff.id })),
          ]"
        />
      </div>

      <div v-if="selectedStaffId !== null" class="staff-section">
        <h4 class="staff-name">{{ getStaffName(selectedStaffId) }}</h4>
        <div v-for="(label, key) in categoryLabels" :key="key" class="input-row">
          <label class="form-label">{{ label }}</label>
          <BaseForm
            :model-value="selectedStaffId !== null ? staffTargets[selectedStaffId][key] : 0"
            type="number"
            suffix="원"
            placeholder="0원"
            @update:model-value="
              val => {
                if (selectedStaffId !== null) {
                  staffTargets[selectedStaffId][key] = val;
                }
              }
            "
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { onMounted, reactive, ref, watch } from 'vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import { getSalesTarget, setSalesTarget } from '@/features/staffsales/api/staffsales.js';
  import { useStaffSales } from '@/features/staffsales/composables/useStaffSales.js';

  const props = defineProps({
    toast: Object,
  });

  const { formatToISODate } = useStaffSales();

  const emit = defineEmits(['close', 'saved']);
  const today = new Date();
  const month = ref(`${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`);
  const mode = ref();
  const selectedStaffId = ref(null);

  const categoryLabels = {
    membership: '회원권(선불권, 정기권) 판매',
    items: '상품(제품, 시술) 판매',
  };
  const targetForm = reactive({
    membership: 0,
    items: 0,
  });
  const staffList = [];
  const staffTargets = reactive({});

  staffList.forEach(staff => {
    if (!staffTargets[staff.id]) {
      staffTargets[staff.id] = {
        membership: 0,
        items: 0,
      };
    }
  });

  const getStaffName = id => {
    const staff = staffList.find(s => s.id === id);
    return staff ? staff.name : '';
  };

  const loadSalesTarget = async targetMonth => {
    if (!targetMonth) return;

    try {
      const res = await getSalesTarget({ targetDate: formatToISODate(`${month.value}-01`) });
      const data = res.data.data;

      // 직원 목록 초기화
      staffList.splice(
        0,
        staffList.length,
        ...data.staffList.map(s => ({
          id: s.staffId,
          name: s.staffName,
        }))
      );

      // 설정 모드
      mode.value = data.type;

      // 일괄 설정
      if (mode.value === 'BULK') {
        const bulk = data.salesTargetInfos[0]?.targetAmounts || {};
        targetForm.membership = bulk.membership || 0;
        targetForm.items = bulk.items || 0;

        // 기본 값
        const bulkTarget = {
          membership: bulk.membership || 0,
          items: bulk.items || 0,
        };

        Object.keys(staffTargets).forEach(k => delete staffTargets[k]);
        data.staffList.forEach(staff => {
          staffTargets[staff.staffId] = { ...bulkTarget };
        });
      }
      // 직원별 설정
      else {
        Object.keys(staffTargets).forEach(k => delete staffTargets[k]);
        data.salesTargetInfos.forEach(info => {
          staffTargets[info.staffId] = {
            membership: info.targetAmounts.membership || 0,
            items: info.targetAmounts.items || 0,
          };
        });
      }
    } catch (err) {
      const message = err.message ? err.message : '목표 매출 조회를 실패했습니다.';
      props.toast.value?.error?.(message);
      console.error('목표 매출 조회 실패:', err);
    }
  };

  const handleSave = async () => {
    try {
      const payload =
        mode.value === 'BULK'
          ? {
              targetDate: formatToISODate(`${month.value}-01`),
              type: 'BULK',
              targetAmounts: {
                membership: Number(targetForm.membership),
                items: Number(targetForm.items),
              },
            }
          : {
              targetDate: formatToISODate(`${month.value}-01`),
              type: 'STAFF',
              staffTargets: Object.entries(staffTargets).map(([staffId, amounts]) => ({
                staffId: Number(staffId),
                targetAmounts: {
                  membership: Number(amounts.membership),
                  items: Number(amounts.items),
                },
              })),
            };

      await setSalesTarget(payload);
      emit('saved');
      props.toast?.success?.('목표 매출이 저장되었습니다.', 'success');
    } catch (err) {
      console.error('저장 실패', err);
      props.toast?.error?.('저장에 실패했습니다.', 'error');
    }
  };

  onMounted(() => {
    if (month.value) {
      loadSalesTarget(month.value);
    }
  });

  watch(month, async newMonth => {
    if (newMonth) {
      await loadSalesTarget(newMonth);
    }
  });

  watch(mode, newMode => {
    if (newMode === 'STAFF') {
      selectedStaffId.value = null;
    }
  });
  defineExpose({ handleSave });
</script>

<style scoped>
  .section {
    margin-bottom: 1.5rem;
  }
  .form-label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
    font-size: 14px;
  }
  .input-row {
    margin-bottom: 1rem;
  }
  .staff-section {
    margin-bottom: 2rem;
  }
  .staff-name {
    margin-bottom: 1rem;
    font-size: 15px;
    font-weight: bold;
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
  .radio-wrapper :deep(.form-group) {
    display: flex;
    align-items: center;
    gap: 16px;
    margin: 0;
    padding: 0;
  }
  .radio-wrapper :deep(.radio-group) {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 16px;
    min-height: 100%;
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
</style>
