<template>
  <div class="prime-datepicker-wrapper">
    <label v-if="label" class="form-label" :for="inputId">
      {{ label }}
    </label>

    <div class="input-container">
      <DatePicker
        :id="inputId"
        v-model="dateValue"
        :selection-mode="selectionMode"
        :view="view"
        :show-time="showTime"
        :time-only="timeOnly"
        :hour-format="hourFormat"
        :date-format="computedDateFormat"
        :show-button-bar="showButtonBar"
        :show-icon="computedShowIcon"
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
        fluid
        class="prime-datepicker"
        @date-select="onDateSelect"
        @show="$emit('show')"
        @hide="$emit('hide')"
        @month-change="$emit('month-change', $event)"
        @year-change="$emit('year-change', $event)"
      />

      <!-- Clear button -->
      <div
        v-if="clearable && modelValue && !disabled"
        class="clear-icon"
        title="지우기"
        @click="clearValue"
      >
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
      // 라벨과 ID
      label: {
        type: String,
        default: '',
      },
      inputId: {
        type: String,
        default: () => `datepicker-${Math.random().toString(36).substr(2, 9)}`,
      },

      // 선택 모드 - 공식 문서 기준
      selectionMode: {
        type: String,
        default: 'single',
        validator: value => ['single', 'multiple', 'range'].includes(value),
      },

      // 뷰 모드 - 공식 문서 기준
      view: {
        type: String,
        default: 'date',
        validator: value => ['date', 'month', 'year'].includes(value),
      },

      // 시간 관련 - 공식 문서 기준
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

      // 날짜 형식 - 공식 문서 기준
      dateFormat: {
        type: String,
        default: 'yy-mm-dd',
      },

      // UI 옵션들 - 공식 문서 기준
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

      // 제약 조건 - 공식 문서 기준
      minDate: {
        type: Date,
        default: null,
      },
      maxDate: {
        type: Date,
        default: null,
      },

      // 기본 UI 속성들
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

      // 기본 시간 설정
      defaultTime: {
        type: String,
        default: '', // 예: '00:00:00' 또는 '23:59:59'
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
      dateValue: {
        get() {
          return this.modelValue;
        },
        set(value) {
          // 안전한 값 검증
          if (value === null || value === undefined) {
            this.$emit('update:modelValue', null);
            this.$emit('change', null);
          } else if (Array.isArray(value)) {
            // Range/Multiple mode 처리
            const validValue = value.filter(date => date !== null && date !== undefined);
            if (validValue.length === 0) {
              this.$emit('update:modelValue', null);
              this.$emit('change', null);
            } else {
              this.$emit('update:modelValue', value);
              this.$emit('change', value);
            }
          } else {
            this.$emit('update:modelValue', value);
            this.$emit('change', value);
          }
        },
      },

      // 뷰 모드에 따른 날짜 형식 계산
      computedDateFormat() {
        if (this.view === 'month') {
          return 'mm/yy';
        } else if (this.view === 'year') {
          return 'yy';
        } else {
          return this.dateFormat;
        }
      },

      // 뷰 모드에 따른 플레이스홀더 계산
      computedPlaceholder() {
        if (this.view === 'month') {
          return this.placeholder || '월을 선택하세요';
        } else if (this.view === 'year') {
          return this.placeholder || '년도를 선택하세요';
        } else {
          return this.placeholder || '날짜를 선택하세요';
        }
      },

      computedShowIcon() {
        if (this.clearable && this.modelValue && !this.disabled) {
          return false;
        }
        return this.showIcon;
      },
    },

    methods: {
      clearValue() {
        this.dateValue = null;
      },

      // 기본 시간 적용 함수
      applyDefaultTime(date) {
        if (!date || !this.defaultTime || !this.showTime) {
          return date;
        }

        // 배열인 경우 (range/multiple mode)
        if (Array.isArray(date)) {
          return date.map(d => this.setTimeToDate(d));
        }

        // 단일 날짜인 경우
        return this.setTimeToDate(date);
      },

      // 날짜에 기본 시간 설정
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

      // 날짜 선택 이벤트 핸들러
      onDateSelect(event) {
        if (this.defaultTime && this.showTime) {
          const adjustedDate = this.applyDefaultTime(event);
          this.dateValue = adjustedDate;
          this.$emit('date-select', adjustedDate);
        } else {
          this.$emit('date-select', event);
        }
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

  .form-label {
    font-size: 14px;
    font-weight: 700;
    color: var(--color-gray-700, #374151);
  }

  .input-container {
    position: relative;
    display: flex;
    align-items: center;
  }

  .prime-datepicker {
    width: 100%;
  }

  .clear-icon {
    position: absolute;
    right: 8px;
    cursor: pointer;
    color: var(--color-gray-400, #9ca3af);
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    transition: background-color 0.2s ease;
    z-index: 10;
    font-size: 12px;
  }

  .clear-icon:hover {
    background-color: var(--color-gray-100, #f3f4f6);
    color: var(--color-gray-600, #4b5563);
  }

  .error-message {
    font-size: 12px;
    color: var(--color-error-500, #ef4444);
  }
</style>
