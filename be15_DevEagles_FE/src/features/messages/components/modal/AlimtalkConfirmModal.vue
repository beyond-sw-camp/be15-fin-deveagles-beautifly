<script setup>
  import { computed } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  defineOptions({
    inheritAttrs: false,
  });

  const props = defineProps({
    modelValue: Boolean,
  });
  const emit = defineEmits(['confirm', 'update:modelValue']);

  const visible = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });
</script>

<template>
  <BaseModal v-model="visible" title="알림톡 신청">
    <div class="modal-body pt-2 pb-6 text-center">
      <p class="form-description">
        카카오 연동 없이 신청 여부만 저장됩니다.<br />
        신청하시겠습니까?
      </p>
    </div>

    <div class="modal-footer mt-6 flex justify-end gap-2">
      <BaseButton type="secondary" @click="visible = false">취소</BaseButton>
      <BaseButton type="primary" @click="$emit('confirm')">신청</BaseButton>
    </div>
  </BaseModal>
</template>

<style scoped>
  .form-description {
    font-size: 13px;
    color: var(--color-gray-500);
    line-height: 1.5;
  }
</style>
