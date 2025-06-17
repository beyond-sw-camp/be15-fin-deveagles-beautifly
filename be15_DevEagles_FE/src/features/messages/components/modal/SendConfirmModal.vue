<script setup>
  import BaseConfirm from '@/components/common/BaseConfirm.vue';
  import { defineProps, defineEmits } from 'vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
  });
  const emit = defineEmits(['update:modelValue', 'confirm', 'cancel']);

  function onConfirm() {
    emit('confirm');
    emit('update:modelValue', false);
  }
  function onCancel() {
    emit('cancel');
    emit('update:modelValue', false);
  }
</script>

<template>
  <BaseConfirm
    :model-value="props.modelValue"
    title="메시지 전송"
    message="메시지를 전송하시겠습니까?"
    confirm-text="보내기"
    cancel-text="취소"
    confirm-type="primary"
    @update:model-value="val => emit('update:modelValue', val)"
    @confirm="onConfirm"
    @cancel="onCancel"
  />
</template>
