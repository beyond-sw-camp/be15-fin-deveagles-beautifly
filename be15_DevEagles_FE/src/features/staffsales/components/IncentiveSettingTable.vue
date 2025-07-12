<template>
  <div class="incentive-table">
    <h5 class="table-title">{{ label }}</h5>
    <div class="table-body">
      <div v-for="option in paymentOptions" :key="option" class="incentive-row">
        <span class="payment-label">{{ paymentOptionLabels[option] }}</span>
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
  import { watch, reactive, computed } from 'vue';

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

  const paymentOptionLabels = {
    CARD: '카드',
    CASH: '현금',
    NAVER_PAY: '네이버페이',
    PREPAID_PASS: '선불권',
    SESSION_PASS: '횟수권',
    LOCAL: '지역화폐',
  };

  const productType = computed(() => {
    const match = props.label.match(/\((.*?)\)$/);
    return match ? match[1] : '';
  });

  const paymentOptions = computed(() => {
    const allOptions = Object.keys(paymentOptionLabels);

    if (productType.value === 'PREPAID_PASS' || productType.value === 'SESSION_PASS') {
      return allOptions.filter(opt => opt !== 'PREPAID_PASS' && opt !== 'SESSION_PASS');
    }

    return allOptions;
  });

  const localValue = reactive({});

  watch(
    () => props.modelValue,
    val => {
      for (const key of paymentOptions.value) {
        localValue[key] = val?.[key] ?? 0;
      }
    },
    { immediate: true, deep: true }
  );

  watch(
    localValue,
    val => {
      emit('update:modelValue', { ...val });
    },
    { deep: true }
  );
</script>

<style scoped>
  .incentive-table {
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
  }
  .table-title {
    font-weight: bold;
    margin-bottom: 8px;
    font-size: 16px;
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
