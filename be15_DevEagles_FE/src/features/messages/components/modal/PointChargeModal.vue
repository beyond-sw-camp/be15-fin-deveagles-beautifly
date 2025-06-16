<script setup>
  import { ref } from 'vue';

  const emit = defineEmits(['confirm', 'close']);
  const amount = ref(0);

  function confirmCharge() {
    const parsed = parseInt(amount.value, 10);
    if (isNaN(parsed) || parsed <= 0) {
      alert('충전할 포인트를 올바르게 입력해 주세요.');
      return;
    }
    emit('confirm', parsed);
  }
</script>

<template>
  <div class="modal-backdrop">
    <div class="modal">
      <!-- ✅ 헤더 -->
      <div class="modal-header">
        <h3 class="modal-title">포인트 충전</h3>
      </div>

      <!-- ✅ 본문 -->
      <div class="modal-body">
        <label class="label text--sm">충전할 포인트</label>
        <input v-model="amount" type="number" class="input" placeholder="숫자를 입력하세요" />
      </div>

      <!-- ✅ 푸터 -->
      <div class="modal-footer">
        <button class="btn btn--primary btn--sm" @click="confirmCharge">충전</button>
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
