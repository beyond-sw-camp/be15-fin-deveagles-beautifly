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
      <BaseForm
        v-model="form.primaryName"
        type="text"
        placeholder="1차 분류명"
        :error="errors.primaryName"
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
  import { ref, watch } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { registerPrimaryItem } from '@/features/items/api/items.js';

  const props = defineProps({
    show: Boolean, // 모달 표시 여부
  });

  const emit = defineEmits(['close', 'submit', 'toast']);

  const initialForm = {
    category: 'SERVICE',
    primaryName: '',
  };

  const form = ref({ ...initialForm });
  const errors = ref({
    primaryName: '',
  });

  // 모달이 열릴 때마다 form과 errors 초기화
  watch(
    () => props.show,
    show => {
      if (show) {
        form.value = { ...initialForm };
        errors.value.primaryName = '';
      }
    }
  );

  const submit = async () => {
    errors.value.primaryName = ''; // 초기화

    if (!form.value.primaryName || form.value.primaryName.trim() === '') {
      errors.value.primaryName = '1차 분류명을 입력해주세요.';
      return;
    }

    try {
      const payload = {
        shopId: 1, // 추후 로그인 사용자 기준으로 교체
        category: form.value.category,
        primaryItemName: form.value.primaryName.trim(),
      };

      await registerPrimaryItem(payload);

      emit('toast', '1차 상품이 등록되었습니다.');
      emit('submit');
      emit('close');
    } catch (error) {
      console.error(error);
      emit('toast', '등록에 실패했습니다. 다시 시도해주세요.');
    }
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
