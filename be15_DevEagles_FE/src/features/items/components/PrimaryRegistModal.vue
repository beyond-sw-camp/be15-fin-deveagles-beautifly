<template>
  <BaseItemModal title="1차 분류 상품 등록" @close="$emit('close')" @submit="submit">
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

    <div class="form-group">
      <label>1차 분류명</label>
      <BaseForm v-model="form.primaryName" type="text" placeholder="1차 분류명" />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <div class="left-group">
          <BaseButton type="primary" outline @click="$emit('close')">취소</BaseButton>
          <BaseButton type="primary" @click="submit">등록</BaseButton>
        </div>
      </div>
    </template>
  </BaseItemModal>
</template>

<script setup>
  import { computed } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['close', 'submit', 'update:modelValue', 'toast']); // ✅ toast 이벤트 추가

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const submit = () => {
    emit('submit', form.value);
    emit('toast', '1차 상품이 등록되었습니다.'); // ✅ 토스트 메시지 emit
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
</style>
