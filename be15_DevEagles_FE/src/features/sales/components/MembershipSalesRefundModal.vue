<template>
  <BaseModal
    :model-value="modelValue"
    title="회원권 매출 환불"
    animation-class="back-in-left"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="confirm-message">
      <p class="main">정말 환불하시겠습니까?</p>
      <p class="sub">환불된 데이터는 복구할 수 없습니다.</p>
    </div>

    <template #footer>
      <div class="button-group">
        <BaseButton @click="$emit('update:modelValue', false)">취소</BaseButton>
        <BaseButton class="danger" @click="handleDelete">삭제</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script setup>
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  defineProps({
    modelValue: Boolean,
  });

  const emit = defineEmits(['update:modelValue', 'confirm']);

  const handleDelete = () => {
    emit('confirm');
    emit('update:modelValue', false);
  };
</script>

<style scoped>
  .confirm-message {
    text-align: center;
    margin: 1.5rem 0;
  }
  .confirm-message .main {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 0.5rem;
  }
  .confirm-message .sub {
    font-size: 14px;
    color: #666;
  }
  .button-group {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
  .danger {
    background-color: red;
    color: white;
    border-color: red;
  }
</style>
