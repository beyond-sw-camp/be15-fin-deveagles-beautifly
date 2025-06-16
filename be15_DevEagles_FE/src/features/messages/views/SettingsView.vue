<script setup>
  import { ref } from 'vue';
  import SenderInfoModal from '@/features/messages/components/modal/SenderInfoModal.vue';
  import AlimtalkConfirmModal from '@/features/messages/components/modal/AlimtalkConfirmModal.vue';
  import PointChargeModal from '@/features/messages/components/modal/PointChargeModal.vue';

  const sender = ref('');
  const showSenderModal = ref(false);

  const useAlimtalk = ref(false);
  const showAlimtalkModal = ref(false);

  const messagePoints = ref(1200);
  const showChargeModal = ref(false);

  function confirmSender(number) {
    sender.value = number;
    showSenderModal.value = false;
  }

  function confirmAlimtalk() {
    useAlimtalk.value = true;
    showAlimtalkModal.value = false;
  }

  function handleConfirmCharge(amount) {
    messagePoints.value += amount;
    showChargeModal.value = false;
  }
</script>

<template>
  <section class="settings-page">
    <!-- ✅ 발신 정보 등록 -->
    <div class="setting-card">
      <h2 class="title-md">발신 정보 등록</h2>
      <p class="text-sm text--gray-600">
        문자 또는 알림톡을 발송할 기본 발신 번호를 등록해 주세요.
      </p>

      <div class="setting-row">
        <div class="setting-label">발신 번호</div>
        <div
          class="setting-value"
          :class="!sender ? 'text--danger clickable' : ''"
          @click="!sender && (showSenderModal = true)"
        >
          {{ sender || '미등록' }}
        </div>
      </div>
    </div>

    <!-- ✅ 발송 환경 설정 -->
    <div class="setting-card">
      <h2 class="title-md">발송 환경 설정</h2>

      <div class="setting-row">
        <div class="setting-label">알림톡 신청 여부</div>
        <div
          class="setting-value"
          :class="!useAlimtalk ? 'text--danger clickable' : ''"
          @click="!useAlimtalk && (showAlimtalkModal = true)"
        >
          {{ useAlimtalk ? '신청 완료' : '미신청' }}
        </div>
      </div>

      <div class="setting-row">
        <div class="setting-label">문자 포인트</div>
        <div class="setting-value">
          <strong class="point-text">{{ messagePoints.toLocaleString() }}P</strong>
          <button class="btn--link" @click="showChargeModal = true">충전</button>
        </div>
      </div>
    </div>

    <!-- ✅ 모달들 -->
    <SenderInfoModal
      v-if="showSenderModal"
      @confirm="confirmSender"
      @close="showSenderModal = false"
    />
    <AlimtalkConfirmModal
      v-if="showAlimtalkModal"
      @confirm="confirmAlimtalk"
      @close="showAlimtalkModal = false"
    />
    <PointChargeModal
      v-if="showChargeModal"
      @confirm="handleConfirmCharge"
      @close="showChargeModal = false"
    />
  </section>
</template>

<style scoped>
  .settings-page {
    padding: 2rem;
    display: flex;
    flex-direction: column;
    gap: 2rem;
    background-color: var(--color-gray-50);
  }

  .setting-card {
    background-color: #fff;
    border-radius: 0.75rem;
    padding: 1.75rem 2rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .setting-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem 0;
    border-bottom: 1px dashed var(--color-gray-200);
  }

  .setting-label {
    font-size: 0.95rem;
    color: var(--color-gray-700);
  }

  .setting-value {
    font-size: 0.95rem;
    color: var(--color-gray-900);
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .point-text {
    font-weight: bold;
    font-size: 1rem;
    color: var(--color-gray-900);
  }

  .btn--link {
    background: none;
    border: none;
    color: var(--color-primary-500);
    cursor: pointer;
    font-size: 0.85rem;
    padding: 0;
  }

  .btn--link:hover {
    color: var(--color-primary-600);
    text-decoration: underline;
  }

  .text--danger {
    color: var(--color-error-600);
  }

  .clickable {
    cursor: pointer;
    text-decoration: underline;
  }
</style>
