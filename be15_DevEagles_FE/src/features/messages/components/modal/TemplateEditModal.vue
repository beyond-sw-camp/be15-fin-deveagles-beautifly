<script setup>
  import { ref, computed, watch } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
    template: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['update:modelValue', 'submit']);

  const visible = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  // 입력값
  const name = ref('');
  const content = ref('');

  // modal 열릴 때 template이 준비돼 있으면 복사 (타이밍 보장용)
  watch(
    () => props.modelValue,
    val => {
      if (val && props.template?.id) {
        name.value = props.template.name ?? '';
        content.value = props.template.content ?? '';
      }
    },
    { immediate: true }
  );

  function close() {
    visible.value = false;
  }

  function submit() {
    if (!name.value.trim() || !content.value.trim()) return;

    emit('submit', {
      id: props.template.id,
      name: props.template.name, // 수정하지 않으니 기존 값
      content: props.template.content, // 수정하지 않으니 기존 값
      createdAt: props.template.createdAt, // ✨ 반드시 포함
    });

    close();
  }
</script>

<template>
  <BaseModal v-model="visible" title="템플릿 수정">
    <div class="space-y-4">
      <!-- 템플릿명 입력 -->
      <BaseForm v-model="name" type="input" label="템플릿명" placeholder="예: 예약 안내" />

      <!-- 내용 입력 -->
      <BaseForm
        v-model="content"
        type="textarea"
        label="내용"
        placeholder="메시지 내용을 입력하세요"
        :rows="10"
      />

      <!-- 버튼 영역 -->
      <div class="action-buttons mt-4 d-flex justify-content-end gap-2">
        <BaseButton variant="subtle" @click="close">취소</BaseButton>
        <BaseButton :disabled="!name.trim() || !content.trim()" @click="submit">수정</BaseButton>
      </div>
    </div>
  </BaseModal>
</template>
