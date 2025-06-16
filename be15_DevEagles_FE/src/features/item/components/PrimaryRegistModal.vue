<template>
  <BaseItemModal title="1차 분류 상품 등록" @close="$emit('close')" @submit="submit">
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
          <button class="submit-button" @click="submit">등록</button>
        </div>
      </div>
    </template>
  </BaseItemModal>
</template>

<script setup>
  import { computed } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';

  const props = defineProps({
    modelValue: {
      type: Object,
      required: true,
    },
  });
  const emit = defineEmits(['close', 'submit', 'update:modelValue']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const submit = () => {
    emit('submit', form.value);
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

  .left-group {
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

  input,
  select {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
</style>
