<template>
  <BaseItemModal title="회원권 등록" @close="$emit('close')" @submit="submit">
    <div class="form-group">
      <label>선불권/횟수권 선택</label>
      <BaseForm
        v-model="form.type"
        type="select"
        :options="[
          { value: 'PREPAID', text: '선불권' },
          { value: 'COUNT', text: '횟수권' },
        ]"
      />
    </div>

    <div class="form-group">
      <label>선불권/횟수권 명</label>
      <BaseForm v-model="form.name" placeholder="선불권/횟수권 이름을 입력하세요." />
    </div>

    <div v-if="form.type === 'COUNT'" class="form-group">
      <label>횟수</label>
      <BaseForm v-model="form.count" type="number" placeholder="횟수를 입력하세요." />
    </div>

    <div class="form-group">
      <label>가격</label>
      <BaseForm v-model="form.price" type="number" step="100" placeholder="가격을 입력하세요." />
    </div>

    <div class="form-group">
      <label>제공 혜택</label>
      <BaseForm
        v-model="form.bonusType"
        type="select"
        :options="[
          { value: '', text: '없음' },
          { value: 'EXTRA_BONUS', text: '제공량 추가' },
        ]"
      />
    </div>

    <div v-if="form.bonusType === 'EXTRA_BONUS'" class="form-group">
      <label>추가 제공량</label>
      <BaseForm v-model="form.extraCount" placeholder="추가 제공량을 입력하세요." />
    </div>

    <div class="form-group">
      <label>유효기간</label>
      <div class="expire-input-wrapper">
        <BaseForm
          v-model="form.expireValue"
          type="number"
          placeholder="기간을 입력하세요."
          class="expire-value"
        />
        <BaseForm
          v-model="form.expireUnit"
          type="select"
          class="expire-unit"
          :options="[
            { value: 'MONTH', text: '개월' },
            { value: 'WEEK', text: '주' },
            { value: 'DAY', text: '일' },
            { value: 'YEAR', text: '년' },
          ]"
        />
      </div>
    </div>

    <div class="form-group">
      <label>메모</label>
      <BaseForm v-model="form.memo" placeholder="메모" />
    </div>

    <template #footer>
      <div class="footer-buttons">
        <div class="left-group">
          <BaseButton type="primary" outline @click="$emit('close')">닫기</BaseButton>
          <BaseButton type="primary" @click="submit">등록</BaseButton>
        </div>
      </div>
    </template>
  </BaseItemModal>
</template>

<script setup>
  import { computed, onMounted } from 'vue';
  import BaseItemModal from '@/features/item/components/BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import '@/features/membership/styles//MembershipModal.css';

  const props = defineProps({
    modelValue: Object,
  });
  const emit = defineEmits(['close', 'submit', 'update:modelValue']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  onMounted(() => {
    if (!form.value.expireUnit) {
      form.value.expireUnit = 'MONTH';
    }
    if (form.value.bonusType === undefined || form.value.bonusType === null) {
      form.value.bonusType = '';
    }
  });

  const submit = () => {
    emit('submit', form.value);
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

  .expire-input-wrapper {
    display: flex;
    gap: 8px;
    align-items: center;
  }
</style>
