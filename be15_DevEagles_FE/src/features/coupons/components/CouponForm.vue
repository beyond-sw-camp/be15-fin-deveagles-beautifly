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
        id="category"
        v-model="formData.category"
        label="카테고리*"
        type="select"
        placeholder="카테고리를 선택하세요"
        :options="categoryOptions"
        :error="errors.category"
        required
      />
      <BaseForm
        id="designer"
        v-model="formData.designer"
        label="디자이너"
        type="select"
        placeholder="디자이너를 선택하세요"
        :options="designerOptions"
        :error="errors.designer"
      />
    </div>

    <!-- 1차 상품 & 2차 상품 (2열) -->
    <div class="form-row">
      <BaseForm
        id="primaryProduct"
        v-model="formData.primaryProduct"
        label="1차 상품*"
        type="select"
        placeholder="1차 상품을 선택하세요"
        :options="primaryProductOptions"
        :error="errors.primaryProduct"
        required
      />
      <BaseForm
        id="secondaryProduct"
        v-model="formData.secondaryProduct"
        label="2차 상품"
        type="select"
        placeholder="2차 상품을 선택하세요 (선택사항)"
        :options="secondaryProductOptions"
        :error="errors.secondaryProduct"
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
    data() {
      return {
        formData: {
          name: '',
          category: '',
          designer: '',
          primaryProduct: '',
          secondaryProduct: '',
          discount: 10,
          expiryDate: '',
          isActive: false,
        },
        errors: {},
        selectedPreset: null, // 현재 선택된 빠른 선택 기간
        initialFormData: {}, // 초기 폼 데이터 상태

        // 날짜 빠른 선택 옵션
        datePresets: [
          { value: 1, text: '1개월' },
          { value: 3, text: '3개월' },
          { value: 6, text: '6개월' },
          { value: 12, text: '12개월' },
          { value: 24, text: '24개월' },
        ],

        // 옵션 데이터
        categoryOptions: [
          { value: '시술', text: '시술' },
          { value: '상품', text: '상품' },
        ],
        designerOptions: [
          { value: '전체', text: '전체 적용' },
          { value: '김미영', text: '김미영' },
          { value: '박지은', text: '박지은' },
          { value: '이수진', text: '이수진' },
          { value: '최민호', text: '최민호' },
          { value: '정하나', text: '정하나' },
        ],
        primaryProductOptions: [
          { value: '헤어컷', text: '헤어컷' },
          { value: '펌', text: '펌' },
          { value: '염색', text: '염색' },
          { value: '네일아트', text: '네일아트' },
          { value: '메이크업', text: '메이크업' },
          { value: '피부관리', text: '피부관리' },
        ],
        secondaryProductOptions: [
          { value: '트리트먼트', text: '트리트먼트' },
          { value: '헤드스파', text: '헤드스파' },
          { value: '네일케어', text: '네일케어' },
          { value: '아이브로우', text: '아이브로우' },
          { value: '마사지', text: '마사지' },
        ],
      };
    },

    computed: {
      // 폼 데이터에 변경사항이 있는지 확인
      hasChanges() {
        if (!this.initialFormData || Object.keys(this.initialFormData).length === 0) {
          return false;
        }

        // 주요 필드들만 체크 (discount는 기본값 10이므로 제외)
        const fieldsToCheck = [
          'name',
          'category',
          'designer',
          'primaryProduct',
          'secondaryProduct',
          'expiryDate',
          'isActive',
        ];

        return fieldsToCheck.some(field => {
          if (field === 'expiryDate') {
            // 날짜는 toString()으로 비교
            const current = this.formData[field] ? this.formData[field].toString() : '';
            const initial = this.initialFormData[field]
              ? this.initialFormData[field].toString()
              : '';
            return current !== initial;
          }
          return this.formData[field] !== this.initialFormData[field];
        });
      },
    },

    watch: {
      couponData: {
        immediate: true,
        handler(newData) {
          if (newData && this.isEditMode) {
            this.formData = {
              id: newData.id,
              name: newData.name || '',
              category: newData.category || '',
              designer: newData.designer || '',
              primaryProduct: newData.product || '',
              secondaryProduct: newData.secondaryProduct || '',
              discount: newData.discount || 10,
              expiryDate: newData.expiryDate || '',
              isActive: newData.isActive || false,
            };
            // 편집 모드에서는 빠른 선택 상태 초기화
            this.selectedPreset = null;
            // 편집 모드에서는 현재 데이터를 초기 상태로 저장
            this.$nextTick(() => {
              this.saveInitialState();
            });
          } else if (!this.isEditMode) {
            this.resetForm();
          }
        },
      },
    },

    mounted() {
      this.saveInitialState();
      // 부모에게 beforeClose 함수 전달
      this.$emit('setBeforeClose', this.checkBeforeClose);
    },

    methods: {
      validateForm() {
        this.errors = {};

        if (!this.formData.name.trim()) {
          this.errors.name = '쿠폰명은 필수입니다.';
        }

        if (!this.formData.category) {
          this.errors.category = '카테고리 선택은 필수입니다.';
        }

        if (!this.formData.primaryProduct) {
          this.errors.primaryProduct = '1차 상품 선택은 필수입니다.';
        }

        if (!this.formData.expiryDate) {
          this.errors.expiryDate = '만료일은 필수입니다.';
        }

        if (this.formData.discount < 0 || this.formData.discount > 100) {
          this.errors.discount = '할인율은 0%에서 100% 사이여야 합니다.';
        }

        // 만료일이 오늘보다 이후인지 확인
        if (this.formData.expiryDate) {
          const today = new Date();
          const expiryDate = new Date(this.formData.expiryDate);
          today.setHours(0, 0, 0, 0);
          expiryDate.setHours(0, 0, 0, 0);

          if (expiryDate <= today) {
            this.errors.expiryDate = '만료일은 오늘 이후여야 합니다.';
          }
        }

        return Object.keys(this.errors).length === 0;
      },

      handleSubmit() {
        if (this.validateForm()) {
          const couponData = this.transformToBackendFormat(this.formData);
          this.$emit('save', couponData);
        }
      },

      // 프론트엔드 폼 데이터를 백엔드 API 형식으로 변환
      transformToBackendFormat(formData) {
        return {
          name: formData.name,
          discount: formData.discount,
          expiryDate: formData.expiryDate,
          isActive: formData.isActive,
          // 백엔드 API 호환을 위한 추가 필드들
          category: formData.category,
          designer: formData.designer,
          primaryProduct: formData.primaryProduct,
          secondaryProduct: formData.secondaryProduct,
          // TODO: 실제 상품 ID와 직원 ID 매핑이 필요
          primaryItemId: this.mapProductToItemId(formData.primaryProduct),
          secondaryItemId: formData.secondaryProduct
            ? this.mapProductToItemId(formData.secondaryProduct)
            : null,
          staffId:
            formData.designer !== '전체' ? this.mapDesignerToStaffId(formData.designer) : null,
          shopId: 1, // TODO: 실제 매장 ID
        };
      },

      // 임시 매핑 함수들 (실제로는 별도 설정에서 가져와야 함)
      mapProductToItemId(product) {
        // 시술 카테고리 (ID 1-50)
        const serviceMapping = {
          헤어컷: 1,
          펌: 2,
          염색: 3,
          네일아트: 4,
          메이크업: 5,
          피부관리: 6,
        };

        // 상품 카테고리 (ID 51-100)
        const productMapping = {
          트리트먼트: 51,
          헤드스파: 52,
          네일케어: 53,
          아이브로우: 54,
          마사지: 55,
        };

        return serviceMapping[product] || productMapping[product] || 1;
      },

      mapDesignerToStaffId(designer) {
        const mapping = {
          김미영: 1,
          박지은: 2,
          이수진: 3,
          최민호: 4,
          정하나: 5,
        };
        return mapping[designer] || null;
      },

      handleCancel() {
        if (this.hasChanges) {
          if (
            confirm(
              '작성하신 내용이 있습니다. 정말로 취소하시겠습니까?\n변경사항이 저장되지 않습니다.'
            )
          ) {
            this.$emit('cancel');
          }
        } else {
          this.$emit('cancel');
        }
      },

      // 창 닫기 전 확인 (BaseWindow의 ESC/백드랍 클릭 처리용)
      checkBeforeClose() {
        if (this.hasChanges) {
          return confirm(
            '작성하신 내용이 있습니다. 정말로 취소하시겠습니까?\n변경사항이 저장되지 않습니다.'
          );
        }
        return true;
      },

      handleFormSubmit(event) {
        event.preventDefault();
        this.handleSubmit();
      },

      // 빠른 날짜 선택 설정
      setDatePreset(months) {
        this.selectedPreset = months;
        const today = new Date();
        const futureDate = new Date(today);
        futureDate.setMonth(futureDate.getMonth() + months);
        futureDate.setHours(0, 0, 0, 0); // 해당 날짜의 시작 시간으로 설정

        this.formData.expiryDate = futureDate;

        // 유효성 검사 에러 초기화
        if (this.errors.expiryDate) {
          delete this.errors.expiryDate;
        }
      },

      // 사용자 정의 날짜 변경 시 빠른 선택 해제
      onCustomDateChange() {
        this.selectedPreset = null;

        // 유효성 검사 에러 초기화
        if (this.errors.expiryDate) {
          delete this.errors.expiryDate;
        }
      },

      resetForm() {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(0, 0, 0, 0);

        this.formData = {
          name: '',
          category: '',
          designer: '전체',
          primaryProduct: '',
          secondaryProduct: '',
          discount: 10,
          expiryDate: null, // 기본값을 null로 설정
          isActive: false,
        };
        this.errors = {};
        this.selectedPreset = null;
        this.saveInitialState();
      },

      // 초기 상태 저장
      saveInitialState() {
        this.initialFormData = { ...this.formData };
      },
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
