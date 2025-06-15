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
    />

    <!-- 카테고리 선택 -->
    <BaseForm
      id="category"
      v-model="formData.category"
      label="카테고리 선택*"
      type="select"
      placeholder="Select"
      :options="categoryOptions"
      :error="errors.category"
      required
    />

    <!-- 디자이너 선택 -->
    <BaseForm
      id="designer"
      v-model="formData.designer"
      label="디자이너 선택*"
      type="select"
      placeholder="Select"
      :options="designerOptions"
      :error="errors.designer"
      required
    />

    <!-- 1차 상품 선택 -->
    <BaseForm
      id="primaryProduct"
      v-model="formData.primaryProduct"
      label="1차 상품 선택*"
      type="select"
      placeholder="Select"
      :options="primaryProductOptions"
      :error="errors.primaryProduct"
      required
    />

    <!-- 2차 상품 선택 -->
    <BaseForm
      id="secondaryProduct"
      v-model="formData.secondaryProduct"
      label="2차 상품 선택*"
      type="select"
      placeholder="Select"
      :options="secondaryProductOptions"
      :error="errors.secondaryProduct"
    />

    <!-- 할인율 -->
    <div class="form-group">
      <label class="form-label">할인율(%)</label>
      <div class="discount-slider">
        <input
          v-model="formData.discountRate"
          type="range"
          min="0"
          max="100"
          step="1"
          class="slider-input"
        />
        <div class="slider-value">{{ formData.discountRate }}%</div>
      </div>
    </div>

    <!-- 쿠폰 만료일 -->
    <BaseForm
      id="expiryDate"
      v-model="formData.expiryDate"
      label="쿠폰 만료일"
      type="date"
      placeholder="Select date"
      :error="errors.expiryDate"
    />

    <!-- 버튼들 -->
    <div class="form-actions">
      <BaseButton type="secondary" outline style="flex: 1" @click="handleCancel"> 취소 </BaseButton>
      <BaseButton type="primary" style="flex: 1" @click="handleSubmit"> 저장 </BaseButton>
    </div>
  </form>
</template>

<script>
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  export default {
    name: 'CouponForm',
    components: {
      BaseForm,
      BaseButton,
    },
    props: {},
    emits: ['save', 'cancel'],
    data() {
      return {
        formData: {
          name: '',
          category: '',
          designer: '',
          primaryProduct: '',
          secondaryProduct: '',
          discountRate: 10,
          expiryDate: '',
          isActive: true,
        },
        errors: {},

        // 옵션 데이터 (실제로는 API에서 받아올 데이터)
        categoryOptions: [
          { value: '1차', text: '1차 카테고리' },
          { value: '2차', text: '2차 카테고리' },
          { value: '3차', text: '3차 카테고리' },
        ],
        designerOptions: [
          { value: '이순신', text: '이순신' },
          { value: '김철수', text: '김철수' },
          { value: '박영희', text: '박영희' },
          { value: '최민수', text: '최민수' },
        ],
        primaryProductOptions: [
          { value: '열린', text: '열린' },
          { value: '닫힌', text: '닫힌' },
          { value: '반열린', text: '반열린' },
        ],
        secondaryProductOptions: [
          { value: '옵션1', text: '옵션1' },
          { value: '옵션2', text: '옵션2' },
          { value: '옵션3', text: '옵션3' },
        ],
      };
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

        if (this.formData.expiryDate) {
          const today = new Date();
          const expiryDate = new Date(this.formData.expiryDate);
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

      resetForm() {
        this.formData = {
          name: '',
          category: '',
          designer: '',
          primaryProduct: '',
          secondaryProduct: '',
          discountRate: 10,
          expiryDate: '',
          isActive: true,
        };
        this.errors = {};
      },
    },
  };
</script>

<style scoped>
  .coupon-form {
    max-width: 500px;
    margin: 0 auto;
  }

  .discount-slider {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-top: 0.5rem;
  }

  .slider-input {
    flex: 1;
    height: 6px;
    border-radius: 3px;
    background: var(--color-gray-200);
    outline: none;
    -webkit-appearance: none;
  }

  .slider-input::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: var(--color-primary-main);
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .slider-input::-moz-range-thumb {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: var(--color-primary-main);
    cursor: pointer;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .slider-value {
    min-width: 60px;
    text-align: center;
    font-weight: 600;
    color: var(--color-primary-main);
    font-size: 16px;
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    margin-top: 2rem;
    padding-top: 1rem;
    border-top: 1px solid var(--color-gray-200);
  }

  @media (max-width: 768px) {
    .coupon-form {
      max-width: none;
    }

    .form-actions {
      flex-direction: column;
    }

    .discount-slider {
      flex-direction: column;
      align-items: stretch;
      text-align: center;
    }
  }
</style>
