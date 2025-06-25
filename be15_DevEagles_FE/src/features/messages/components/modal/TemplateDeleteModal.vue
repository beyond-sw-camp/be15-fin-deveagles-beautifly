<script setup>
  import { computed } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
  });

  const emit = defineEmits(['update:modelValue', 'confirm']);

  const visible = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  function close() {
    visible.value = false;
  }

  function confirmDelete() {
    emit('confirm'); // 삭제 이벤트 전달 (삭제는 안함)
    close();
  }
</script>

<template>
  <BaseModal v-model="visible" title="삭제 확인">
    <p class="text-center">정말 이 템플릿을 삭제하시겠습니까?</p>
    <div class="action-buttons mt-4 d-flex justify-content-end gap-2">
      <BaseButton type="primary" @click="close">취소</BaseButton>
      <BaseButton type="error" @click="confirmDelete">삭제</BaseButton>
    </div>
  </BaseModal>
</template>
