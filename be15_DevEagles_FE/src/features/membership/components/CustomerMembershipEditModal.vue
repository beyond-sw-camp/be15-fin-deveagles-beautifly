<template>
  <Teleport to="body">
    <div class="edit-modal-layer">
      <BaseModal
        :model-value="modelValue"
        title="회원권 수정"
        animation-class="back-in-left"
        @update:model-value="$emit('close')"
      >
        <div class="edit-form">
          <!-- 조건부 필드: 선불권 -->
          <div v-if="type === 'PREPAID'" class="form-group">
            <label>잔여 선불액</label>
            <input v-model.number="prepaid" type="number" step="100" placeholder="잔여 선불권" />
          </div>

          <!-- 조건부 필드: 횟수권 -->
          <div v-if="type === 'COUNT'" class="form-group">
            <label>잔여 횟수권</label>
            <input v-model.number="count" type="number" placeholder="잔여 횟수권" />
          </div>

          <!-- ✅ 공통: 만료(예정)일 -->
          <div class="form-group">
            <label>만료(예정)일</label>
            <input v-model="expiry" type="date" />
          </div>
        </div>

        <template #footer>
          <div class="footer-buttons">
            <button class="cancel-button" @click="$emit('close')">닫기</button>
            <button class="apply-button" @click="submitEdit">저장</button>
          </div>
        </template>
      </BaseModal>
    </div>
  </Teleport>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';

  const props = defineProps({
    modelValue: Boolean,
    membership: {
      type: Object,
      required: true,
    },
  });

  const emit = defineEmits(['close', 'submit']);

  const prepaid = ref(null);
  const count = ref(null);
  const expiry = ref(null);
  const type = ref('');

  watch(
    () => props.membership,
    val => {
      if (!val) return;
      type.value = val.type;
      prepaid.value = val.remaining ?? null;
      count.value = val.remaining ?? null;
      expiry.value = val.expiry ?? null;
    },
    { immediate: true }
  );

  const submitEdit = () => {
    emit('submit', {
      id: props.membership.id,
      type: type.value,
      prepaid: prepaid.value,
      count: count.value,
      expiry: expiry.value,
    });
  };
</script>

<style scoped>
  .edit-modal-layer {
    position: fixed;
    inset: 0;
    z-index: 11000;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .edit-form {
    padding: 1rem 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  label {
    font-weight: bold;
    font-size: 15px;
  }

  input {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #aaa;
    border-radius: 4px;
  }

  .footer-buttons {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding: 0 1.5rem 1rem;
  }

  .cancel-button {
    padding: 6px 14px;
    background-color: white;
    border: 1px solid black;
    border-radius: 4px;
    cursor: pointer;
  }

  .apply-button {
    padding: 6px 14px;
    background-color: black;
    color: white;
    border-radius: 4px;
    cursor: pointer;
  }
</style>

<style>
  .modal-backdrop {
    z-index: 11000 !important;
  }
</style>
