<script setup>
  import { ref, watch, computed } from 'vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: Boolean,
    templateContent: String,
    customers: Array,
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

  const grades = ['전체', 'VIP', '일반'];
  const tags = ['여드름', '탈모', '기미', '스킨부스터'];

  const linkAttached = ref(false);
  const editingLink = ref(false);
  const linkUrl = ref('');
  const confirmedLink = ref('');

  const selectedCoupon = ref(null);
  const availableCoupons = [
    { id: 1, name: '신규 고객 10% 할인' },
    { id: 2, name: '시술 5천원 할인' },
  ];

  watch(
    () => props.templateContent,
    newVal => {
      if (!newVal) return;

      try {
        const parsed = JSON.parse(newVal);
        // ✅ content만 뽑아서 넣기
        messageContent.value = parsed.content || '';
      } catch {
        // 템플릿에서 그냥 문자열이 들어온 경우
        messageContent.value = typeof newVal === 'string' ? newVal : '';
      }
    }
  );

  const summaryCustomers = computed(() => {
    const customers = props.customers || [];
    const names = customers.map(c => c.name);
    if (names.length <= 3) return `${names.join(', ')} 선택됨`;
    return `${names.slice(0, 3).join(', ')} 외 ${names.length - 3}명 선택됨`;
  });

  const visibleCustomers = computed(() => {
    return (props.customers || []).slice(0, 5);
  });
  const hiddenCustomerCount = computed(() => {
    const total = props.customers?.length || 0;
    return total > 5 ? total - 5 : 0;
  });

  function close() {
    emit('update:modelValue', false);
    messageContent.value = '';
    selectedGrade.value = [];
    selectedTags.value = [];
    linkAttached.value = false;
    editingLink.value = false;
    linkUrl.value = '';
    confirmedLink.value = '';
    selectedCoupon.value = null;
  }

  function toggleLinkInput() {
    if (linkAttached.value) {
      linkAttached.value = false;
      linkUrl.value = '';
      confirmedLink.value = '';
      editingLink.value = false;
    } else {
      editingLink.value = true;
    }
  }

  function confirmLink() {
    if (!linkUrl.value.trim()) return;
    confirmedLink.value = linkUrl.value;
    linkAttached.value = true;
    editingLink.value = false;
  }

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

  function confirmSend() {
    if (!messageContent.value.trim()) return;
    emit('request-send', {
      content: messageContent.value,
      link: linkAttached.value ? confirmedLink.value : null,
      coupon: selectedCoupon.value,
      grades: selectedGrade.value,
      tags: selectedTags.value,
    });
    close();
  }

  function reserveSend() {
    if (!messageContent.value.trim()) return;
    emit('request-reserve', {
      content: messageContent.value,
      link: linkAttached.value ? confirmedLink.value : null,
      coupon: selectedCoupon.value,
      grades: selectedGrade.value,
      tags: selectedTags.value,
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

        <!-- 등급 선택 -->
        <div class="option-section">
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

        <!-- 태그 선택 -->
        <div class="option-section">
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

        <!-- 링크 첨부 -->
        <div class="option-section">
          <label class="form-label">링크 첨부</label>
          <div class="toggle-buttons">
            <BaseButton
              :type="linkAttached ? 'primary' : 'ghost'"
              size="sm"
              @click="toggleLinkInput"
            >
              {{ linkAttached ? '첨부됨' : '첨부 안 함' }}
            </BaseButton>
          </div>

          <div v-if="editingLink" class="mt-2">
            <input
              v-model="linkUrl"
              type="text"
              class="input input--md"
              placeholder="https://example.com"
            />
            <BaseButton class="mt-1" type="primary" size="sm" @click="confirmLink">
              링크 등록
            </BaseButton>
          </div>
        </div>

        <!-- 쿠폰 선택 -->
        <div class="option-section">
          <label class="form-label">쿠폰 등록</label>
          <select v-model="selectedCoupon" class="input input--md">
            <option disabled value="">쿠폰을 선택하세요</option>
            <option v-for="coupon in availableCoupons" :key="coupon.id" :value="coupon">
              {{ coupon.name }}
            </option>
          </select>
        </div>

        <!-- 메시지 내용 -->
        <label class="form-label mt-4">메시지 내용</label>
        <textarea
          v-model="messageContent"
          class="input input--md"
          rows="5"
          placeholder="메시지 내용을 입력하세요"
        />

        <!-- 고객 선택 -->
        <div class="option-section">
          <div class="customer-select-group">
            <BaseButton
              class="select-customer-btn"
              type="primary"
              size="sm"
              @click="emit('open-customer')"
            >
              + 고객 선택
            </BaseButton>
            <span v-if="props.customers?.length" class="selected-count">
              {{ summaryCustomers }}
            </span>
          </div>

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
          <BaseButton class="footer-btn" type="primary" @click="confirmSend">보내기</BaseButton>
          <BaseButton class="footer-btn" type="secondary" @click="reserveSend"
            >예약 보내기</BaseButton
          >
          <BaseButton class="footer-btn" type="error" @click="close">취소</BaseButton>
        </div>
      </template>
    </BaseModal>
  </div>
</template>

<style scoped>
  .modal-wrapper.modal-z-index {
    position: relative;
    z-index: 1100;
  }
  .form-group {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .template-button {
    align-self: flex-start;
  }
  .option-section {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  .toggle-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  .customer-select-group {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .select-customer-btn .icon {
    margin-right: 4px;
    font-size: 14px;
  }
  .selected-count {
    font-size: 14px;
    color: var(--color-primary-600);
  }
  .selected-chip-wrap {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 6px;
    padding-left: 2px;
    max-height: 56px;
    overflow: hidden;
  }
  .customer-chip {
    background-color: var(--color-gray-100);
    color: var(--color-gray-800);
    font-size: 13px;
    padding: 4px 8px;
    border-radius: 12px;
    line-height: 1.2;
    white-space: nowrap;
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
    flex-wrap: wrap;
  }
  .footer-btn {
    min-width: 120px;
    padding: 0.5rem 1rem;
    text-align: center;
    white-space: nowrap;
  }
</style>
