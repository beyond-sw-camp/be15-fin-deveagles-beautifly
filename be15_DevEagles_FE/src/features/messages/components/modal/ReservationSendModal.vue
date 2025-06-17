<script setup>
  import { ref } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
    messageContent: {
      type: String,
      required: true,
    },
  });
  const emit = defineEmits(['update:modelValue', 'confirm']);

  const selectedDate = ref(null);

  function close() {
    emit('update:modelValue', false);
  }

  function submit() {
    if (!selectedDate.value) {
      alert('예약 날짜를 선택해주세요.');
      return;
    }

    emit('confirm', {
      content: props.messageContent,
      date: selectedDate.value,
    });
    close();
  }
</script>

<template>
  <BaseModal
    :model-value="modelValue"
    title="예약 메시지 등록"
    @update:model-value="val => emit('update:modelValue', val)"
  >
    <div class="modal-body">
      <label class="form-label">메시지 내용</label>
      <div class="message-preview">
        {{ messageContent }}
      </div>

      <PrimeDatePicker
        v-model="selectedDate"
        label="발송 날짜"
        :min-date="new Date()"
        placeholder="발송 날짜를 선택하세요"
        :show-button-bar="true"
        :show-time="false"
        :selection-mode="'single'"
      />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <BaseButton type="primary" @click="submit">예약 확정</BaseButton>
        <BaseButton type="ghost" @click="close">취소</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<style scoped>
  .modal-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .message-preview {
    padding: 12px;
    border-radius: 6px;
    background-color: var(--color-gray-50);
    border: 1px solid var(--color-gray-200);
    font-size: 14px;
    color: var(--color-gray-800);
    white-space: pre-wrap;
    min-height: 80px;
  }

  .footer-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
</style>
