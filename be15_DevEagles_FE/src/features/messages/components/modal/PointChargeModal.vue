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

  const visible = ref(props.modelValue);
  const amount = ref('');

  watch(
    () => props.modelValue,
    val => {
      visible.value = val;
    }
  );
  watch(visible, val => {
    emit('update:modelValue', val);
  });

  function confirmCharge() {
    const parsed = parseInt(amount.value, 10);
    if (isNaN(parsed) || parsed <= 0) {
      alert('충전할 포인트를 올바르게 입력해 주세요.');
      return;
    }
    emit('confirm', parsed);
    visible.value = false;
  }
</script>

<template>
  <BaseModal v-model="visible" title="포인트 충전">
    <div class="modal-body pt-2 pb-6">
      <label for="charge-input" class="form-label">충전할 포인트</label>
      <input
        id="charge-input"
        v-model="amount"
        type="number"
        class="modern-input mt-2"
        placeholder="숫자를 입력하세요"
      />
    </div>

    <div class="modal-footer mt-6 flex justify-end gap-2">
      <BaseButton type="gray" @click="visible = false">취소</BaseButton>
      <BaseButton type="primary" @click="confirmCharge">충전</BaseButton>
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
</style>
