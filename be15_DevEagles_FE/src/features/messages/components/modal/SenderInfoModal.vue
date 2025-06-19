<script setup>
  import { ref, watch } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
  });

  const emit = defineEmits(['confirm', 'update:modelValue']);

  const number = ref('');
  const errorMessage = ref('');

  const visible = ref(props.modelValue);

  watch(
    () => props.modelValue,
    val => {
      visible.value = val;
    }
  );
  watch(visible, val => {
    emit('update:modelValue', val);
  });

  function validatePhone(phone) {
    return /^\d{11}$/.test(phone);
  }

  function submit() {
    const trimmed = number.value.trim();

    if (!trimmed) {
      errorMessage.value = '발신 번호를 입력해주세요.';
      return;
    }

    if (!validatePhone(trimmed)) {
      errorMessage.value = '올바르지 않은 번호 형식입니다.';
      return;
    }

    errorMessage.value = '';
    emit('confirm', trimmed);
    visible.value = false;
  }
</script>

<template>
  <BaseModal v-model="visible" title="발신 정보 등록">
    <div class="modal-body pt-2 pb-6">
      <div class="form-item">
        <p class="form-description mb-2">문자 서비스를 이용하기 위해서 발신 번호를 입력해주세요.</p>
        <label for="sender-number" class="form-label">발신 번호</label>
        <input
          id="sender-number"
          v-model="number"
          type="text"
          class="modern-input mt-2"
          placeholder="예: 01012345678"
        />
        <p v-if="errorMessage" class="text-error text-sm mt-1">{{ errorMessage }}</p>
      </div>
    </div>

    <div class="modal-footer mt-6 flex justify-end gap-2">
      <BaseButton type="secondary" @click="visible = false">취소</BaseButton>
      <BaseButton type="primary" @click="submit">등록</BaseButton>
    </div>
  </BaseModal>
</template>

<style scoped>
  .modern-input {
    width: 100%;
    padding: 10px 14px;
    border: 1px solid var(--color-gray-300);
    border-radius: 8px;
    background-color: #fff;
    font-size: 14px;
    color: var(--color-gray-900);
    transition:
      border-color 0.2s,
      box-shadow 0.2s;
  }

  .modern-input::placeholder {
    color: var(--color-gray-400);
  }

  .modern-input:focus {
    outline: none;
    border-color: var(--color-primary-main);
    box-shadow: 0 0 0 3px rgba(78, 117, 255, 0.1);
  }

  .form-description {
    font-size: 13px;
    color: var(--color-gray-500);
    line-height: 1.5;
    margin-bottom: 0.5rem;
  }
</style>
