<template>
  <BaseItemModal title="회원권 수정" @close="$emit('close')" @submit="submit">
    <div class="form-group">
      <label>선불권/횟수권 선택</label>
      <select v-model="form.type" disabled>
        <option value="PREPAID">선불권</option>
        <option value="COUNT">횟수권</option>
      </select>
    </div>

    <div class="form-group">
      <label>선불권/횟수권 명</label>
      <input v-model="form.name" placeholder="이름을 입력하세요." />
    </div>

    <div v-if="form.type === 'COUNT'" class="form-group">
      <label>횟수</label>
      <input v-model="form.count" type="number" placeholder="횟수를 입력하세요." />
    </div>

    <div class="form-group">
      <label>가격</label>
      <input v-model="form.price" type="number" step="100" placeholder="가격을 입력하세요." />
    </div>

    <div class="form-group">
      <label>제공 혜택</label>
      <select v-model="form.bonusType">
        <option value="">없음</option>
        <option value="EXTRA_BONUS">제공량 추가</option>
      </select>
    </div>

    <div v-if="form.bonusType === 'EXTRA_BONUS'" class="form-group">
      <label>추가 제공량</label>
      <input v-model="form.extraCount" placeholder="추가 제공량을 입력하세요." />
    </div>

    <div class="form-group">
      <label>유효기간</label>
      <div class="expire-input-wrapper">
        <input
          v-model="form.expireValue"
          type="number"
          placeholder="기간을 입력하세요."
          class="expire-value"
        />
        <select v-model="form.expireUnit" class="expire-unit">
          <option value="MONTH">개월</option>
          <option value="WEEK">주</option>
          <option value="DAY">일</option>
          <option value="YEAR">년</option>
        </select>
      </div>
    </div>

    <div class="form-group">
      <label>메모</label>
      <input v-model="form.memo" placeholder="메모" />
    </div>

    <template #footer>
      <div class="footer-buttons-between">
        <div class="left-group">
          <button class="cancel-button" @click="$emit('close')">닫기</button>
          <button class="submit-button" @click="submit">수정</button>
        </div>
        <div class="right-group">
          <button class="delete-button" @click="showDeleteModal = true">삭제</button>
        </div>
      </div>

      <!-- 삭제 확인 모달 -->
      <MembershipDeleteModal v-model="showDeleteModal" @confirm="confirmDelete" />
    </template>
  </BaseItemModal>
</template>

<script setup>
  import { computed, onMounted, ref } from 'vue';
  import BaseItemModal from '@/features/item/components/BaseItemModal.vue';
  import MembershipDeleteModal from '@/features/membership/components/MembershipDeleteModal.vue';

  const props = defineProps({
    modelValue: Object,
  });
  const emit = defineEmits(['close', 'submit', 'delete', 'update:modelValue']);

  const form = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const showDeleteModal = ref(false);

  const confirmDelete = () => {
    emit('delete', form.value);
    showDeleteModal.value = false;
  };

  const submit = () => {
    emit('submit', form.value);
  };

  onMounted(() => {
    if (!form.value.expireUnit) form.value.expireUnit = 'MONTH';
    if (form.value.bonusType === undefined || form.value.bonusType === null) {
      form.value.bonusType = '';
    }
  });
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

  input,
  select {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }

  .expire-input-wrapper {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .expire-value,
  .expire-unit {
    flex: 1;
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }

  .footer-buttons-between {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .left-group {
    display: flex;
    gap: 8px;
  }

  .right-group {
    display: flex;
  }

  .cancel-button,
  .submit-button {
    padding: 8px 12px;
    border-radius: 4px;
    border: 1px solid #ccc;
    background: white;
    cursor: pointer;
  }

  .delete-button {
    padding: 8px 12px;
    border-radius: 4px;
    border: 1px solid #f00;
    color: #f00;
    background: white;
    cursor: pointer;
  }
</style>
