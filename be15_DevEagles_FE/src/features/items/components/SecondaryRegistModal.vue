<template>
  <BaseItemModal title="2차 분류 상품 등록" @close="handleClose" @submit="submit">
    <!-- 1차 분류명 -->
    <div class="form-group">
      <label for="primary">1차 분류명</label>
      <BaseForm
        id="primary"
        v-model.number="form.primaryItemId"
        type="select"
        :options="
          primaryOptions.map(item => ({ value: item.primaryItemId, text: item.primaryItemName }))
        "
        placeholder="1차 분류명"
        :error="errors.primaryItemId"
      />
    </div>

    <!-- 2차 분류명 -->
    <div class="form-group">
      <label for="secondaryName">2차 분류명</label>
      <BaseForm
        id="secondaryName"
        v-model="form.secondaryName"
        type="text"
        placeholder="2차 분류명"
        :error="errors.secondaryName"
      />
    </div>

    <!-- 상품 금액 -->
    <div class="form-group">
      <label for="price">상품 금액</label>
      <BaseForm
        id="price"
        v-model.number="form.price"
        type="number"
        step="100"
        placeholder="금액"
        :error="errors.price"
      />
    </div>

    <!-- 시술 시간 -->
    <div v-if="selectedPrimary?.category === 'SERVICE'" class="form-group">
      <label for="duration">시술 시간 (분)</label>
      <BaseForm
        id="duration"
        v-model.number="form.duration"
        type="number"
        step="5"
        placeholder="예: 60"
        :error="errors.duration"
      />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <div class="left-group">
          <BaseButton type="primary" outline @click="handleClose">취소</BaseButton>
          <BaseButton type="primary" @click="submit">등록</BaseButton>
        </div>
      </div>
    </template>
  </BaseItemModal>
</template>

<script setup>
  import { ref, reactive, computed, onMounted } from 'vue';
  import BaseItemModal from './BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { getPrimaryItems, registerSecondaryItem } from '@/features/items/api/items.js';

  const emit = defineEmits(['close', 'submit', 'toast']);

  const defaultForm = () => ({
    primaryItemId: null,
    secondaryName: '',
    price: null,
    duration: null,
  });

  const form = reactive(defaultForm());
  const errors = reactive({
    primaryItemId: '',
    secondaryName: '',
    price: '',
    duration: '',
  });

  const primaryOptions = ref([]);

  const selectedPrimary = computed(() =>
    primaryOptions.value.find(opt => opt.primaryItemId === form.primaryItemId)
  );

  const validate = () => {
    let valid = true;
    errors.primaryItemId = form.primaryItemId ? '' : '1차 분류를 선택해주세요.';
    errors.secondaryName = form.secondaryName.trim() ? '' : '2차 분류명을 입력해주세요.';
    errors.price = form.price ? '' : '상품 금액을 입력해주세요.';
    errors.duration =
      selectedPrimary.value?.category === 'SERVICE' && !form.duration
        ? '시술 시간을 입력해주세요.'
        : '';

    return !errors.primaryItemId && !errors.secondaryName && !errors.price && !errors.duration;
  };

  const submit = async () => {
    if (!validate()) return;

    try {
      await registerSecondaryItem({
        primaryItemId: form.primaryItemId,
        secondaryItemName: form.secondaryName.trim(),
        secondaryItemPrice: form.price,
        timeTaken: selectedPrimary.value?.category === 'SERVICE' ? form.duration : null,
      });

      emit('toast', '2차 상품이 등록되었습니다');
      emit('submit');
      handleClose();
    } catch (err) {
      emit('toast', err.response?.data?.message || '등록 중 오류가 발생했습니다');
    }
  };

  const handleClose = () => {
    Object.assign(form, defaultForm());
    Object.keys(errors).forEach(key => (errors[key] = ''));
    emit('close');
  };

  onMounted(async () => {
    try {
      const res = await getPrimaryItems();
      primaryOptions.value = res;
    } catch (err) {
      emit('toast', '1차 상품 목록을 불러오는 데 실패했습니다.');
    }
  });
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
