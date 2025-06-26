<script setup>
  import { ref, watch, computed, nextTick, onMounted, onUnmounted } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseToggleSwitch from '@/components/common/BaseToggleSwitch.vue';

  const props = defineProps({
    modelValue: Boolean,
    item: Object,
  });

  const emit = defineEmits(['update:modelValue', 'save']);

  const show = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  const message = ref('');
  const enabled = ref(false);
  const sendTime = ref('immediate');

  const timeOptions = [
    { value: 'immediate', label: '차감 즉시' },
    { value: '1min', label: '1분 후' },
    { value: '5min', label: '5분 후' },
    { value: 'custom', label: '직접 설정' },
  ];

  const variableOptions = ['#{고객명}', '#{방문일}', '#{매장명}', '#{연락처}'];
  const showVariableDropdown = ref(false);

  // ✅ props.item 갱신 시 상태 초기화
  watch(
    () => props.item,
    val => {
      if (val) {
        message.value = val.message || '';
        enabled.value = val.enabled ?? true;
        sendTime.value = val.sendTime || 'immediate';
      }
    },
    { immediate: true }
  );

  // ✅ 변수 삽입
  function insertVariable(variable) {
    const textarea = document.getElementById('auto-message-textarea');
    const cursorPos = textarea?.selectionStart ?? message.value.length;
    const textBefore = message.value.substring(0, cursorPos);
    const textAfter = message.value.substring(cursorPos);
    message.value = textBefore + variable + textAfter;
    nextTick(() => {
      textarea?.focus();
      textarea?.setSelectionRange(cursorPos + variable.length, cursorPos + variable.length);
    });
  }

  // ✅ 저장 시 index 정보 포함 emit
  function handleSave() {
    emit('save', {
      parentIndex: props.item.parentIndex,
      messageIndex: props.item.messageIndex ?? null, // 신규일 경우 null
      message: message.value,
      enabled: enabled.value,
      sendTime: sendTime.value,
    });
    show.value = false;
  }

  // ✅ 외부 클릭 시 변수 드롭다운 닫기
  function handleClickOutside(e) {
    const wrapper = document.querySelector('.dropdown-wrapper');
    if (wrapper && !wrapper.contains(e.target)) {
      showVariableDropdown.value = false;
    }
  }

  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
  });

  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
  });
</script>

<template>
  <BaseDrawer v-model="show" title="자동 메시지 설정" size="md">
    <div class="drawer-body">
      <p class="drawer-label">{{ item.label }} 메시지를 수정하세요</p>

      <!-- 전송 시간 -->
      <label class="field-label">전송 시간</label>
      <select v-model="sendTime" class="select-box">
        <option v-for="option in timeOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>

      <!-- 자동발송 토글 -->
      <div class="flex justify-between items-center mt-4">
        <label class="field-label mb-0">자동발송 활성화</label>
        <div class="ml-2">
          <BaseToggleSwitch v-model="enabled" />
        </div>
      </div>

      <!-- 메시지 입력과 변수 삽입 -->
      <div class="mt-4">
        <div class="flex justify-between items-end">
          <label class="field-label mb-0">메시지 내용</label>
          <div class="dropdown-wrapper relative inline-block">
            <BaseButton
              size="xs"
              type="ghost"
              @click.stop="showVariableDropdown = !showVariableDropdown"
            >
              변수 삽입 ▼
            </BaseButton>

            <div
              v-if="showVariableDropdown"
              class="variable-dropdown-list absolute right-0 top-full mt-1"
              @click.stop
            >
              <ul class="dropdown-ul">
                <li
                  v-for="v in variableOptions"
                  :key="v"
                  class="variable-option"
                  @click="
                    insertVariable(v);
                    showVariableDropdown = false;
                  "
                >
                  {{ v }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <textarea
          id="auto-message-textarea"
          v-model="message"
          class="w-full p-2 border border-gray-300 rounded-md mt-2"
          rows="5"
          placeholder="메시지 내용을 입력하세요"
        />
      </div>

      <!-- 저장 버튼 -->
      <div class="mt-8 flex justify-end">
        <BaseButton type="primary" @click="handleSave">저장</BaseButton>
      </div>
    </div>
  </BaseDrawer>
</template>

<style scoped>
  .drawer-body {
    display: flex;
    flex-direction: column;
  }

  .drawer-label {
    font-size: 0.875rem;
    margin-bottom: 1.5rem;
    color: #6b7280;
  }

  .field-label {
    font-size: 0.875rem;
    font-weight: 600;
    color: #111827;
  }

  .select-box {
    width: 100%;
    padding: 0.5rem;
    font-size: 0.875rem;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    margin-bottom: 1.25rem;
    background-color: white;
  }

  textarea {
    font-size: 0.875rem;
    resize: vertical;
  }

  .dropdown-wrapper {
    position: relative;
    display: inline-block;
  }

  .variable-dropdown-list {
    position: absolute;
    z-index: 20;
    background-color: #ffffff;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    padding: 0.25rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    min-width: 120px;
  }

  .dropdown-ul {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .variable-option {
    padding: 0.375rem 0.75rem;
    font-size: 0.8125rem;
    cursor: pointer;
    white-space: nowrap;
    border-radius: 4px;
  }

  .variable-option:hover {
    background-color: #f3f4f6;
  }
</style>
