<script setup>
  import { ref, watch, computed, nextTick, onMounted, onBeforeUnmount } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
    templateContent: {
      type: Object,
      default: () => ({ content: '', link: '', coupon: null, grades: [], tags: [] }),
    },
    customers: {
      type: Array,
      default: () => [],
    },
  });

  const emit = defineEmits([
    'update:modelValue',
    'request-send',
    'request-reserve',
    'open-template',
    'open-customer',
  ]);

  const messageContent = ref('');
  const selectedGrade = ref([]);
  const selectedTags = ref([]);
  const availableCoupons = ref([
    { id: 1, name: '신규 고객 10% 할인' },
    { id: 2, name: '시술 5천원 할인' },
    { id: 3, name: '후기 작성 시 무료 서비스' },
  ]);
  const grades = ['전체', 'VIP', '일반'];
  const tags = ['여드름', '탈모', '기미', '스킨부스터'];

  const linkUrl = ref('');
  const selectedCoupon = ref(null);

  const showVariableDropdown = ref(false);
  const textareaWrapper = ref(null);
  const variableButtonRef = ref(null);

  const variables = [
    '#{고객명}',
    '#{잔여선불충전액}',
    '#{잔여포인트}',
    '#{상점명}',
    '#{인스타url}',
  ];

  watch(
    () => props.templateContent,
    newVal => {
      if (!newVal) return;
      try {
        const parsed = JSON.parse(newVal);
        messageContent.value = parsed.content || '';
      } catch {
        messageContent.value = typeof newVal === 'string' ? newVal : '';
      }
    }
  );

  function insertVariable(variable) {
    if (variable === '#{인스타url}') {
      linkUrl.value = variable;
      return;
    }

    const textarea = textareaWrapper.value?.querySelector('textarea');
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const before = messageContent.value.slice(0, start);
    const after = messageContent.value.slice(end);

    messageContent.value = before + variable + after;

    nextTick(() => {
      textarea.focus();
      textarea.selectionStart = textarea.selectionEnd = start + variable.length;
    });
  }

  function handleClickOutside(e) {
    const btn = variableButtonRef.value?.$el ?? null;
    const dropdown = document.querySelector('.variable-insert-wrap .dropdown');

    if (
      showVariableDropdown.value &&
      btn &&
      !btn.contains(e.target) &&
      !dropdown?.contains(e.target)
    ) {
      showVariableDropdown.value = false;
    }
  }

  onMounted(() => {
    window.addEventListener('click', handleClickOutside);
  });
  onBeforeUnmount(() => {
    window.removeEventListener('click', handleClickOutside);
  });

  const visibleCustomers = computed(() => (props.customers || []).slice(0, 5));
  const hiddenCustomerCount = computed(() => Math.max((props.customers?.length || 0) - 5, 0));

  function toggleGrade(grade) {
    if (grade === '전체') {
      selectedGrade.value = selectedGrade.value.includes('전체') ? [] : ['전체'];
    } else {
      if (selectedGrade.value.includes('전체')) {
        selectedGrade.value = [grade];
      } else if (selectedGrade.value.includes(grade)) {
        selectedGrade.value = selectedGrade.value.filter(g => g !== grade);
      } else {
        selectedGrade.value.push(grade);
      }
    }
  }

  function close() {
    emit('update:modelValue', false);
    messageContent.value = '';
    selectedGrade.value = [];
    selectedTags.value = [];
    linkUrl.value = '';
    selectedCoupon.value = null;
    showVariableDropdown.value = false;
  }

  function confirmSend() {
    if (!messageContent.value.trim()) return;
    emit('request-send', {
      content: messageContent.value,
      link: linkUrl.value || null,
      coupon: selectedCoupon.value,
      grades: selectedGrade.value,
      tags: selectedTags.value,
      customers: props.customers,
    });
    close();
  }

  function reserveSend() {
    if (!messageContent.value.trim()) return;
    emit('request-reserve', {
      content: messageContent.value,
      link: linkUrl.value || null,
      coupon: selectedCoupon.value,
      grades: selectedGrade.value,
      tags: selectedTags.value,
      customers: props.customers,
    });
    close();
  }
</script>

