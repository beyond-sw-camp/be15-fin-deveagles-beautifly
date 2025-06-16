<script setup>
  import { ref } from 'vue';

  const emit = defineEmits(['close', 'confirm']);
  const number = ref('');

  function submit() {
    if (!number.value.trim()) {
      alert('발신 번호를 입력해주세요.');
      return;
    }
    emit('confirm', number.value);
  }
</script>

<template>
  <div class="modal-backdrop">
    <div class="modal">
      <h3 class="modal-title">발신 정보 등록</h3>
      <div class="modal-body">
        <label class="label">발신 번호</label>
        <input v-model="number" type="text" class="input" placeholder="예: 01000000000" />
      </div>
      <div class="modal-footer">
        <button class="btn btn--primary btn--sm" @click="submit">등록</button>
        <button class="btn btn--gray btn--sm" @click="$emit('close')">취소</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .modal-backdrop {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(4px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }

  .modal {
    background-color: white;
    width: 360px;
    border-radius: 0.5rem;
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .modal-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: var(--color-neutral-dark);
  }

  .modal-body {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
  }
</style>
