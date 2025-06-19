<template>
  <div class="incentive-table">
    <h5 class="table-title">{{ label }}</h5>
    <div class="table-body">
      <div v-for="option in paymentOptions" :key="option" class="incentive-row">
        <span class="payment-label">{{ option }}</span>
        <input
          v-model.number="localValue[option]"
          type="number"
          class="percent-input"
          min="0"
          max="100"
        />
        <span class="unit">%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { watch, reactive } from 'vue';

  const props = defineProps({
    modelValue: {
      type: Object,
      default: () => ({}),
    },
    label: {
      type: String,
      default: '',
    },
  });

  const emit = defineEmits(['update:modelValue']);

  const paymentOptions = ['카드', '현금', '네이버페이', '선불권', '횟수권', '지역화폐'];

  const localValue = reactive({});

  watch(
    () => props.modelValue,
    val => {
      for (const key of paymentOptions) {
        localValue[key] = val?.[key] ?? 0;
      }
    },
    { immediate: true, deep: true }
  );

  watch(
    () => ({ ...localValue }),
    val => {
      emit('update:modelValue', { ...val });
    },
    { deep: true }
  );
</script>

<style scoped>
  .incentive-table {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 12px 16px;
  }
  .table-title {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  .table-body {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  .incentive-row {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .payment-label {
    width: 80px;
    font-size: 13px;
  }
  .percent-input {
    width: 60px;
    padding: 4px;
    border: 1px solid #ccc;
    border-radius: 4px;
    text-align: right;
  }
  .unit {
    font-size: 13px;
  }
</style>
