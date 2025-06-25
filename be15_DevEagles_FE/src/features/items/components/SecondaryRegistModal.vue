<template>
  <BaseItemModal title="2차 상품 등록" @close="$emit('close')" @submit="submit">
    <div class="form-group">
      <label for="primary">1차 분류명</label>
      <BaseForm
        id="primary"
        v-model.number="form.primaryItemId"
        type="select"
        :options="primaryOptions.map(item => ({ value: item.id, text: item.name }))"
        placeholder="1차 분류명"
      />
    </div>

    <div class="form-group">
      <label for="secondaryName">2차 분류명</label>
      <BaseForm
        id="secondaryName"
        v-model="form.secondaryName"
        type="text"
        placeholder="2차 분류명"
      />
    </div>

    <div class="form-group">
      <label for="price">상품 금액</label>
      <BaseForm
        id="price"
        v-model.number="form.price"
        type="number"
        step="100"
        placeholder="상품 금액"
      />
    </div>

    <!-- 시술일 경우만 시술 시간 입력 -->
    <div v-if="selectedPrimary?.category === 'SERVICE'" class="form-group">
      <label for="duration">시술 시간 (분)</label>
      <BaseForm
        id="duration"
        v-model.number="form.duration"
        type="number"
        step="5"
        placeholder="예: 60"
      />
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

  const emit = defineEmits(['close', 'submit', 'update:modelValue', 'toast']);

  const primaryOptions = [
    { id: 1, name: '펌(남성)', category: 'SERVICE' },
    { id: 2, name: '헤어', category: 'PRODUCT' },
  ];

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const selectedPrimary = computed(() =>
    primaryOptions.find(opt => opt.id === form.value.primaryItemId)
  );

  const submit = () => {
    emit('submit', form.value);
    emit('toast', '2차 상품이 등록되었습니다');
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
  .left-group {
    display: flex;
    gap: 8px;
  }
</style>
