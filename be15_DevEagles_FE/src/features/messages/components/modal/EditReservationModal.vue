<script setup>
  import { ref, watch, nextTick } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import BaseForm from '@/components/common/BaseForm.vue';

  const props = defineProps({
    modelValue: Boolean,
    message: Object,
  });

  const emit = defineEmits(['update:modelValue', 'confirm']);

  const form = ref({
    title: '',
    content: '',
    receiver: '',
    status: 'sent',
    date: null,
  });

  function close() {
    emit('update:modelValue', false);
  }

  function handleConfirm() {
    emit('confirm', {
      id: props.message?.id,
      title: form.value.title,
      content: form.value.content,
      receiver: form.value.receiver,
      status: form.value.status,
      date: form.value.date ? formatDate(form.value.date) : '',
    });
    close();
  }

  function formatDate(timeOnly) {
    const d = props.message?.date ? new Date(props.message.date) : new Date();

    d.setHours(timeOnly.getHours());
    d.setMinutes(timeOnly.getMinutes());
    d.setSeconds(0);
    d.setMilliseconds(0);

    return (
      d.getFullYear() +
      '-' +
      (d.getMonth() + 1).toString().padStart(2, '0') +
      '-' +
      d.getDate().toString().padStart(2, '0') +
      ' ' +
      d.getHours().toString().padStart(2, '0') +
      ':' +
      d.getMinutes().toString().padStart(2, '0')
    );
  }

  function setFormValues(msg) {
    if (!msg) return;
    form.value.title = msg.title || '';
    form.value.content = msg.content || '';
    form.value.receiver = msg.receiver || '';
    form.value.status = msg.status || 'sent';
    form.value.date = msg.date ? new Date(msg.date) : null;
  }

  watch(
    () => props.modelValue,
    async visible => {
      if (visible && props.message) {
        await nextTick();
        setFormValues(props.message);
      }
    },
    { immediate: true }
  );
</script>

<template>
  <BaseModal :model-value="modelValue" title="메시지 정보 수정" @update:model-value="close">
    <div class="edit-message-form">
      <BaseForm v-model="form.title" label="제목" />
      <BaseForm v-model="form.content" label="내용" type="textarea" />
      <BaseForm v-model="form.receiver" label="수신자" />
      <BaseForm
        v-model="form.status"
        label="상태"
        type="select"
        :options="[
          { value: 'sent', text: '발송 완료' },
          { value: 'reserved', text: '예약 문자' },
        ]"
      />
      <label class="form-label">날짜</label>
      <PrimeDatePicker
        v-model="form.date"
        :show-time="true"
        :hour-format="'24'"
        :show-button-bar="true"
        :placeholder="'날짜와 시간을 선택하세요'"
        :min-date="new Date()"
      />
    </div>

    <template #footer>
      <BaseButton type="secondary" @click="close">취소</BaseButton>
      <BaseButton type="primary" @click="handleConfirm">수정 완료</BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
  .edit-message-form {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .form-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-gray-700);
  }
</style>
