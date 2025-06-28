<template>
  <BaseItemModal title="회원권 등록" @close="$emit('close')" @submit="submit">
    <div class="form-group">
      <label>선불권/횟수권 선택</label>
      <BaseForm
        v-model="form.type"
        type="select"
        :options="[
          { value: 'PREPAID', text: '선불권' },
          { value: 'SESSION', text: '횟수권' },
        ]"
      />
    </div>

    <div class="form-group">
      <label>선불권/횟수권 명</label>
      <BaseForm v-model="form.name" placeholder="선불권/횟수권 이름을 입력하세요." />
    </div>

    <div v-if="form.type === 'SESSION'" class="form-group">
      <label>횟수</label>
      <BaseForm v-model="form.session" type="number" placeholder="횟수를 입력하세요." />
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
          { value: 'DISSESSION_RATE', text: '할인' },
          { value: 'EXTRA_BONUS', text: '제공량 추가' },
        ]"
      />
    </div>

    <div v-if="form.bonusType === 'DISSESSION_RATE'" class="form-group">
      <label>할인율 및 할인금액</label>
      <div class="expire-input-wrapper">
        <BaseForm
          :model-value="formattedDissessionAmount"
          readonly
          class="dissession-amount-input"
          placeholder="0"
        />
        <BaseForm
          v-model.number="form.dissessionRate"
          type="select"
          :options="dissessionRateOptions"
          placeholder="할인율"
          class="expire-value"
        />
      </div>
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
  import { computed, onMounted, watch } from 'vue';
  import BaseItemModal from '@/features/items/components/BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { registerPrepaidPass, registerSessionPass } from '@/features/membership/api/membership';
  import '@/features/membership/styles/MembershipModal.css';

  const props = defineProps({ modelValue: Object });
  const emit = defineEmits(['close', 'submit', 'update:modelValue']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  watch(
    () => props.modelValue,
    val => {
      if (val) {
        if (!val.expireUnit) val.expireUnit = 'MONTH';
        if (!val.bonusType) val.bonusType = '';
        if (val.bonusType !== 'DISSESSION_RATE') val.dissessionRate = null;
        if (val.bonusType !== 'EXTRA_BONUS') val.extraCount = null;
        if (val.type !== 'SESSION') val.session = null;
      }
    },
    { immediate: true, deep: true }
  );

  // 할인율 옵션
  const dissessionRateOptions = Array.from({ length: 10 }, (_, i) => ({
    value: (i + 1) * 5,
    text: `${(i + 1) * 5}%`,
  }));

  // 할인 금액 계산
  const formattedDissessionAmount = computed(() => {
    if (!form.value.price || !form.value.dissessionRate) return '0';
    const dissession = Math.floor((form.value.price * form.value.dissessionRate) / 100);
    return dissession.toLocaleString();
  });

  onMounted(() => {
    if (!form.value.expireUnit) form.value.expireUnit = 'MONTH';
    if (form.value.bonusType === undefined || form.value.bonusType === null) {
      form.value.bonusType = '';
    }
  });

  // 등록 처리
  const submit = async () => {
    try {
      const period = Number(form.value.expireValue);
      const unit = form.value.expireUnit;
      let expirationPeriod = 0;

      if (unit === 'DAY') expirationPeriod = period * 1;
      else if (unit === 'WEEK') expirationPeriod = period * 7;
      else if (unit === 'MONTH') expirationPeriod = period * 30;
      else if (unit === 'YEAR') expirationPeriod = period * 365;

      const basePayload = {
        shopId: 1, // 실제 shopId로 교체하세요
        expirationPeriod,
        bonus: form.value.bonusType === 'EXTRA_BONUS' ? form.value.extraCount : null,
        discountRate: form.value.bonusType === 'DISSESSION_RATE' ? form.value.dissessionRate : null,
        prepaidPassMemo: form.value.memo,
        sessionPassMemo: form.value.memo,
      };

      if (form.value.type === 'PREPAID') {
        await registerPrepaidPass({
          ...basePayload,
          prepaidPassId: null,
          prepaidPassName: form.value.name,
          prepaidPassPrice: form.value.price,
        });
      } else if (form.value.type === 'SESSION') {
        await registerSessionPass({
          ...basePayload,
          sessionPassId: null,
          sessionPassName: form.value.name,
          sessionPassPrice: form.value.price,
          session: form.value.session,
        });
      } else {
        throw new Error('회원권 종류를 선택해주세요.');
      }

      emit('submit');
      emit('close');
    } catch (err) {
      console.error(err);
      alert('회원권 등록 중 오류가 발생했습니다.');
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
  .expire-input-wrapper {
    display: flex;
    gap: 8px;
    align-items: center;
  }
</style>
