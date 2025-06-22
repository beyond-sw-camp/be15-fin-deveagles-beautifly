<template>
  <BaseItemModal title="1차 분류 상품 수정" @close="$emit('close')" @submit="submit">
    <!-- 카테고리 -->
    <div class="form-group">
      <label>카테고리</label>
      <BaseForm
        v-model="form.category"
        type="select"
        :options="[
          { value: 'SERVICE', text: '시술' },
          { value: 'PRODUCT', text: '상품' },
        ]"
      />
    </div>

    <!-- 1차 분류명 -->
    <div class="form-group">
      <label>1차 분류명</label>
      <BaseForm v-model="form.primaryName" type="text" placeholder="1차 분류명" />
    </div>

    <!-- 하단 버튼 -->
    <template #footer>
      <div class="footer-buttons">
        <div class="left-group">
          <BaseButton type="primary" outline @click="$emit('close')">취소</BaseButton>
          <BaseButton type="primary" @click="submit">수정</BaseButton>
        </div>
        <div class="right-group">
          <BaseButton type="error" @click="showDeleteModal = true">삭제</BaseButton>
        </div>
      </div>
    </template>

    <!-- 삭제 확인 모달 -->
    <PrimaryDeleteModal v-if="showDeleteModal" v-model="showDeleteModal" @confirm="handleDelete" />
  </BaseItemModal>
</template>

<script setup>
  import { computed, ref } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimaryDeleteModal from './PrimaryDeleteModal.vue';

  const props = defineProps({
    modelValue: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['close', 'submit', 'update:modelValue', 'delete', 'toast']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const showDeleteModal = ref(false);

  const submit = () => {
    emit('submit', form.value);
    emit('toast', '1차 상품이 수정되었습니다.');
  };

  const handleDelete = () => {
    emit('delete', form.value);
    showDeleteModal.value = false;
    emit('close');
  };
</script>

<style scoped>
  .form-group {
    margin-bottom: 16px;
    display: flex;
    flex-direction: column;
  }
  label {
    margin-bottom: 4px;
  }

  /* 버튼 정렬 */
  .footer-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .left-group,
  .right-group {
    display: flex;
    gap: 8px;
  }
</style>
