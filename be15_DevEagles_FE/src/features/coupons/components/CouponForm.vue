<template>
  <form class="coupon-form" @submit.prevent="handleSubmit">
    <!-- 쿠폰명 (전체 너비) -->
    <BaseForm
      id="couponName"
      v-model="formData.name"
      label="쿠폰명*"
      type="text"
      placeholder="쿠폰명을 입력하세요"
      :error="errors.name"
      required
      @enter-key="handleFormSubmit"
    />

    <!-- 카테고리 & 디자이너 (2열) -->
    <div class="form-row">
      <BaseForm
        id="designer"
        v-model="formData.staffId"
        label="디자이너"
        type="select"
        placeholder="디자이너를 선택하세요"
        :options="staffOptions"
        :error="errors.designer"
        style="width: 96%"
      />
    </div>

    <!-- 1차 상품 & 2차 상품 (2열) -->
    <div class="form-row">
      <BaseForm
        id="primaryProduct"
        v-model="formData.primaryItemId"
        label="1차 상품*"
        type="select"
        placeholder="1차 상품을 선택하세요"
        :options="primaryProductOptions"
        :error="errors.primaryProduct"
        required
        style="width: 96%"
      />
      <BaseForm
        id="secondaryProduct"
        v-model="formData.secondaryItemId"
        label="2차 상품"
        type="select"
        placeholder="2차 상품을 선택하세요 (선택사항)"
        :options="secondaryProductOptions"
        :error="errors.secondaryProduct"
        style="width: 96%"
      />
    </div>

    <!-- 할인율 & 활성화 설정 (2열) -->
    <div class="form-row">
      <div class="form-group">
        <label class="form-label">할인율* ({{ formData.discount }}%)</label>
        <div class="slider-container">
          <input
            v-model="formData.discount"
            type="range"
            min="0"
            max="100"
            step="5"
            class="discount-slider"
          />
          <div class="slider-labels">
            <span>0%</span>
            <span>50%</span>
            <span>100%</span>
          </div>
        </div>
        <div v-if="errors.discount" class="error-message">{{ errors.discount }}</div>
      </div>
      <div class="form-group">
        <label class="form-label">활성화 설정</label>
        <div class="checkbox-wrapper">
          <div class="checkbox">
            <input id="isActive" v-model="formData.isActive" type="checkbox" />
            <label for="isActive">쿠폰을 즉시 활성화</label>
          </div>
        </div>
      </div>
    </div>

    <!-- 만료일 (한 행) -->
    <div class="form-group">
      <div class="expiry-date-single-row">
        <label class="form-label inline-label">만료일*</label>

        <div class="quick-selection-inline">
          <button
            v-for="period in datePresets"
            :key="period.value"
            type="button"
            class="quick-date-btn inline"
            :class="{ active: selectedPreset === period.value }"
            @click="setDatePreset(period.value)"
          >
            {{ period.text }}
          </button>
        </div>

        <div class="date-picker-wrapper compact">
          <PrimeDatePicker
            v-model="formData.expiryDate"
            placeholder="직접 선택"
            :show-button-bar="true"
            :min-date="new Date()"
            :error="errors.expiryDate"
            clearable
            @change="onCustomDateChange"
          />
        </div>
      </div>

      <div v-if="errors.expiryDate" class="error-message">{{ errors.expiryDate }}</div>
    </div>

    <!-- 버튼들 -->
    <div class="form-actions">
      <BaseButton type="secondary" outline style="flex: 1" html-type="button" @click="handleCancel">
        취소
      </BaseButton>
      <BaseButton type="primary" style="flex: 1" html-type="submit">
        {{ isEditMode ? '수정' : '생성' }}
      </BaseButton>
    </div>
  </form>
</template>

