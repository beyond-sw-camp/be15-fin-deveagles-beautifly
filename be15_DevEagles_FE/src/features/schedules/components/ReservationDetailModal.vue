<!-- 📄 src/components/schedule/ReservationDetailModal.vue -->
<template>
  <div v-if="modelValue" class="overlay">
    <div class="modal-panel">
      <div class="modal-header">
        <div>
          <h1>등록된 스케줄</h1>
          <p class="type-label">예약</p>
        </div>
        <button class="close-btn" @click="close">×</button>
      </div>

      <div class="modal-body">
        <div class="left-detail">
          <div class="row">
            <label>고객명</label>
            <span v-if="!isEditMode">{{ reservation.customer }}</span>
            <input v-else v-model="edited.customer" />
          </div>
          <div class="row">
            <label>연락처</label>
            <span v-if="!isEditMode">{{ reservation.phone }}</span>
            <input v-else v-model="edited.phone" />
          </div>
          <div class="row">
            <label>예약일</label>
            <span v-if="!isEditMode">{{ reservation.start }} ~ {{ reservation.end }}</span>
            <div v-else style="display: flex; gap: 8px">
              <input v-model="edited.start" /><input v-model="edited.end" />
            </div>
          </div>
          <div class="row">
            <label>시술</label>
            <span v-if="!isEditMode">{{ reservation.service }}</span>
            <input v-else v-model="edited.service" />
          </div>
          <div class="row row-select">
            <label>담당자</label>
            <div class="form-control-wrapper">
              <BaseForm
                v-if="isEditMode"
                v-model="edited.staff"
                type="select"
                :options="[
                  { text: '디자이너 A', value: '디자이너 A' },
                  { text: '디자이너 B', value: '디자이너 B' },
                ]"
                placeholder="담당자 선택"
              />
              <span v-else>{{ reservation.staff }}</span>
            </div>
          </div>
          <div class="row row-select">
            <label>예약 상태</label>
            <div class="form-control-wrapper">
              <BaseForm
                v-if="isEditMode"
                v-model="edited.status"
                type="select"
                :options="statusOptions"
                placeholder="예약 상태 선택"
              />
              <span v-else>{{ reservation.status }}</span>
            </div>
          </div>
          <div class="row">
            <label>특이사항</label>
            <span v-if="!isEditMode">{{ reservation.note }}</span>
            <textarea v-else v-model="edited.note" />
          </div>
          <div class="row">
            <label>고객 메모</label>
            <span v-if="!isEditMode">{{ reservation.memo }}</span>
            <textarea v-else v-model="edited.memo" />
          </div>
        </div>

        <div class="right-box">
          <p>고객정보 확인</p>
          <p>매출 등록</p>
        </div>
      </div>

      <div class="modal-footer">
        <BaseButton type="error" @click="close">닫기</BaseButton>
        <template v-if="isEditMode">
          <BaseButton type="primary" @click="saveEdit">저장</BaseButton>
        </template>
        <template v-else>
          <div class="action-dropdown">
            <BaseButton type="primary" @click="toggleMenu">수정 / 삭제</BaseButton>
            <ul v-if="showMenu" class="dropdown-menu">
              <li @click="handleEdit">수정하기</li>
              <li @click="handleDelete">삭제하기</li>
            </ul>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, defineProps, defineEmits, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseForm from '@/components/common/BaseForm.vue';

  const props = defineProps({
    modelValue: { type: Boolean, required: true },
    reservation: { type: Object, default: () => ({}) },
  });
  const emit = defineEmits(['update:modelValue']);

  const showMenu = ref(false);
  const isEditMode = ref(false);
  const edited = ref({});

  watch(
    () => props.modelValue,
    newVal => {
      if (newVal) {
        isEditMode.value = false;
        showMenu.value = false;
        edited.value = {};
      }
    }
  );

  const close = () => {
    emit('update:modelValue', false);
    isEditMode.value = false;
    showMenu.value = false;
    edited.value = {};
  };

  const statusOptions = [
    { text: '노쇼', value: '노쇼' },
    { text: '가게에 의한 취소', value: '가게에 의한 취소' },
    { text: '고객에 의한 취소', value: '고객에 의한 취소' },
    { text: '예약 대기', value: '예약 대기' },
    { text: '예약 확정', value: '예약 확정' },
  ];

  const toggleMenu = () => {
    showMenu.value = !showMenu.value;
  };

  const handleEdit = () => {
    isEditMode.value = true;
    showMenu.value = false;
    edited.value = { ...props.reservation };
  };

  const handleDelete = () => {
    showMenu.value = false;
    if (confirm('정말 삭제하시겠습니까?')) {
      alert('삭제 요청 전송');
    }
  };

  const saveEdit = () => {
    alert('수정 내용 저장 요청:\n' + JSON.stringify(edited.value, null, 2));
    isEditMode.value = false;
  };
