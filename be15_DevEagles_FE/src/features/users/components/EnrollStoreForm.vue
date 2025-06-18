<template>
  <h2 class="font-section-title signup-title">매장 정보 등록</h2>

  <div class="form-fields">
    <div class="label-row">
      <label for="storeName">
        상점명
        <span v-if="isRequired('storeName')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      id="storeName"
      v-model="form.storeName"
      placeholder="상점명을 입력해주세요."
      :error="errors.storeName"
      @focus="clearError('storeName')"
    />

    <AddressSearch v-model="form.address" />

    <div class="label-row">
      <label for="category">
        업종
        <span v-if="isRequired('category')" class="required">*</span>
      </label>
    </div>
    <BaseForm
      v-model="form.category"
      type="select"
      :options="[
        { value: 'hair', text: '헤어' },
        { value: 'nail', text: '네일' },
        { value: 'lash', text: '속눈썹' },
      ]"
      placeholder="카테고리를 선택해주세요."
      :error="errors.category"
      @focus="clearError('category')"
    />

    <div class="label-row">
      <label for="storePhone"> 매장 전화번호 </label>
    </div>
    <BaseForm
      v-model="form.storePhone"
      placeholder="매장 전화번호를 입력해주세요."
      :error="errors.storePhone"
      @focus="clearError('storePhone')"
    />

    <div class="label-row">
      <label for="bizNumber"> 사업자 등록번호 </label>
    </div>
    <BaseForm
      v-model="form.bizNumber"
      placeholder="사업자 등록번호를 입력해주세요."
      :error="errors.bizNumber"
      @focus="clearError('bizNumber')"
    />
  </div>
</template>

<script setup>
  import BaseForm from '@/components/common/BaseForm.vue';
  import { computed, ref } from 'vue';
  import AddressSearch from '@/features/users/components/AddressSearch.vue';

  const props = defineProps({
    modelValue: Object,
    isRequired: Function,
  });
  const emit = defineEmits(['update:modelValue']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const errors = ref({
    storeName: '',
    category: '',
    storePhone: '',
    bizNumber: '',
  });

  const clearError = field => {
    errors.value[field] = '';
  };

  const validate = () => {
    let valid = true;

    if (!form.value.storeName) {
      errors.value.storeName = '상점명을 입력해주세요.';
      valid = false;
    }

    if (!form.value.category) {
      errors.value.category = '업종을 선택해주세요.';
      valid = false;
    }

    if (form.value.storePhone) {
      const onlyNumberPattern = /^[0-9]+$/;
      if (!onlyNumberPattern.test(form.value.storePhone)) {
        errors.value.storePhone = '숫자만 입력해주세요.';
        valid = false;
      }
    }

    if (form.value.bizNumber && !/^[0-9]{10}$/.test(form.value.bizNumber)) {
      errors.value.bizNumber = '숫자 10자리로 입력해주세요.';
      valid = false;
    }

    return valid;
  };

  defineExpose({ validate });
</script>
<style scoped>
  .signup-title {
    text-align: center;
    margin-bottom: 24px;
  }
  .label-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4px;
    font-weight: 600;
    font-size: 14px;
    color: var(--color-gray-900);
  }
  .form-fields {
    width: 360px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .required {
    color: red;
    margin-left: 4px;
  }
  .validation-msg {
    font-size: 12px;
    color: var(--color-error, red);
    margin-top: -8px;
    margin-bottom: 4px;
  }
</style>