<script>
  import { ref, computed, watch, onMounted, nextTick } from 'vue';
  import { useMetadataStore } from '@/store/metadata.js';
  import { getPrimaryItems, getAllSecondaryItems } from '@/features/items/api/items.js';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';

  export default {
    name: 'CouponForm',
    components: {
      BaseForm,
      BaseButton,
      PrimeDatePicker,
    },
    props: {
      couponData: {
        type: Object,
        default: null,
      },
      isEditMode: {
        type: Boolean,
        default: false,
      },
    },
    emits: ['save', 'cancel', 'setBeforeClose'],
    setup(props, { emit }) {
      // 동적 옵션
      const metadataStore = useMetadataStore();
      const staffOptions = computed(() => [
        { value: null, text: '전체 적용' },
        ...metadataStore.staff.map(s => ({ value: s.id, text: s.name })),
      ]);
      const primaryProductOptions = ref([]);
      const secondaryProductOptions = ref([]);
      const categoryOptions = [
        { value: '시술', text: '시술' },
        { value: '상품', text: '상품' },
      ];

      // 폼 상태
      const formData = ref({
        name: '',
        staffId: null,
        primaryItemId: null,
        secondaryItemId: null,
        discount: 10,
        expiryDate: '',
        isActive: false,
      });
      const errors = ref({});
      const selectedPreset = ref(null);
      const initialFormData = ref({});

      const datePresets = [
        { value: 1, text: '1개월' },
        { value: 3, text: '3개월' },
        { value: 6, text: '6개월' },
        { value: 12, text: '12개월' },
        { value: 24, text: '24개월' },
      ];

      watch(
        () => props.couponData,
        newData => {
          if (newData && props.isEditMode) {
            formData.value = {
              id: newData.id,
              name: newData.name || '',
              staffId: newData.staffId || null,
              primaryItemId: newData.primaryItemId || null,
              secondaryItemId: newData.secondaryItemId || null,
              discount: newData.discount || 10,
              expiryDate: newData.expiryDate || '',
              isActive: newData.isActive || false,
            };
            selectedPreset.value = null;
            nextTick(() => {
              saveInitialState();
            });
          } else if (!props.isEditMode) {
            resetForm();
          }
        }
      );

      const loadOptions = async () => {
        await metadataStore.loadMetadata();
        const primary = await getPrimaryItems();
        primaryProductOptions.value = primary.map(item => ({
          value: item.primaryItemId,
          text: item.primaryItemName,
        }));
        const secondary = await getAllSecondaryItems();
        secondaryProductOptions.value = secondary.map(item => ({
          value: item.secondaryItemId,
          text: item.secondaryItemName,
        }));
      };
      onMounted(loadOptions);

      const transformToBackendFormat = data => ({
        name: data.name,
        discount: data.discount !== null ? Number(data.discount) : null,
        expiryDate: data.expiryDate,
        isActive: data.isActive,
        primaryItemId: data.primaryItemId !== null ? Number(data.primaryItemId) : null,
        secondaryItemId: data.secondaryItemId !== null ? Number(data.secondaryItemId) : null,
        staffId: data.staffId !== null ? Number(data.staffId) : null,
      });

      const validateForm = () => {
        errors.value = {};

        if (!formData.value.name.trim()) {
          errors.value.name = '쿠폰명은 필수입니다.';
        }

        if (!formData.value.primaryItemId) {
          errors.value.primaryProduct = '1차 상품 선택은 필수입니다.';
        }

        if (!formData.value.expiryDate) {
          errors.value.expiryDate = '만료일은 필수입니다.';
        }

        if (formData.value.discount < 0 || formData.value.discount > 100) {
          errors.value.discount = '할인율은 0%에서 100% 사이여야 합니다.';
        }

        // 만료일이 오늘보다 이후인지 확인
        if (formData.value.expiryDate) {
          const today = new Date();
          const expiryDate = new Date(formData.value.expiryDate);
          today.setHours(0, 0, 0, 0);
          expiryDate.setHours(0, 0, 0, 0);

          if (expiryDate <= today) {
            errors.value.expiryDate = '만료일은 오늘 이후여야 합니다.';
          }
        }

        return Object.keys(errors.value).length === 0;
      };

      // 폼 제출 핸들러
      const handleSubmit = () => {
        if (validateForm()) {
          const couponData = transformToBackendFormat(formData.value);
          emit('save', couponData);
        }
      };

      // 취소 핸들러
      const handleCancel = () => {
        if (hasChanges.value) {
          if (
            confirm(
              '작성하신 내용이 있습니다. 정말로 취소하시겠습니까?\n변경사항이 저장되지 않습니다.'
            )
          ) {
            emit('cancel');
          }
        } else {
          emit('cancel');
        }
      };

      // 창 닫기 전 확인 (BaseWindow의 ESC/백드랍 클릭 처리용)
      const checkBeforeClose = () => {
        if (hasChanges.value) {
          return confirm(
            '작성하신 내용이 있습니다. 정말로 취소하시겠습니까?\n변경사항이 저장되지 않습니다.'
          );
        }
        return true;
      };

      // 빠른 날짜 선택 설정
      const setDatePreset = months => {
        selectedPreset.value = months;
        const today = new Date();
        const futureDate = new Date(today);
        futureDate.setMonth(futureDate.getMonth() + months);
        futureDate.setHours(0, 0, 0, 0); // 해당 날짜의 시작 시간으로 설정

        formData.value.expiryDate = futureDate;

        // 유효성 검사 에러 초기화
        if (errors.value.expiryDate) {
          delete errors.value.expiryDate;
        }
      };

      // 사용자 정의 날짜 변경 시 빠른 선택 해제
      const onCustomDateChange = () => {
        selectedPreset.value = null;

        // 유효성 검사 에러 초기화
        if (errors.value.expiryDate) {
          delete errors.value.expiryDate;
        }
      };

      // 폼 초기화
      const resetForm = () => {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(0, 0, 0, 0);

        formData.value = {
          name: '',
          staffId: null,
          primaryItemId: null,
          secondaryItemId: null,
          discount: 10,
          expiryDate: null, // 기본값을 null로 설정
          isActive: false,
        };
        errors.value = {};
        selectedPreset.value = null;
        saveInitialState();
      };

      // 초기 상태 저장
      const saveInitialState = () => {
        initialFormData.value = { ...formData.value };
      };

      // 폼 데이터에 변경사항이 있는지 확인
      const hasChanges = computed(() => {
        if (!initialFormData.value || Object.keys(initialFormData.value).length === 0) {
          return false;
        }

        // 주요 필드들만 체크 (discount는 기본값 10이므로 제외)
        const fieldsToCheck = [
          'name',
          'staffId',
          'primaryItemId',
          'secondaryItemId',
          'expiryDate',
          'isActive',
        ];

        return fieldsToCheck.some(field => {
          if (field === 'expiryDate') {
            // 날짜는 toString()으로 비교
            const current = formData.value[field] ? formData.value[field].toString() : '';
            const initial = initialFormData.value[field]
              ? initialFormData.value[field].toString()
              : '';
            return current !== initial;
          }
          return formData.value[field] !== initialFormData.value[field];
        });
      });

      // 폼 제출 핸들러 (BaseForm의 @enter-key 이벤트 핸들러)
      const handleFormSubmit = event => {
        event.preventDefault();
        handleSubmit();
      };

      // 부모에게 beforeClose 함수 전달
      onMounted(() => {
        emit('setBeforeClose', checkBeforeClose);
      });

      return {
        formData,
        errors,
        categoryOptions,
        staffOptions,
        primaryProductOptions,
        secondaryProductOptions,
        datePresets,
        selectedPreset,
        hasChanges,
        transformToBackendFormat,
        validateForm,
        handleSubmit,
        handleCancel,
        checkBeforeClose,
        setDatePreset,
        onCustomDateChange,
        resetForm,
        saveInitialState,
        handleFormSubmit,
      };
    },
  };
