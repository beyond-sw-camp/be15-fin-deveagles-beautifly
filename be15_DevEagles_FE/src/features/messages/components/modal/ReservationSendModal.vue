<script setup>
  import { ref, computed } from 'vue';
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

  const parsed = computed(() => {
    try {
      return JSON.parse(props.messageContent);
    } catch {
      return {};
    }
  });

  function close() {
    emit('update:modelValue', false);
  }

  function submit() {
    if (!selectedDate.value) {
      alert('예약 날짜와 시간을 선택해주세요.');
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
        <p><strong>내용:</strong> {{ parsed?.content || '없음' }}</p>
        <p v-if="parsed?.link"><strong>링크:</strong> {{ parsed.link }}</p>
        <p v-if="parsed?.coupon"><strong>쿠폰:</strong> {{ parsed.coupon.name }}</p>
        <p v-if="parsed?.grades?.length"><strong>등급:</strong> {{ parsed.grades.join(', ') }}</p>
        <p v-if="parsed?.tags?.length"><strong>태그:</strong> {{ parsed.tags.join(', ') }}</p>
      </div>

      <PrimeDatePicker
        v-model="selectedDate"
        label="발송 날짜 및 시간"
        :min-date="new Date()"
        placeholder="날짜 및 시간을 선택하세요"
        :show-button-bar="true"
        :show-time="true"
        :hour-format="'24'"
        :selection-mode="'single'"
      />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <BaseButton type="primary" @click="submit">예약 발송</BaseButton>
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

  .message-preview p {
    margin: 4px 0;
    font-size: 14px;
  }
</style>
