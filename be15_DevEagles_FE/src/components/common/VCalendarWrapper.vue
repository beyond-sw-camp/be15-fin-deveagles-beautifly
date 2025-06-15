<template>
  <div class="v-calendar-wrapper">
    <label v-if="label" class="form-label">{{ label }}</label>

    <!-- Date Range Picker -->
    <VDatePicker
      v-if="mode === 'range'"
      v-model.range="dateRange"
      :locale="locale"
      :min-date="minDate"
      :max-date="maxDate"
      :disabled="disabled"
      :popover="popoverOptions"
      :attributes="attributes"
      mode="date"
      is-range
    >
      <template #default="{ inputValue, inputEvents, isDragging }">
        <div class="date-input-wrapper" :class="{ 'is-dragging': isDragging }">
          <input
            :value="inputValue.start"
            class="date-input start-input"
            :class="{ 'has-error': error, 'is-disabled': disabled }"
            :placeholder="startPlaceholder"
            :disabled="disabled"
            v-on="inputEvents.start"
          />
          <span class="date-separator">~</span>
          <input
            :value="inputValue.end"
            class="date-input end-input"
            :class="{ 'has-error': error, 'is-disabled': disabled }"
            :placeholder="endPlaceholder"
            :disabled="disabled"
            v-on="inputEvents.end"
          />
        </div>
      </template>
    </VDatePicker>

    <!-- Single Date Picker -->
    <VDatePicker
      v-else
      v-model="singleDate"
      :locale="locale"
      :min-date="minDate"
      :max-date="maxDate"
      :disabled="disabled"
      :popover="popoverOptions"
      :attributes="attributes"
      :mode="pickerMode"
    >
      <template #default="{ inputValue, inputEvents }">
        <div class="date-input-wrapper">
          <input
            :value="inputValue"
            class="date-input"
            :class="{ 'has-error': error, 'is-disabled': disabled }"
            :placeholder="placeholder"
            :disabled="disabled"
            v-on="inputEvents"
          />
          <div class="input-icon">
            <CalendarIcon />
          </div>
          <div v-if="clearable && modelValue" class="clear-icon" @click="clearValue">
            <XIcon />
          </div>
        </div>
      </template>
    </VDatePicker>

    <!-- Error message -->
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script>
  import { DatePicker as VDatePicker } from 'v-calendar';

  // Simple icons
  const CalendarIcon = {
    template: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
      <line x1="16" y1="2" x2="16" y2="6"/>
      <line x1="8" y1="2" x2="8" y2="6"/>
      <line x1="3" y1="10" x2="21" y2="10"/>
    </svg>`,
  };

  const XIcon = {
    template: `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <line x1="18" y1="6" x2="6" y2="18"/>
      <line x1="6" y1="6" x2="18" y2="18"/>
    </svg>`,
  };

  export default {
    name: 'VCalendarWrapper',
    components: {
      VDatePicker,
      CalendarIcon,
      XIcon,
    },
    props: {
      modelValue: {
        type: [Date, Array, String],
        default: null,
      },
      mode: {
        type: String,
        default: 'date', // date, datetime, range
        validator: value => ['date', 'datetime', 'range'].includes(value),
      },
      label: {
        type: String,
        default: '',
      },
      placeholder: {
        type: String,
        default: '날짜를 선택하세요',
      },
      startPlaceholder: {
        type: String,
        default: '시작일',
      },
      endPlaceholder: {
        type: String,
        default: '종료일',
      },
      minDate: {
        type: Date,
        default: null,
      },
      maxDate: {
        type: Date,
        default: null,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
      clearable: {
        type: Boolean,
        default: true,
      },
      error: {
        type: String,
        default: '',
      },
      locale: {
        type: String,
        default: 'ko',
      },
    },
    emits: ['update:modelValue', 'change'],
    computed: {
      singleDate: {
        get() {
          return this.modelValue;
        },
        set(value) {
          this.$emit('update:modelValue', value);
          this.$emit('change', value);
        },
      },
      dateRange: {
        get() {
          if (this.mode === 'range' && Array.isArray(this.modelValue)) {
            return {
              start: this.modelValue[0],
              end: this.modelValue[1],
            };
          }
          return { start: null, end: null };
        },
        set(value) {
          const rangeArray = value ? [value.start, value.end].filter(Boolean) : null;
          this.$emit('update:modelValue', rangeArray);
          this.$emit('change', rangeArray);
        },
      },
      pickerMode() {
        return this.mode === 'datetime' ? 'dateTime' : 'date';
      },
      popoverOptions() {
        return {
          placement: 'bottom-start',
          visibility: 'click',
        };
      },
      attributes() {
        return [
          {
            key: 'today',
            highlight: {
              color: 'blue',
              fillMode: 'outline',
            },
            dates: new Date(),
          },
        ];
      },
    },
    methods: {
      clearValue() {
        this.$emit('update:modelValue', null);
        this.$emit('change', null);
      },
    },
  };
</script>

<style scoped>
  .v-calendar-wrapper {
    width: 100%;
  }

  .form-label {
    display: block;
    margin-bottom: 0.5rem;
    font-size: 14px;
    font-weight: 700;
    color: var(--color-gray-700);
  }

  .date-input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .date-input-wrapper.is-dragging {
    opacity: 0.6;
  }

  .date-input {
    flex: 1;
    padding: 0.625rem 2.5rem 0.625rem 0.875rem;
    border: 1px solid var(--color-gray-300);
    border-radius: 0.5rem;
    background: var(--color-neutral-white);
    font-size: 14px;
    transition: border-color 0.2s ease;
  }

  .date-input:focus {
    outline: none;
    border-color: var(--color-primary-500);
    box-shadow: 0 0 0 2px rgba(37, 113, 128, 0.2);
  }

  .date-input.has-error {
    border-color: var(--color-error-500);
  }

  .date-input.is-disabled {
    background-color: var(--color-gray-100);
    cursor: not-allowed;
  }

  .start-input,
  .end-input {
    padding-right: 0.875rem;
  }

  .date-separator {
    color: var(--color-gray-500);
    font-weight: 500;
    white-space: nowrap;
  }

  .input-icon {
    position: absolute;
    right: 0.75rem;
    color: var(--color-gray-500);
    pointer-events: none;
  }

  .clear-icon {
    position: absolute;
    right: 2.25rem;
    color: var(--color-gray-400);
    cursor: pointer;
    padding: 2px;
    border-radius: 2px;
    transition: color 0.2s ease;
  }

  .clear-icon:hover {
    color: var(--color-gray-600);
    background-color: var(--color-gray-100);
  }

  .error-message {
    margin-top: 0.25rem;
    font-size: 12px;
    color: var(--color-error-500);
  }

  @media (max-width: 768px) {
    .date-input-wrapper {
      flex-direction: column;
      align-items: stretch;
    }

    .date-separator {
      align-self: center;
      margin: 0.25rem 0;
    }
  }
</style>

<style>
  /* v-calendar 스타일 커스터마이징 */
  .vc-container {
    --vc-color-primary: var(--color-primary-500);
    --vc-color-secondary: var(--color-primary-100);
    --vc-border-color: var(--color-gray-200);
    --vc-nav-item-hover-color: var(--color-gray-100);
    font-family: 'Noto Sans KR', sans-serif;
  }

  .vc-popover-content {
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }

  .vc-highlight {
    border-radius: 4px;
  }

  .vc-day-content {
    font-size: 14px;
    font-weight: 500;
  }

  .vc-nav-title {
    font-weight: 600;
    color: var(--color-neutral-dark);
  }

  .vc-nav-arrow {
    color: var(--color-gray-600);
  }

  .vc-nav-arrow:hover {
    background-color: var(--color-gray-100);
  }

  .vc-weekday {
    font-size: 12px;
    font-weight: 600;
    color: var(--color-gray-600);
  }
</style>