</script>

<style scoped>
  .coupon-form {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-label {
    font-size: 14px;
    font-weight: 700;
    color: var(--color-gray-700);
  }

  .slider-container {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .discount-slider {
    width: 100%;
    height: 6px;
    border-radius: 3px;
    background: var(--color-gray-200);
    outline: none;
    -webkit-appearance: none;
  }

  .discount-slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: var(--color-primary-main);
    cursor: pointer;
  }

  .discount-slider::-moz-range-thumb {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: var(--color-primary-main);
    cursor: pointer;
    border: none;
  }

  .slider-labels {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--color-gray-500);
  }

  .checkbox-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    justify-content: center;
  }

  .checkbox {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 14px;
    color: var(--color-gray-700);
  }

  .checkbox input[type='checkbox'] {
    width: auto;
    margin: 0;
    accent-color: var(--color-primary-main);
    cursor: pointer;
  }

  .checkbox label {
    cursor: pointer;
    margin: 0;
    font-size: 14px;
    color: var(--color-gray-700);
  }

  .error-message {
    font-size: 12px;
    color: var(--color-error-500);
    margin-top: 0.25rem;
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    margin-top: 1rem;
  }

  /* 만료일 한 줄 레이아웃 */
  .expiry-date-single-row {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    flex-wrap: nowrap;
  }

  .inline-label {
    white-space: nowrap;
    margin: 0;
    min-width: auto;
    flex-shrink: 0;
  }

  .quick-selection-inline {
    display: flex;
    gap: 0.25rem;
    align-items: center;
    flex-shrink: 0;
  }

  .date-picker-wrapper {
    flex: 1;
    min-width: 150px;
  }

  .date-picker-wrapper.compact {
    min-width: 120px;
    max-width: 180px;
  }

  .quick-date-btn.inline {
    padding: 0.25rem 0.5rem;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    background: white;
    color: var(--color-gray-600);
    font-size: 11px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.15s ease;
    min-width: auto;
    white-space: nowrap;
  }

  .quick-date-btn.inline:hover {
    border-color: var(--color-primary-main);
    color: var(--color-primary-main);
  }

  .quick-date-btn.inline.active {
    border-color: var(--color-primary-main);
    background: var(--color-primary-main);
    color: white;
  }

  @media (max-width: 768px) {
    .form-row {
      grid-template-columns: 1fr;
      gap: 0.75rem;
    }

    .form-actions {
      flex-direction: column;
    }

    .expiry-date-single-row {
      flex-direction: column;
      align-items: stretch;
      gap: 0.5rem;
    }

    .inline-label {
      align-self: flex-start;
    }

    .quick-selection-inline {
      gap: 0.2rem;
      justify-content: center;
    }

    .date-picker-wrapper.compact {
      min-width: auto;
      max-width: none;
    }

    .quick-date-btn.inline {
      font-size: 10px;
      padding: 0.2rem 0.4rem;
    }
  }
</style>