</script>

<style scoped>
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: rgba(0, 0, 0, 0.3);
    z-index: 1000;
  }

  .modal-panel {
    position: fixed;
    top: 0;
    left: 240px;
    width: calc(100% - 240px);
    height: 100vh;
    background: var(--color-neutral-white);
    display: flex;
    flex-direction: column;
    padding: 24px;
    overflow-y: auto;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .modal-header h1 {
    font-size: 20px;
    font-weight: bold;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
  }

  .modal-body {
    display: flex;
    gap: 32px;
    flex: 1;
  }

  .left-detail {
    flex: 1;
  }

  .row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 14px;
  }

  .row label {
    width: 100px;
    font-weight: bold;
    color: var(--color-gray-800);
    padding-top: 6px;
    line-height: 1.5;
  }

  .row span,
  .row input,
  .row textarea {
    font-size: 14px;
    line-height: 1.5;
    padding: 6px 8px;
    vertical-align: middle;
    width: 100%;
    max-width: 400px;
    box-sizing: border-box;
  }

  .row input,
  .row textarea {
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
  }

  .row textarea {
    resize: vertical;
  }

  .form-control-wrapper {
    flex: 1;
    display: flex;
    align-items: flex-start;
  }

  .form-control-wrapper :deep(.input) {
    width: 100%;
    max-width: 300px;
  }

  .right-box {
    width: 200px;
    padding: 12px;
    border-left: 1px solid var(--color-gray-200);
  }

  .right-box p {
    margin-bottom: 16px;
    font-weight: 500;
    color: var(--color-gray-700);
  }

  .modal-footer {
    margin-top: 32px;
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }

  .action-dropdown {
    position: relative;
  }

  .dropdown-menu {
    position: absolute;
    bottom: 40px;
    right: 0;
    background: var(--color-neutral-white);
    border: 1px solid var(--color-gray-300);
    border-radius: 6px;
    list-style: none;
    padding: 8px 0;
    width: 120px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
    z-index: 10;
  }

  .dropdown-menu li {
    padding: 8px 12px;
    cursor: pointer;
    color: var(--color-gray-800);
  }

  .dropdown-menu li:hover {
    background: var(--color-gray-100);
  }

  .type-label {
    margin-top: 4px;
    font-size: 18px;
    font-weight: 500;
    color: var(--color-gray-500);
  }

  .date-inline {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
  }

  .date-inline input[type='date'],
  .date-inline input[type='text'],
  .date-inline select {
    font-size: 14px;
    padding: 6px 8px;
    border: 1px solid var(--color-gray-300);
    border-radius: 4px;
    background-color: var(--color-neutral-white);
    color: var(--color-gray-900);
    min-width: 120px;
    height: 32px;
  }

  .repeat-inline :deep(.input) {
    display: inline-block;
    width: auto;
    min-width: 160px;
  }

  .date-inline span {
    white-space: nowrap;
    width: auto !important;
    max-width: none;
    color: var(--color-gray-800);
  }

  .repeat-inline span {
    white-space: nowrap;
    width: auto !important;
    max-width: none;
  }
</style>
