<template>
  <div class="staff-form">
    <div class="profile-section">
      <ProfileUpload v-model="form.profileUrl" />

      <div class="color-picker-inline">
        <PickColors v-model:value="form.colorCode" class="color-picker" />

        <div class="tooltip-wrapper">
          <InfoTooltipIcon />
          <div class="tooltip-bubble">대표 색상을 변경할 수 있어요</div>
        </div>
      </div>
    </div>

    <BaseForm v-model="form.staffName" label="직원명" />
    <BaseForm v-model="form.email" label="이메일" />
    <BaseForm v-model="form.phoneNumber" label="전화번호" />
    <BaseForm v-model="form.grade" label="직급" />
    <BaseForm
      v-model="form.working"
      label="재직 상태"
      type="select"
      :options="[
        { value: true, text: '재직중' },
        { value: false, text: '퇴사' },
      ]"
    />
    <PrimeDatePicker v-model="form.joinedDate" label="입사일" />
    <PrimeDatePicker v-show="form.working === false" v-model="form.leftDate" label="퇴사일" />
    <BaseForm v-model="form.description" label="메모" type="textarea" :rows="4" />
  </div>
</template>
<script setup>
  import { ref, watch } from 'vue';
  import PickColors from 'vue-pick-colors';
  import ProfileUpload from '@/features/users/components/ProfileUpload.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';
  import InfoTooltipIcon from '@/components/icons/InfoTooltipIcon.vue';

  const props = defineProps({ modelValue: Object });
  const emit = defineEmits(['update:modelValue']);

  const form = ref({});

  // 1. props → 내부 form
  watch(
    () => props.modelValue,
    val => {
      if (JSON.stringify(val) !== JSON.stringify(form.value)) {
        form.value = {
          ...val,
          working: Boolean(val?.working),
          colorCode: val?.colorCode || '#3FC1C9',
        };
      }
    },
    { immediate: true, deep: true }
  );

  // 2. 재직 상태 바뀔 때 퇴사일 초기화
  // "false" (string) → false (boolean) 로 변환
  watch(
    () => form.value.working,
    newVal => {
      if (newVal === 'false') {
        form.value.working = false;
      } else if (newVal === 'true') {
        form.value.working = true;
      }
    }
  );

  watch(
    () => form.value.working,
    newVal => {
      if (newVal === true) {
        form.value.leftDate = null;
      }
    }
  );

  // 3. 내부 form → 부모 emit
  watch(
    () => form.value,
    val => {
      emit('update:modelValue', val);
    },
    { deep: false }
  );
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
    gap: 0;
  }

  .tooltip-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    margin-left: 0px;
    transform: translateX(-0.5px);
  }

  .tooltip-icon {
    width: 16px;
    height: 16px;
    color: #9ca3af;
    cursor: default;
  }

  .tooltip-bubble {
    position: absolute;
    bottom: 125%;
    left: 50%;
    transform: translateX(-50%);
    background-color: #333;
    color: white;
    padding: 6px 8px;
    border-radius: 6px;
    font-size: 12px;
    white-space: nowrap;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.2s ease;
    z-index: 10;
  }

  .tooltip-wrapper:hover .tooltip-bubble {
    opacity: 1;
    pointer-events: auto;
  }
  .staff-form :deep(.input-container) {
    margin-bottom: 16px;
  }
</style>
