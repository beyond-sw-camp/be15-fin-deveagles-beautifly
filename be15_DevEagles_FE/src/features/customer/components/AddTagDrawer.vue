<template>
  <BaseDrawer
    :model-value="modelValue"
    title="태그 등록"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <form class="tag-form" @submit.prevent="handleSubmit">
      <div class="form-row">
        <label class="form-label">태그명<span class="required">*</span></label>
        <input
          v-model="tagName"
          type="text"
          class="form-input"
          maxlength="20"
          required
          placeholder="태그명을 입력하세요"
        />
      </div>
      <div class="form-row">
        <label class="form-label">색상<span class="required">*</span></label>
        <div class="color-palette">
          <div
            v-for="color in colorOptions"
            :key="color"
            class="color-swatch"
            :style="{
              backgroundColor: color,
              border: colorCode === color ? '2px solid #364f6b' : '2px solid #fff',
            }"
            :aria-label="color"
            :tabindex="0"
            @click="colorCode = color"
            @keydown.enter="colorCode = color"
          >
            <span v-if="colorCode === color" class="selected-dot"></span>
          </div>
        </div>
      </div>
    </form>
    <template #footer>
      <div class="tag-form-actions">
        <BaseButton type="secondary" outline @click="closeDrawer">취소</BaseButton>
        <BaseButton
          type="primary"
          native-type="submit"
          :disabled="!tagName.trim() || !colorCode"
          @click="handleSubmit"
          >등록</BaseButton
        >
      </div>
    </template>
    <BaseToast ref="toastRef" />
  </BaseDrawer>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  const props = defineProps({
    modelValue: { type: Boolean, required: true },
  });
  const emit = defineEmits(['update:modelValue', 'create']);

  const colorOptions = [
    '#ff4d4f',
    '#ff9800',
    '#ffe156',
    '#4caf50',
    '#2196f3',
    '#3f51b5',
    '#9c27b0',
    '#ff82ac',
    '#a0522d',
    '#222222',
  ];

  const tagName = ref('');
  const colorCode = ref(null); // 기본 선택 해제
  const toastRef = ref(null);

  watch(
    () => props.modelValue,
    val => {
      if (!val) {
        tagName.value = '';
        colorCode.value = null; // 초기화
      }
    }
  );

  function handleSubmit() {
    if (!tagName.value.trim() || !colorCode.value) return;
    emit('create', {
      tagName: tagName.value.trim(),
      colorCode: colorCode.value,
    });
    toastRef.value?.success('태그가 생성되었습니다.');
    emit('update:modelValue', false);
  }
  function closeDrawer() {
    emit('update:modelValue', false);
  }
</script>

<style scoped>
  .tag-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
    margin-top: 12px;
  }
  .form-row {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 8px;
  }
  .form-label {
    font-size: 14px;
    font-weight: 500;
    color: #222;
  }
  .required {
    color: #e53935;
    margin-left: 2px;
    font-size: 15px;
    font-weight: bold;
  }
  .form-input {
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 15px;
    padding: 0 12px;
    background: #fff;
    transition: border 0.2s;
  }
  .form-input:focus {
    outline: none;
    border-color: #364f6b;
    background: #f8fafd;
  }
  .color-palette {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 4px;
  }
  .color-swatch {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    cursor: pointer;
    border: 2px solid #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
    transition:
      border 0.15s,
      box-shadow 0.15s;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }
  .color-swatch:focus {
    outline: 2px solid #364f6b;
  }
  .selected-dot {
    width: 12px;
    height: 12px;
    background: #fff;
    border-radius: 50%;
    border: 2px solid #364f6b;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }
  .tag-form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 0;
  }
</style>