<template>
  <div class="modal-wrapper modal-z-index">
    <BaseModal
      :model-value="props.modelValue"
      title="새 메시지"
      @update:model-value="val => emit('update:modelValue', val)"
    >
      <div class="form-group">
        <!-- 템플릿 가져오기 -->
        <BaseButton class="template-button" size="sm" @click="emit('open-template')">
          + 템플릿 가져오기
        </BaseButton>

        <!-- 메시지 제목 + 버튼 -->
        <div class="label-with-button">
          <label class="form-label">메시지 내용</label>
          <BaseButton
            ref="variableButtonRef"
            size="xs"
            type="ghost"
            @click.stop="showVariableDropdown = !showVariableDropdown"
          >
            변수 삽입 ▼
          </BaseButton>
        </div>

        <div class="textarea-variable-wrapper">
          <div ref="textareaWrapper" class="textarea-container">
            <textarea
              v-model="messageContent"
              class="input input--md w-full"
              rows="5"
              placeholder="메시지 내용을 입력하세요"
            />
          </div>
          <div class="variable-insert-wrap">
            <div v-if="showVariableDropdown" class="dropdown">
              <div v-for="v in variables" :key="v" class="insert-item" @click="insertVariable(v)">
                {{ v }}
              </div>
            </div>
          </div>
        </div>

        <!-- 링크 -->
        <BaseButton class="mt-3" size="sm" type="ghost" @click="linkUrl = ''"
          >링크 초기화</BaseButton
        >
        <input v-model="linkUrl" class="input input--md mt-1" placeholder="https://..." />

        <!-- 쿠폰 -->
        <label class="form-label mt-4">쿠폰 선택</label>
        <select v-model="selectedCoupon" class="input input--md">
          <option disabled value="">쿠폰을 선택하세요</option>
          <option v-for="coupon in availableCoupons" :key="coupon.id" :value="coupon">
            {{ coupon.name }}
          </option>
        </select>

        <!-- 등급 -->
        <div class="option-section mt-4">
          <label class="form-label">보낼 대상 등급</label>
          <div class="toggle-buttons">
            <BaseButton
              v-for="grade in grades"
              :key="grade"
              :type="selectedGrade.includes(grade) ? 'primary' : 'ghost'"
              size="sm"
              @click="toggleGrade(grade)"
            >
              {{ grade }}
            </BaseButton>
          </div>
        </div>

        <!-- 태그 -->
        <div class="option-section mt-2">
          <label class="form-label">고객 태그</label>
          <div class="toggle-buttons">
            <BaseButton
              v-for="tag in tags"
              :key="tag"
              :type="selectedTags.includes(tag) ? 'primary' : 'ghost'"
              size="sm"
              @click="
                selectedTags.includes(tag)
                  ? (selectedTags = selectedTags.filter(t => t !== tag))
                  : selectedTags.push(tag)
              "
            >
              {{ tag }}
            </BaseButton>
          </div>
        </div>

        <!-- 고객 -->
        <div class="option-section mt-4">
          <BaseButton type="primary" size="sm" @click="emit('open-customer')">
            + 고객 선택
          </BaseButton>
          <div v-if="props.customers?.length" class="selected-chip-wrap">
            <span v-for="(customer, idx) in visibleCustomers" :key="idx" class="customer-chip">
              {{ customer.name }}
            </span>
            <span v-if="hiddenCustomerCount > 0" class="customer-chip more-chip">
              +{{ hiddenCustomerCount }} 더 보기
            </span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="footer-buttons">
          <BaseButton type="primary" @click="confirmSend">보내기</BaseButton>
          <BaseButton type="secondary" @click="reserveSend">예약 보내기</BaseButton>
          <BaseButton type="error" @click="close">취소</BaseButton>
        </div>
      </template>
    </BaseModal>
  </div>
</template>

<style scoped>
  .insert-item {
    @apply py-1 px-2 text-sm hover:bg-gray-100 rounded cursor-pointer;
  }
  .selected-chip-wrap {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 6px;
  }
  .customer-chip {
    background-color: var(--color-gray-100);
    color: var(--color-gray-800);
    font-size: 13px;
    padding: 4px 8px;
    border-radius: 12px;
  }
  .more-chip {
    background-color: var(--color-primary-50);
    color: var(--color-primary-600);
  }
  .footer-buttons {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-top: 12px;
  }
  .template-button {
    margin-bottom: 16px;
  }
  .label-with-button {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }
  .textarea-variable-wrapper {
    display: flex;
    gap: 8px;
    position: relative;
  }
  .textarea-container {
    flex: 1;
  }
  .variable-insert-wrap {
    position: relative;
  }
  .variable-insert-wrap .dropdown {
    position: absolute;
    right: 0;
    background-color: white;
    border: 1px solid var(--color-gray-200);
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    padding: 6px;
    width: 160px;
    z-index: 10;
  }
</style>
