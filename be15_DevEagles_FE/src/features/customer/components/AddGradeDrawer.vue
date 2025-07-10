<template>
  <BaseDrawer
    v-model="visible"
    title="고객 등급 생성"
    position="right"
    size="md"
    :closable="true"
    :mask-closable="true"
    @after-leave="resetForm"
  >
    <form
      class="customer-create-form"
      autocomplete="off"
      novalidate
      @submit.prevent="validateAndSubmit"
    >
      <div class="form-row">
        <label class="form-label">등급명<span class="required">*</span></label>
        <input
          v-model="form.name"
          type="text"
          class="form-input"
          :class="{ 'input-error': errors.name }"
          placeholder="등급명"
          @blur="validateField('name')"
        />
        <div v-if="errors.name" class="error-message">{{ errors.name }}</div>
      </div>
      <div class="form-row">
        <label class="form-label">할인율<span class="required">*</span></label>
        <div class="input-with-suffix">
          <input
            v-model.number="form.discountRate"
            class="form-input"
            type="number"
            min="0"
            max="100"
            :class="{ 'input-error': errors.discountRate }"
            required
            @input="
              forceInteger('discountRate');
              limitPercent('discountRate');
            "
            @blur="validateField('discountRate')"
          />
          <span class="suffix">%</span>
        </div>
        <div v-if="errors.discountRate" class="error-message">{{ errors.discountRate }}</div>
      </div>
    </form>
    <template #footer>
      <div class="drawer-footer-actions">
        <BaseButton type="secondary" size="sm" outline @click="closeDrawer">취소</BaseButton>
        <BaseButton type="primary" size="sm" native-type="submit" @click="validateAndSubmit"
          >등록</BaseButton
        >
      </div>
    </template>
  </BaseDrawer>
</template>

<script setup>
  import { ref, watch, nextTick } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { useAuthStore } from '@/store/auth.js';

  const props = defineProps({
    modelValue: { type: Boolean, default: false },
  });
  const emit = defineEmits(['update:modelValue', 'create']);

  const authStore = useAuthStore();
  const visible = ref(props.modelValue);
  watch(
    () => props.modelValue,
    v => {
      visible.value = v;
      if (v) nextTick(resetForm);
    }
  );
  watch(visible, v => emit('update:modelValue', v));

  const initialForm = () => ({
    name: '',
    discountRate: 0,
  });
  const form = ref(initialForm());
  const errors = ref({});

  function validateField(field) {
    if (field === 'name')
      errors.value.name = !form.value.name.trim() ? '등급명을 입력해주세요' : '';
    if (field === 'discountRate') {
      const v = form.value.discountRate;
      if (v === '' || v === null || v === undefined || isNaN(v)) {
        errors.value.discountRate = '필수 입력값입니다';
      } else if (v < 0 || v > 100) {
        errors.value.discountRate = '0~100 사이의 정수만 입력';
      } else if (!Number.isInteger(v)) {
        errors.value.discountRate = '정수만 입력';
      } else {
        errors.value.discountRate = '';
      }
    }
  }

  function validateAndSubmit() {
    validateField('name');
    validateField('discountRate');
    if (!errors.value.name && !errors.value.discountRate) {
      emit('create', {
        name: form.value.name,
        discountRate: form.value.discountRate,
        shopId: authStore.shopId,
      });
      visible.value = false;
      resetForm();
    }
  }

  function limitPercent(field) {
    let v = Number(form.value[field]);
    if (isNaN(v)) v = 0;
    if (v < 0) v = 0;
    if (v > 100) v = 100;
    form.value[field] = v;
  }

  function forceInteger(field) {
    if (form.value[field] !== null && form.value[field] !== undefined) {
      form.value[field] = parseInt(form.value[field], 10) || 0;
    }
  }

  function closeDrawer() {
    visible.value = false;
    resetForm();
  }
  function resetForm() {
    form.value = initialForm();
    errors.value = {};
  }
</script>

<style scoped>
  /* paste.txt의 .form-input, .input-error, .error-message, .drawer-footer-actions 등 동일하게 적용 */
  .customer-create-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
    padding: 0;
    min-height: 100%;
    box-sizing: border-box;
    flex: 1 1 auto;
  }
  .form-row {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }
  .form-label {
    font-size: 14px;
    font-weight: 500;
    color: #222;
  }
  .required {
    color: #e53935;
    margin-left: 2px;
    font-size: 15px;
    font-weight: bold;
  }
  .form-input {
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 15px;
    padding: 0 12px;
    background: #fff;
    transition: border 0.2s;
  }
  .form-input:focus {
    outline: none;
    border-color: #364f6b;
    background: #f8fafd;
  }
  .input-error {
    border-color: #e53935 !important;
  }
  .error-message {
    color: #e53935;
    font-size: 12px;
    margin-top: 2px;
  }
  .drawer-footer-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    margin-top: 0;
    margin-bottom: 0;
  }
  .input-with-suffix {
    display: flex;
    align-items: center;
  }
  .suffix {
    margin-left: 8px;
    font-size: 15px;
    color: #888;
    min-width: 20px;
  }
</style>
