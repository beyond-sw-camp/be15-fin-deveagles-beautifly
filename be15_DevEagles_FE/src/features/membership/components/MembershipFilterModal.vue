<template>
  <BaseModal v-model="internalVisible" title="필터">
    <div class="filter-form">
      <label class="section-title">잔여 선불권</label>
      <div class="range-inputs">
        <BaseForm v-model.number="min" type="number" step="100" placeholder="잔여 선불권" />
        <BaseForm v-model.number="max" type="number" step="100" placeholder="잔여 선불권" />
      </div>
    </div>

    <template #footer>
      <div class="footer-buttons">
        <BaseButton class="primary" outline @click="internalVisible = false">닫기</BaseButton>
        <BaseButton class="primary" @click="applyFilter">저장</BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const emit = defineEmits(['update:modelValue', 'apply']);
  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
  });

  const internalVisible = ref(props.modelValue);
  const min = ref(null);
  const max = ref(null);

  // 외부 modelValue 값 변화 → 내부 반영
  watch(
    () => props.modelValue,
    val => {
      internalVisible.value = val;
    }
  );

  // 내부 값 변화 → 외부로 emit
  watch(internalVisible, val => {
    emit('update:modelValue', val);
  });

  const applyFilter = () => {
    emit('apply', { min: min.value, max: max.value });
    internalVisible.value = false; // 저장 후 닫기
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
</style>
