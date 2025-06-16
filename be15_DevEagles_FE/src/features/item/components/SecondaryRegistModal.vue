<template>
  <BaseItemModal title="2차 상품 등록" @close="$emit('close')" @submit="handleSubmit">
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="primary">1차 분류명</label>
        <select id="primary" v-model="form.primaryItemId">
          <option disabled value="">1차 분류명</option>
          <option v-for="item in primaryOptions" :key="item.id" :value="item.id">
            {{ item.name }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label for="secondaryName">2차 분류명</label>
        <input
          id="secondaryName"
          v-model="form.secondaryName"
          placeholder="2차 분류명"
          type="text"
        />
      </div>

      <div class="form-group">
        <label for="price">상품 금액</label>
        <input
          id="price"
          v-model.number="form.price"
          placeholder="상품 금액"
          type="number"
          step="100"
        />
      </div>
    </form>

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
  import { ref } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';

  const emit = defineEmits(['close', 'submit']);

  const primaryOptions = [
    { id: 1, name: '펌(남성)' },
    { id: 2, name: '헤어' },
  ]; // 추후 props로 받을 수도 있음

  const form = ref({
    primaryItemId: '',
    secondaryName: '',
    price: '',
  });

  const handleSubmit = () => {
    if (!form.value.primaryItemId || !form.value.secondaryName || !form.value.price) return;
    emit('submit', form.value);
    emit('close');
  };
</script>

<style scoped>
  .form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 16px;
  }
  label {
    font-size: 14px;
    margin-bottom: 4px;
  }
  input,
  select {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
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
</style>
