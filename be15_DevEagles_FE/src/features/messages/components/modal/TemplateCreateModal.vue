<script setup>
  import { ref, computed } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
  });
  const emit = defineEmits(['update:modelValue', 'submit']);

  const visible = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const name = ref('');
  const content = ref('');

  function close() {
    visible.value = false;
  }

  function submit() {
    if (!name.value || !content.value) return;
    emit('submit', {
      name: name.value,
      content: content.value,
    });
    close();
  }
</script>

<template>
  <BaseModal v-model="visible" title="템플릿 등록">
    <div class="space-y-4">
      <BaseForm v-model="name" label="템플릿명">
        <input class="input input--md" placeholder="예: 예약 안내" />
      </BaseForm>

      <BaseForm
        v-model="content"
        type="textarea"
        label="내용"
        placeholder="메시지 내용을 입력하세요"
        :rows="10"
      />

      <div class="action-buttons mt-4 d-flex justify-content-end gap-2">
        <BaseButton variant="subtle" @click="close">취소</BaseButton>
        <BaseButton :disabled="!name || !content" @click="submit">등록</BaseButton>
      </div>
    </div>
  </BaseModal>
</template>
