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
      <BaseForm
        v-model="form.primaryItemName"
        type="text"
        :placeholder="originalName || '1차 분류명'"
        :error="errors.primaryItemName"
      />
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
  import { computed, ref, watch } from 'vue';
  import { updatePrimaryItem } from '@/features/items/api/items.js';
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

  const originalName = ref('');

  watch(
    () => props.modelValue,
    val => {
      console.log('🚀 Incoming modelValue:', val); // DEBUG
      if (val?.primaryItemName) {
        originalName.value = val.primaryItemName;
      }
    },
    { immediate: true }
  );

  const showDeleteModal = ref(false);

  const errors = ref({
    primaryItemName: '',
  });

  const submit = async () => {
    errors.value.primaryItemName = '';

    if (!form.value.primaryItemId) {
      console.error('❌ primaryItemId가 undefined입니다:', form.value);
      emit('toast', '1차 상품 ID가 누락되었습니다.');
      return;
    }

    if (!form.value.primaryItemName || form.value.primaryItemName.trim() === '') {
      errors.value.primaryItemName = '1차 분류명을 입력해주세요.';
      return;
    }

    try {
      await updatePrimaryItem({
        primaryItemId: form.value.primaryItemId,
        shopId: 1,
        category: form.value.category,
        primaryItemName: form.value.primaryItemName.trim(),
      });

      emit('submit', form.value);
      emit('toast', '1차 상품이 수정되었습니다.');
      emit('close');
    } catch (e) {
      console.error('🧨 오류:', e);
      emit('toast', e.response?.data?.message || '1차 상품 수정에 실패했습니다.');
    }
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
