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
            { text: '일괄 설정', value: '일괄 설정' },
            { text: '직원별 설정', value: '직원별 설정' },
          ]"
        />
      </div>
    </div>

    <!-- ✅ 일감 설정 -->
    <div v-if="mode === '일괄 설정'" class="form-section">
      <div v-for="(label, key) in categoryLabels" :key="key" class="input-row">
        <label class="form-label">{{ label }}</label>
        <BaseForm v-model="targetForm[key]" type="number" suffix="원" placeholder="0원" />
      </div>
    </div>

    <!-- ✅ 직원별 설정 -->
    <div v-else class="form-section">
      <div class="input-row">
        <label class="form-label">직원 선택</label>
        <BaseForm
          v-model="selectedStaffId"
          type="select"
          :options="staffList.map(staff => ({ text: staff.name, value: staff.id }))"
          placeholder="직원을 선택하세요"
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
  import { reactive, ref } from 'vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseForm from '@/components/common/BaseForm.vue';

  const emit = defineEmits(['close']);
  const emitClose = () => emit('close');

  const month = ref('2025-06');
  const mode = ref('일괄 설정');
  const selectedStaffId = ref(null);

  const categoryLabels = {
    pre: '선불액, 정기권 판매',
    treatment: '상품-시술 판매',
    product: '상품-제품 판매',
  };

  const targetForm = reactive({
    pre: 100000,
    treatment: 100000,
    product: 100000,
  });

  const staffList = [
    { id: 1, name: '김이글' },
    { id: 2, name: '한위니' },
  ];

  const staffTargets = reactive({});

  staffList.forEach(staff => {
    if (!staffTargets[staff.id]) {
      staffTargets[staff.id] = {
        pre: 0,
        treatment: 0,
        product: 0,
      };
    }
  });

  const getStaffName = id => {
    const staff = staffList.find(s => s.id === id);
    return staff ? staff.name : '';
  };

  const handleSubmit = () => {
    const result =
      mode.value === '일괄 설정'
        ? {
            month: month.value,
            mode: mode.value,
            targets: { ...targetForm },
          }
        : {
            month: month.value,
            mode: mode.value,
            targets: { ...staffTargets },
          };

    console.log('저장할 값', result);
    emitClose();
  };
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
