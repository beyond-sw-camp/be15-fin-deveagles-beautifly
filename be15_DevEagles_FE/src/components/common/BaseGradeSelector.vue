<template>
  <div class="grade-selector-wrapper">
    <label v-if="label" class="form-label">{{ label }}</label>
    <select
      v-model="selectedGrade"
      class="grade-select"
      :class="[{ 'select-error': error }, `grade-select-${size}`]"
    >
      <option value="">{{ placeholder }}</option>
      <option v-for="grade in gradeOptions" :key="grade.value" :value="grade.value">
        {{ grade.text }}
      </option>
    </select>
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script>
  export default {
    name: 'BaseGradeSelector',
    props: {
      modelValue: {
        type: String,
        default: '',
      },
      label: {
        type: String,
        default: '',
      },
      placeholder: {
        type: String,
        default: '등급 선택',
      },
      error: {
        type: String,
        default: '',
      },
      size: {
        type: String,
        default: 'md',
        validator: value => ['sm', 'md', 'lg'].includes(value),
      },
      options: {
        type: Array,
        default: () => [
          { value: 'new', text: '신규 고객' },
          { value: 'growing', text: '성장 고객' },
          { value: 'loyal', text: '충성 고객' },
          { value: 'vip', text: 'VIP 고객' },
          { value: 'at_risk', text: '이탈 위험 고객' },
          { value: 'dormant', text: '휴면 고객' },
        ],
      },
    },
    emits: ['update:modelValue'],
    computed: {
      gradeOptions() {
        return this.options;
      },
      selectedGrade: {
        get() {
          return this.modelValue;
        },
        set(value) {
          this.$emit('update:modelValue', value);
        },
      },
    },
  };
</script>

<style scoped>
  .grade-selector-wrapper {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .form-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-neutral-dark);
  }

  .grade-select {
    border: 1px solid var(--color-gray-200);
    border-radius: 8px;
    background: var(--color-neutral-white);
    color: var(--color-neutral-dark);
    transition: border-color 0.2s ease;
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%236b7280' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6,9 12,15 18,9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 12px center;
    background-size: 16px;
    padding-right: 40px;
  }

  /* Size variants */
  .grade-select-sm {
    height: 32px;
    font-size: 12px;
    line-height: 15.6px;
    padding: 0 10px;
    padding-right: 36px;
    background-size: 14px;
    background-position: right 10px center;
  }

  .grade-select-md {
    height: 40px;
    font-size: 14px;
    line-height: 21px;
    padding: 0 12px;
    padding-right: 40px;
  }

  .grade-select-lg {
    height: 48px;
    font-size: 16px;
    line-height: 24px;
    padding: 0 16px;
    padding-right: 48px;
    background-size: 18px;
    background-position: right 16px center;
  }

  .grade-select:focus {
    outline: none;
    border-color: var(--color-primary-main);
    background: var(--color-gray-25);
  }

  .grade-select:hover {
    border-color: var(--color-gray-300);
  }

  .select-error {
    border-color: var(--color-error-300) !important;
  }

  .error-message {
    color: var(--color-error-300);
    font-size: 12px;
    line-height: 15.6px;
    margin-top: 2px;
  }
</style>
