<script setup>
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import { computed } from 'vue';

  const props = defineProps({
    modelValue: Boolean,
    message: {
      type: Object,
      required: false,
      default: null,
    },
  });
  const emit = defineEmits(['update:modelValue']);

  const statusText = computed(() => {
    if (!props.message) return '-';
    return props.message.status === 'sent' ? '발송 완료' : '예약 문자';
  });
</script>

<template>
  <BaseModal
    :model-value="modelValue"
    title="메시지 상세 보기"
    @update:model-value="val => emit('update:modelValue', val)"
  >
    <div v-if="message" class="detail-container">
      <div class="detail-group">
        <div class="group-title">기본 정보</div>
        <div class="group-body">
          <div class="info-row">
            <span class="label">제목</span>
            <span class="value">{{ message.title }}</span>
          </div>
          <div class="info-row">
            <span class="label">수신자</span>
            <span class="value">{{ message.receiver }}</span>
          </div>
          <div class="info-row">
            <span class="label">상태</span>
            <span class="value">{{ statusText }}</span>
          </div>
          <div class="info-row">
            <span class="label">날짜</span>
            <span class="value">{{ message.date }}</span>
          </div>
        </div>
      </div>

      <div class="detail-group">
        <div class="group-title">내용</div>
        <div class="group-body">
          <pre class="content-box">{{ message.content }}</pre>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">표시할 메시지 정보가 없습니다.</div>

    <template #footer>
      <BaseButton type="primary" class="w-full" @click="emit('update:modelValue', false)">
        닫기
      </BaseButton>
    </template>
  </BaseModal>
</template>

<style scoped>
  .detail-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
    padding-top: 12px;
  }

  .detail-group {
    background: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 12px;
    padding: 20px;
  }

  .group-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--color-gray-800);
    margin-bottom: 16px;
    border-bottom: 1px dashed var(--color-gray-200);
    padding-bottom: 6px;
  }

  .group-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
  }

  .label {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-gray-500);
  }

  .value {
    font-size: 14px;
    font-weight: 400;
    color: var(--color-gray-900);
  }

  .content-box {
    font-size: 14px;
    line-height: 1.6;
    color: var(--color-gray-800);
    background: var(--color-gray-50);
    padding: 16px;
    border-radius: 8px;
    border: 1px solid var(--color-gray-200);
    white-space: pre-wrap;
    word-break: break-word;
  }

  .empty-state {
    text-align: center;
    color: var(--color-gray-500);
    font-size: 14px;
    padding: 32px 0;
  }
</style>
