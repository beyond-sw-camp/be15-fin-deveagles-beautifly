<template>
  <form class="coupon-form" @submit.prevent="handleSubmit">
    <!-- 쿠폰명 -->
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

    <!-- 카테고리 -->
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

    <!-- 디자이너 -->
    <BaseForm
      id="designer"
      v-model="formData.designer"
      label="디자이너*"
      type="select"
      placeholder="디자이너를 선택하세요"
      :options="designerOptions"
      :error="errors.designer"
      required
    />

    <!-- 1차 상품 -->
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

    <!-- 2차 상품 -->
    <BaseForm
      id="secondaryProduct"
      v-model="formData.secondaryProduct"
      label="2차 상품"
      type="select"
      placeholder="2차 상품을 선택하세요 (선택사항)"
      :options="secondaryProductOptions"
      :error="errors.secondaryProduct"
    />

    <!-- 할인율 슬라이더 -->
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

    <!-- 만료일 -->
    <PrimeDatePicker
      v-model="formData.expiryDate"
      label="만료일*"
      placeholder="날짜와 시간을 선택하세요"
      show-time
      :show-button-bar="true"
      :min-date="new Date()"
      :error="errors.expiryDate"
      clearable
    />

    <!-- 활성화 여부 -->
    <div class="form-group">
      <label class="form-label">활성화 설정</label>
      <div class="checkbox-wrapper">
        <div class="checkbox">
          <input id="isActive" v-model="formData.isActive" type="checkbox" />
          <label for="isActive">쿠폰을 즉시 활성화</label>
        </div>
      </div>
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
    emits: ['save', 'cancel'],
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

        // 옵션 데이터
        categoryOptions: [
          { value: 'hair', text: '헤어' },
          { value: 'nail', text: '네일' },
          { value: 'makeup', text: '메이크업' },
          { value: 'skincare', text: '피부관리' },
          { value: 'massage', text: '마사지' },
        ],
        designerOptions: [
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
          } else if (!this.isEditMode) {
            this.resetForm();
          }
        },
      },
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

        if (!this.formData.designer) {
          this.errors.designer = '디자이너 선택은 필수입니다.';
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
          const couponData = {
            ...this.formData,
            product: this.formData.primaryProduct, // API 호환성을 위해
          };

          this.$emit('save', couponData);
        }
      },

      handleCancel() {
        this.$emit('cancel');
      },

      handleFormSubmit(event) {
        event.preventDefault();
        this.handleSubmit();
      },

      resetForm() {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(0, 0, 0, 0);

        this.formData = {
          name: '',
          category: '',
          designer: '',
          primaryProduct: '',
          secondaryProduct: '',
          discount: 10,
          expiryDate: tomorrow,
          isActive: false,
        };
        this.errors = {};
      },
    },
  };
</script>

<style scoped>
  .coupon-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
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

  @media (max-width: 768px) {
    .form-actions {
      flex-direction: column;
    }
  }
</style>
