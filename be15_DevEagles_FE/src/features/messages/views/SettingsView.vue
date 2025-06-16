<template>
  <section class="settings-page">
    <BaseCard class="card-wrapper shadow-drop">
      <div class="setting-header">
        <h2 class="page-title">문자 설정</h2>
        <p class="page-sub">서비스 이용에 필요한 기본 정보를 설정하세요.</p>
      </div>

      <div class="setting-group">
        <!-- 발신 번호 -->
        <div class="setting-item">
          <div class="setting-status-row">
            <h3 class="setting-title with-underline">발신 번호</h3>
            <div class="status-controls fixed-width">
              <span class="status-chip error" :class="{ error: !sender }">
                {{ sender || '미등록' }}
              </span>
              <BaseButton class="action-button sm" type="primary" @click="showSenderModal = true">
                번호 등록
              </BaseButton>
            </div>
          </div>
          <div class="setting-info">
            <p class="setting-description text-sub">
              문자 서비스를 이용하기 위해 발신 번호를 입력해주세요.
            </p>
            <p class="setting-description text-sub">
              문자 또는 알림톡 발신 시 사용될 기본 번호입니다.
            </p>
          </div>
        </div>

        <hr class="section-divider" />

        <!-- 알림톡 신청 -->
        <div class="setting-item">
          <div class="setting-status-row">
            <h3 class="setting-title with-underline">알림톡 신청</h3>
            <div class="status-controls fixed-width">
              <span class="status-chip" :class="useAlimtalk ? 'success' : 'error'">
                {{ useAlimtalk ? '신청 완료' : '미신청' }}
              </span>
              <BaseButton
                class="action-button sm"
                type="primary"
                :disabled="useAlimtalk"
                @click="showAlimtalkModal = true"
              >
                알림톡 신청
              </BaseButton>
            </div>
          </div>
          <div class="setting-info">
            <p class="setting-description text-sub">
              알림톡 발송을 위해 카카오 발신 프로필이 필요합니다.
            </p>
          </div>
        </div>

        <hr class="section-divider" />

        <!-- 문자 포인트 -->
        <div class="setting-item no-border">
          <div class="setting-status-row">
            <h3 class="setting-title with-underline">문자 포인트</h3>
            <div class="status-controls fixed-width">
              <span class="status-chip neutral"> {{ messagePoints.toLocaleString() }}P </span>
              <BaseButton class="action-button sm" type="primary" @click="showChargeModal = true">
                포인트 충전
              </BaseButton>
            </div>
          </div>
          <div class="setting-info">
            <p class="setting-description text-sub">
              문자 또는 알림톡 발송 시 포인트가 차감됩니다.
            </p>
          </div>
        </div>
      </div>
    </BaseCard>

    <!-- 모달들 -->
    <SenderInfoModal v-model="showSenderModal" @confirm="confirmSender" />

    <AlimtalkConfirmModal
      v-model="showAlimtalkModal"
      @confirm="confirmAlimtalk"
      @close="showAlimtalkModal = false"
    />

    <PointChargeModal
      v-model="showChargeModal"
      @confirm="handleConfirmCharge"
      @close="showChargeModal = false"
    />

    <BaseToast ref="toastRef" />
  </section>
</template>

<script setup>
  import { ref } from 'vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import SenderInfoModal from '@/features/messages/components/modal/SenderInfoModal.vue';
  import AlimtalkConfirmModal from '@/features/messages/components/modal/AlimtalkConfirmModal.vue';
  import PointChargeModal from '@/features/messages/components/modal/PointChargeModal.vue';

  const sender = ref('');
  const useAlimtalk = ref(false);
  const messagePoints = ref(1200);

  const showSenderModal = ref(false);
  const showAlimtalkModal = ref(false);
  const showChargeModal = ref(false);

  const toastRef = ref();

  function confirmSender(number) {
    sender.value = number;
    toastRef.value?.addToast({
      message: '발신 번호가 등록되었습니다.',
      type: 'success',
    });
    showSenderModal.value = false;
  }

  function confirmAlimtalk() {
    useAlimtalk.value = true;
    toastRef.value?.addToast({
      message: '알림톡 신청이 완료되었습니다.',
      type: 'success',
    });
    showAlimtalkModal.value = false;
  }

  function handleConfirmCharge(amount) {
    messagePoints.value += amount;
    toastRef.value?.addToast({
      message: `${amount.toLocaleString()}P 충전되었습니다.`,
      type: 'success',
    });
    showChargeModal.value = false;
  }
</script>

<style scoped>
  @import '@/assets/base.css';
  @import '@/assets/css/components.css';
  @import '@/assets/css/styleguide.css';
  @import '@/assets/css/tooltip.css';

  .settings-page {
    padding: 2.5rem 0;
    display: flex;
    justify-content: center;
    background-color: #f9fafb;
  }

  .card-wrapper {
    width: 100%;
    max-width: 600px;
    padding: 2.5rem 2rem;
    border-radius: 16px;
    background-color: #ffffff;
  }

  .setting-header {
    margin-bottom: 2rem;
    text-align: center;
  }

  .page-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #111827;
  }

  .page-sub {
    font-size: 0.875rem;
    color: #6b7280;
    margin-top: 0.25rem;
  }

  .setting-item {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .setting-item.no-border {
    padding-bottom: 0;
  }

  .setting-status-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: nowrap;
    gap: 1rem;
  }

  .setting-title.with-underline {
    font-weight: 600;
    font-size: 1rem;
    color: #111827;
    padding-bottom: 0.25rem;
    border-bottom: 1px solid #e5e7eb;
    flex-shrink: 0;
  }

  .status-controls {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .status-controls.fixed-width {
    min-width: 200px;
    justify-content: flex-end;
  }

  .status-chip {
    display: inline-block;
    padding: 4px 10px;
    font-size: 12px;
    border-radius: 9999px;
    font-weight: 500;
  }

  .status-chip.success {
    background-color: #dcfce7;
    color: #166534;
  }

  .status-chip.error {
    background-color: #fee2e2;
    color: #b91c1c;
  }

  .status-chip.neutral {
    background-color: #f3f4f6;
    color: #374151;
  }

  .action-button.sm {
    min-width: auto;
    padding: 6px 12px;
    font-size: 13px;
    height: auto;
    line-height: 1.2;
  }

  .section-divider {
    border: none;
    border-top: 1px solid #e5e7eb;
    margin: 2rem 0 0.5rem;
  }
</style>
