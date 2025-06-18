<template>
  <div class="staff-form">
    <div class="profile-section">
      <ProfileUpload v-model="form.profileImage" />

      <div class="color-picker-inline">
        <span class="color-label">대표 색상</span>
        <PickColors v-model:value="form.colorCode" class="color-picker" />
      </div>
    </div>

    <BaseForm v-model="form.name" label="직원명" />
    <BaseForm v-model="form.username" label="아이디" />
    <BaseForm v-model="form.phone" label="전화번호" />
    <BaseForm v-model="form.position" label="직급" />
    <BaseForm
      v-model="form.status"
      label="재직 상태"
      type="select"
      :options="[
        { value: true, text: '재직중' },
        { value: false, text: '퇴사' },
      ]"
    />
    <PrimeDatePicker v-model="form.joinDate" label="입사일" />
    <PrimeDatePicker v-if="form.status === false" v-model="form.retireDate" label="퇴사일" />
    <BaseForm v-model="form.description" label="메모" type="textarea" :rows="4" />
  </div>
</template>
<script setup>
  import { ref, watch } from 'vue';
  import PickColors from 'vue-pick-colors';
  import ProfileUpload from '@/features/users/components/ProfileUpload.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';

  const props = defineProps({ modelValue: Object });
  const emit = defineEmits(['update:modelValue']);

  const form = ref({});

  watch(
    () => props.modelValue,
    val => {
      console.log('[DEBUG] 받은 값:', val);
      form.value = { ...val, colorCode: val?.colorCode || '#3FC1C9' };
    },
    { immediate: true, deep: true }
  );

  watch(form, val => emit('update:modelValue', val), { deep: true });
</script>

<style scoped>
  .staff-form {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 24px;
    border: 1px solid #e0e0e0;
    border-radius: 12px;
    background-color: #fff;
    max-width: 600px;
    margin: 0 auto;
  }
  .profile-section {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .color-picker {
    margin-top: 4px;
    z-index: 10;
  }
  .color-picker-inline {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .color-label {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    white-space: nowrap;
  }
</style>
