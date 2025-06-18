<template>
  <BaseModal title="필터" @close="$emit('close')">
    <div class="filter-form">
      <label class="section-title">잔여 선불권</label>
      <div class="range-inputs">
        <input v-model.number="min" type="number" step="100" placeholder="잔여 선불권" />
        <input v-model.number="max" type="number" step="100" placeholder="잔여 선불권" />
      </div>
    </div>

    <template #footer>
      <div class="footer-buttons">
        <button class="cancel-button" @click="$emit('close')">닫기</button>
        <button class="apply-button" @click="applyFilter">저장</button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';

  const emit = defineEmits(['close', 'apply']);

  const min = ref(null);
  const max = ref(null);

  const applyFilter = () => {
    emit('apply', { min: min.value, max: max.value });
  };
</script>

<style scoped>
  .filter-form {
    padding: 1rem 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .section-title {
    font-weight: bold;
    font-size: 16px;
  }

  .range-inputs {
    display: flex;
    gap: 1rem;
  }

  input {
    flex: 1;
    padding: 8px;
    font-size: 14px;
    border: 1px solid #aaa;
    border-radius: 4px;
  }

  /* Footer Buttons */
  .footer-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding: 0 1.5rem 1rem;
  }

  .cancel-button {
    padding: 6px 14px;
    background-color: white;
    border: 1px solid black;
    border-radius: 4px;
    cursor: pointer;
  }

  .apply-button {
    padding: 6px 14px;
    background-color: black;
    color: white;
    border-radius: 4px;
    cursor: pointer;
  }
</style>
