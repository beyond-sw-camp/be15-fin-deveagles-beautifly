<template>
  <BaseModal
    :model-value="modelValue"
    title="1차 상품 삭제"
    animation-class="back-in-left"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div class="confirm-message">
      <p class="main">1차 상품을 삭제하시겠습니까?</p>
      <p class="sub">삭제된 데이터는 복구할 수 없습니다.</p>
    </div>

    <template #footer>
      <div class="button-group">
        <BaseButton @click="$emit('update:modelValue', false)">취소</BaseButton>
        <BaseButton type="error" @click="handleDelete">삭제</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script setup>
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { deletePrimaryItem } from '@/features/items/api/items';

  const props = defineProps({
    modelValue: Boolean,
    primaryItemId: {
      type: Number,
      required: true,
    },
  });

  const emit = defineEmits(['update:modelValue', 'confirm', 'error']);

  const handleDelete = async () => {
    try {
      await deletePrimaryItem(props.primaryItemId);
      emit('confirm'); // 부모가 성공 메시지 처리
    } catch (error) {
      emit('error', error.response?.data?.message || '삭제에 실패했습니다.');
    } finally {
      emit('update:modelValue', false);
    }
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
</style>
