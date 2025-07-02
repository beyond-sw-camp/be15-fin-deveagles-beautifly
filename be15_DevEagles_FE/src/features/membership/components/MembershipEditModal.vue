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

    <!-- 횟수 -->
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

    <!-- 혜택 -->
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
          placeholder="기간 입력"
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
      <div class="footer-buttons-between">
        <div class="left-group">
          <BaseButton type="primary" outline @click="$emit('close')">닫기</BaseButton>
          <BaseButton type="primary" @click="submit">수정</BaseButton>
        </div>
        <div class="right-group">
          <BaseButton type="error" outline @click="showDeleteModal = true">삭제</BaseButton>
        </div>
      </div>

      <MembershipDeleteModal
        v-model="showDeleteModal"
        @confirm="confirmDelete"
        @toast="msg => emit('toast', msg)"
      />
    </template>
  </BaseItemModal>
</template>

<script setup>
  import { computed, onMounted, watch, ref } from 'vue';
  import BaseItemModal from '@/features/items/components/BaseItemModal.vue';
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import MembershipDeleteModal from '@/features/membership/components/MembershipDeleteModal.vue';
  import {
    updatePrepaidPass,
    updateSessionPass,
    deletePrepaidPass,
    deleteSessionPass,
  } from '@/features/membership/api/membership';
  import '@/features/membership/styles/MembershipModal.css';

  const props = defineProps({ modelValue: Object });
  const emit = defineEmits(['close', 'submit', 'update:modelValue']);
  const showDeleteModal = ref(false);
  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  watch(
    () => form.value.bonusType,
    newType => {
      if (newType !== 'DISSESSION_RATE') form.value.dissessionRate = null;
      if (newType !== 'EXTRA_BONUS') form.value.extraCount = null;
    }
  );

  watch(
    () => form.value.type,
    type => {
      if (type !== 'SESSION') form.value.session = null;
    }
  );

  onMounted(() => {
    form.value.type = form.value.session != null ? 'SESSION' : 'PREPAID';

    if (form.value.type === 'SESSION') {
      form.value.name = form.value.sessionPassName;
      form.value.price = form.value.sessionPassPrice;
      form.value.memo = form.value.sessionPassMemo;
      form.value.id = form.value.sessionPassId;
    } else {
      form.value.name = form.value.prepaidPassName;
      form.value.price = form.value.prepaidPassPrice;
      form.value.memo = form.value.prepaidPassMemo;
      form.value.id = form.value.prepaidPassId;
    }

    const unitRaw = form.value.expirationPeriodType;
    const unitUpper = typeof unitRaw === 'string' ? unitRaw.toUpperCase() : '';
    const validExpireUnits = ['MONTH', 'WEEK', 'DAY', 'YEAR'];
    form.value.expireUnit = validExpireUnits.includes(unitUpper) ? unitUpper : 'DAY';

    form.value.expireValue = form.value.expirationPeriod ?? 1;

    if (form.value.discountRate != null) {
      form.value.bonusType = 'DISSESSION_RATE';
      form.value.dissessionRate = form.value.discountRate;
    } else if (form.value.bonus != null) {
      form.value.bonusType = 'EXTRA_BONUS';
      form.value.extraCount = form.value.bonus;
    } else {
      form.value.bonusType = '';
    }

    if (!form.value.memo) {
      form.value.memo = form.value.prepaidPassMemo ?? form.value.sessionPassMemo ?? '';
    }
  });

  const dissessionRateOptions = Array.from({ length: 10 }, (_, i) => ({
    value: (i + 1) * 5,
    text: `${(i + 1) * 5}%`,
  }));

  const formattedDissessionAmount = computed(() => {
    if (!form.value.price || !form.value.dissessionRate) return '0';
    const amount = Math.floor((form.value.price * form.value.dissessionRate) / 100);
    return amount.toLocaleString();
  });

  const submit = async () => {
    try {
      const expirationPeriod = form.value.expireValue;
      const expirationPeriodType = form.value.expireUnit;

      const commonPayload = {
        shopId: 1, // 실제 shopId 설정
        expirationPeriod,
        expirationPeriodType,
        bonus: form.value.bonusType === 'EXTRA_BONUS' ? form.value.extraCount : null,
        discountRate: form.value.bonusType === 'DISSESSION_RATE' ? form.value.dissessionRate : null,
        prepaidPassMemo: form.value.memo,
        sessionPassMemo: form.value.memo,
      };

      if (form.value.type === 'PREPAID') {
        await updatePrepaidPass({
          ...commonPayload,
          prepaidPassId: form.value.id,
          prepaidPassName: form.value.name,
          prepaidPassPrice: form.value.price,
        });
      } else if (form.value.type === 'SESSION') {
        await updateSessionPass({
          ...commonPayload,
          sessionPassId: form.value.id,
          sessionPassName: form.value.name,
          sessionPassPrice: form.value.price,
          session: form.value.session,
        });
      } else {
        throw new Error('회원권 종류를 선택해주세요.');
      }

      emit('submit');
      emit('close');
    } catch (e) {
      console.error(e);
      alert('회원권 수정 중 오류가 발생했습니다.');
    }
  };

  const confirmDelete = async () => {
    try {
      if (form.value.type === 'PREPAID') {
        await deletePrepaidPass(form.value.id);
      } else if (form.value.type === 'SESSION') {
        await deleteSessionPass(form.value.id);
      } else {
        throw new Error('회원권 종류를 알 수 없습니다.');
      }

      emit('delete', form.value);
      emit('toast', '회원권이 삭제되었습니다.');
      emit('close');
      showDeleteModal.value = false;
    } catch (e) {
      console.error(e);
      alert('회원권 삭제 중 오류가 발생했습니다.');
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
  .right-group {
    display: flex;
  }
  .expire-input-wrapper {
    display: flex;
    gap: 8px;
    align-items: center;
  }
  .footer-buttons-between {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
</style>
