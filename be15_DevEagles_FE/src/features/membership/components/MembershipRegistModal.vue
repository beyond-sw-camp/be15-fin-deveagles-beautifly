<template>
  <BaseItemModal title="회원권 등록" @close="$emit('close')" @submit="submit">
    <!-- 타입 선택 -->
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

    <!-- 이름 -->
    <div class="form-group">
      <label>선불권/횟수권 명</label>
      <BaseForm v-model="form.name" placeholder="이름을 입력하세요." />
    </div>

    <!-- 횟수 (SESSION만 노출) -->
    <div v-if="form.type === 'SESSION'" class="form-group">
      <label>횟수</label>
      <BaseForm v-model.number="form.session" type="number" placeholder="횟수를 입력하세요." />
    </div>

    <!-- 가격 -->
    <div class="form-group">
      <label>가격</label>
      <BaseForm
        v-model.number="form.price"
        type="number"
        step="100"
        placeholder="가격을 입력하세요."
      />
    </div>

    <!-- 제공 혜택 -->
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

    <!-- 할인율 -->
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

    <!-- 추가 제공량 -->
    <div v-if="form.bonusType === 'EXTRA_BONUS'" class="form-group">
      <label>추가 제공량</label>
      <BaseForm v-model.number="form.extraCount" type="number" placeholder="제공량을 입력하세요." />
    </div>

    <!-- 유효기간 -->
    <div class="form-group">
      <label>유효기간</label>
      <div class="expire-input-wrapper">
        <BaseForm
          v-model.number="form.expireValue"
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

    <!-- 메모 -->
    <div class="form-group">
      <label>메모</label>
      <BaseForm v-model="form.memo" placeholder="메모를 입력하세요." />
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

  const dissessionRateOptions = Array.from({ length: 10 }, (_, i) => ({
    value: (i + 1) * 5,
    text: `${(i + 1) * 5}%`,
  }));

  const formattedDissessionAmount = computed(() => {
    if (!form.value.price || !form.value.dissessionRate) return '0';
    const amount = Math.floor((form.value.price * form.value.dissessionRate) / 100);
    return amount.toLocaleString();
  });

  onMounted(() => {
    if (!form.value.expireUnit) form.value.expireUnit = 'MONTH';
    if (form.value.bonusType === undefined || form.value.bonusType === null) {
      form.value.bonusType = '';
    }
  });

  const submit = async () => {
    try {
      const expirationPeriod = Number(form.value.expireValue);
      const expirationPeriodType = form.value.expireUnit;

      const bonus = form.value.bonusType === 'EXTRA_BONUS' ? form.value.extraCount : null;
      const discountRate =
        form.value.bonusType === 'DISSESSION_RATE' ? form.value.dissessionRate : null;

      if (form.value.type === 'PREPAID') {
        await registerPrepaidPass({
          shopId: 1, // 실제 shopId 적용
          prepaidPassId: null,
          prepaidPassName: form.value.name,
          prepaidPassPrice: form.value.price,
          expirationPeriod,
          expirationPeriodType, // 그대로 전달
          bonus,
          discountRate,
          prepaidPassMemo: form.value.memo,
        });
      } else if (form.value.type === 'SESSION') {
        await registerSessionPass({
          shopId: 1,
          sessionPassId: null,
          sessionPassName: form.value.name,
          sessionPassPrice: form.value.price,
          session: form.value.session,
          expirationPeriod,
          expirationPeriodType,
          bonus,
          discountRate,
          sessionPassMemo: form.value.memo,
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
