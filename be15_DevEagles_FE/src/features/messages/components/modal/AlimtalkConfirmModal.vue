<script setup>
  const emit = defineEmits(['confirm', 'close']);
</script>

<template>
  <div class="modal-backdrop">
    <div class="modal">
      <div class="modal-header">
        <h3 class="modal-title">알림톡 신청</h3>
      </div>
      <div class="modal-body">
        <p class="text-sm">
          카카오 연동 없이 신청 여부만 저장합니다.<br />
          신청하시겠습니까?
        </p>
      </div>
      <div class="modal-footer">
        <button class="btn btn--primary btn--sm" @click="$emit('confirm')">신청</button>
        <button class="btn btn--gray btn--sm" @click="$emit('close')">취소</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .modal-backdrop {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(2px); /* ✅ 블러 처리 */
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
    gap: 0.75rem;
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
  }
</style>
