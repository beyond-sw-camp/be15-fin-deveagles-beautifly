<script setup>
  import { ref } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
  });
  const emit = defineEmits(['update:modelValue', 'request-send']);

  const messageContent = ref('');

  function close() {
    emit('update:modelValue', false);
  }

  function confirmSend() {
    if (!messageContent.value.trim()) return;
    emit('request-send', messageContent.value);
    close();
    messageContent.value = '';
  }
</script>

<template>
  <BaseModal
    :model-value="props.modelValue"
    title="새 메시지"
    @update:model-value="val => emit('update:modelValue', val)"
  >
    <div class="form-group">
      <div class="template-row">
        <BaseButton type="ghost" size="sm">템플릿 가져오기</BaseButton>
      </div>

      <label class="form-label mt-4">메시지 내용</label>
      <textarea
        v-model="messageContent"
        class="input input--md"
        rows="5"
        placeholder="메시지 내용을 입력하세요"
      />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <div class="left-buttons">
          <BaseButton type="primary" @click="confirmSend">보내기</BaseButton>
          <BaseButton type="secondary">예약 보내기</BaseButton>
        </div>
        <BaseButton type="ghost" @click="close">취소</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<style scoped>
  .footer-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  .left-buttons {
    display: flex;
    gap: 8px;
  }
  .template-row {
    display: flex;
    justify-content: flex-start;
  }
</style>
