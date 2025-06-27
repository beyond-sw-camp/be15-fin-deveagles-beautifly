<template>
  <div class="prime-datepicker-wrapper">
    <label v-if="label" class="form-label" :for="inputId">
      {{ label }}
    </label>

    <div class="input-container">
      <DatePicker
        :id="inputId"
        :model-value="modelValue"
        :selection-mode="selectionMode"
        :view="view"
        :show-time="showTime"
        :time-only="timeOnly"
        :hour-format="hourFormat"
        :date-format="computedDateFormat"
        :show-button-bar="showButtonBar"
        :show-icon="showIcon"
        :icon-display="iconDisplay"
        :number-of-months="numberOfMonths"
        :min-date="minDate"
        :max-date="maxDate"
        :placeholder="computedPlaceholder"
        :disabled="disabled"
        :invalid="!!error"
        :manual-input="true"
        :step-hour="1"
        :step-minute="1"
        :step-second="1"
        :base-z-index="baseZIndex"
        fluid
        class="prime-datepicker"
        @update:model-value="onUpdateModelValue"
        @date-select="onDateSelect"
        @show="$emit('show')"
        @hide="$emit('hide')"
        @month-change="$emit('month-change', $event)"
        @year-change="$emit('year-change', $event)"
      />

      <!-- Clear button -->
      <div v-if="shouldShowClearIcon" class="clear-icon" title="닫기" @click.stop="clearValue">
        ✕
      </div>
    </div>

    <!-- Error message -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
  </div>
</template>

<script>
  import DatePicker from 'primevue/datepicker';

  export default {
    name: 'PrimeDatePicker',
    components: {
      DatePicker,
    },
    props: {
      modelValue: {
        type: [Date, Array, String],
        default: null,
      },
      label: {
        type: String,
        default: '',
      },
      inputId: {
        type: String,
        default: () => `datepicker-${Math.random().toString(36).substr(2, 9)}`,
      },
      selectionMode: {
        type: String,
        default: 'single',
        validator: value => ['single', 'multiple', 'range'].includes(value),
      },
      view: {
        type: String,
        default: 'date',
        validator: value => ['date', 'month', 'year'].includes(value),
      },
      showTime: {
        type: Boolean,
        default: false,
      },
      timeOnly: {
        type: Boolean,
        default: false,
      },
      hourFormat: {
        type: String,
        default: '24',
        validator: value => ['12', '24'].includes(value),
      },
      dateFormat: {
        type: String,
        default: 'yy-mm-dd',
      },
      showButtonBar: {
        type: Boolean,
        default: false,
      },
      showIcon: {
        type: Boolean,
        default: true,
      },
      iconDisplay: {
        type: String,
        default: 'input',
        validator: value => ['input', 'button'].includes(value),
      },
      numberOfMonths: {
        type: Number,
        default: 1,
      },
      minDate: {
        type: Date,
        default: null,
      },
      maxDate: {
        type: Date,
        default: null,
      },
      placeholder: {
        type: String,
        default: '날짜를 선택하세요',
      },
      disabled: {
        type: Boolean,
        default: false,
      },
      error: {
        type: String,
        default: '',
      },
      clearable: {
        type: Boolean,
        default: true,
      },
      defaultTime: {
        type: String,
        default: '',
      },
      baseZIndex: {
        type: Number,
        default: 0,
      },
    },
    emits: [
      'update:modelValue',
      'change',
      'date-select',
      'show',
      'hide',
      'month-change',
      'year-change',
    ],

    computed: {
      computedDateFormat() {
        if (this.view === 'month') {
          return 'yy/mm'; //
        } else if (this.view === 'year') {
          return 'yy';
        } else {
          return this.dateFormat;
        }
      },
      computedPlaceholder() {
        if (this.view === 'month') {
          return this.placeholder || '월을 선택하세요';
        } else if (this.view === 'year') {
          return this.placeholder || '년도를 선택하세요';
        } else {
          return this.placeholder || '날짜를 선택하세요';
        }
      },
      shouldShowClearIcon() {
        return this.clearable && this.modelValue && !this.disabled;
      },
    },

    methods: {
      onUpdateModelValue(value) {
        if (this.defaultTime && this.showTime && value) {
          const adjustedValue = this.applyDefaultTime(value);
          this.$emit('update:modelValue', adjustedValue);
          this.$emit('change', adjustedValue);
        } else {
          this.$emit('update:modelValue', value);
          this.$emit('change', value);
        }
      },
      clearValue() {
        this.$emit('update:modelValue', null);
        this.$emit('change', null);
      },
      applyDefaultTime(date) {
        if (!date || !this.defaultTime || !this.showTime) {
          return date;
        }
        if (Array.isArray(date)) {
          return date.map(d => this.setTimeToDate(d));
        }
        return this.setTimeToDate(date);
      },
      setTimeToDate(date) {
        if (!date || !this.defaultTime) return date;
        const newDate = new Date(date);
        const timeParts = this.defaultTime.split(':');
        if (timeParts.length >= 2) {
          const hours = parseInt(timeParts[0]) || 0;
          const minutes = parseInt(timeParts[1]) || 0;
          const seconds = parseInt(timeParts[2]) || 0;
          newDate.setHours(hours, minutes, seconds, 0);
        }
        return newDate;
      },
      onDateSelect(event) {
        this.$emit('date-select', event);
      },
    },
  };
</script>

<style scoped>
  .prime-datepicker-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  .input-container {
    position: relative;
    display: flex;
    align-items: center;
  }
  .prime-datepicker {
    width: 100%;
  }
  .form-label {
    font-size: 14px;
    font-weight: 700;
    color: var(--color-gray-700);
    margin-bottom: 0;
  }
  .clear-icon {
    position: absolute;
    right: 40px;
    top: 50%;
    transform: translateY(-50%);
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-gray-300);
    border-radius: 50%;
    cursor: pointer;
    font-size: 12px;
    color: var(--color-gray-600);
    z-index: 10;
    transition: all 0.2s ease;
  }
  .clear-icon:hover {
    background: var(--color-gray-400);
    color: var(--color-gray-700);
    transform: translateY(-50%) scale(1.1);
  }
  .error-message {
    font-size: 12px;
    color: var(--color-error-500);
    margin-top: 0;
  }
  :deep(.p-datepicker-input) {
    padding-right: 70px !important;
  }
</style>
