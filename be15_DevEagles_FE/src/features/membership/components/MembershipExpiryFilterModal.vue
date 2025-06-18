<template>
  <BaseModal v-model="modalVisible" title="필터">
    <div class="filter-form">
      <!-- 잔여 필드 -->
      <label class="section-title">잔여 {{ type === '선불' ? '선불권' : '횟수권' }}</label>
      <div class="range-inputs">
        <input
          v-model.number="min"
          type="number"
          step="100"
          :placeholder="`잔여 ${type === '선불' ? '선불권' : '횟수권'}`"
        />
        <input
          v-model.number="max"
          type="number"
          step="100"
          :placeholder="`잔여 ${type === '선불' ? '선불권' : '횟수권'}`"
        />
      </div>

      <!-- 만료 예정일 필드 -->
      <label class="section-title">만료(예정)일</label>
      <div class="range-inputs">
        <input v-model="startDate" type="date" />
        <input v-model="endDate" type="date" />
      </div>
    </div>

    <template #footer>
      <div class="footer-buttons">
        <button class="cancel-button" @click="closeModal">닫기</button>
        <button class="apply-button" @click="applyFilter">저장</button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
    type: {
      type: String,
      default: '선불',
    },
  });

  const emit = defineEmits(['update:modelValue', 'apply']);

  const modalVisible = ref(props.modelValue);
  watch(
    () => props.modelValue,
    val => (modalVisible.value = val)
  );
  watch(modalVisible, val => emit('update:modelValue', val));

  const min = ref(null);
  const max = ref(null);
  const startDate = ref('');
  const endDate = ref('');

  const closeModal = () => {
    modalVisible.value = false;
  };

  const applyFilter = () => {
    emit('apply', {
      min: min.value,
      max: max.value,
      startDate: startDate.value,
      endDate: endDate.value,
    });
    closeModal();
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
