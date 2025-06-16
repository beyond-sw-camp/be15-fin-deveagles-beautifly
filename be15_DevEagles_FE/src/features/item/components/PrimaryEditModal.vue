<template>
  <BaseItemModal title="1차 분류 상품 수정" @close="$emit('close')" @submit="submit">
    <div class="form-group">
      <label>카테고리</label>
      <select v-model="form.category">
        <option value="SERVICE">시술</option>
        <option value="PRODUCT">상품</option>
      </select>
    </div>
    <div class="form-group">
      <label>1차 분류명</label>
      <input v-model="form.primaryName" type="text" placeholder="1차 분류명" />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <div class="left-group">
          <button class="cancel-button" @click="$emit('close')">취소</button>
          <button class="submit-button" @click="submit">수정</button>
        </div>
        <div class="right-group">
          <button class="delete-button" @click="showDeleteModal = true">삭제</button>
        </div>
      </div>
    </template>

    <PrimaryDeleteModal v-if="showDeleteModal" v-model="showDeleteModal" @confirm="handleDelete" />
  </BaseItemModal>
</template>

<script setup>
  import { computed, ref } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';
  import PrimaryDeleteModal from './PrimaryDeleteModal.vue';

  const props = defineProps({
    modelValue: {
      type: Object,
      required: true,
    },
  });
  const emit = defineEmits(['close', 'submit', 'update:modelValue', 'delete']);

  const showDeleteModal = ref(false);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const submit = () => {
    emit('submit', form.value);
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
  input,
  select {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }

  /* 하단 버튼 */
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

  .cancel-button,
  .submit-button {
    padding: 8px 12px;
    border-radius: 4px;
    border: 1px solid #ccc;
    background: white;
    cursor: pointer;
  }

  .delete-button {
    background-color: red;
    color: white;
    border: 1px solid red;
    padding: 8px 12px;
    border-radius: 4px;
    cursor: pointer;
  }
</style>
