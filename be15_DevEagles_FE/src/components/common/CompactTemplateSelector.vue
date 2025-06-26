<template>
  <div class="compact-template-selector">
    <!-- 라벨과 버튼이 한 행에 배치 -->
    <div class="selector-row">
      <label class="selector-label">{{ label }}</label>
      <div class="selector-buttons">
        <BaseButton
          type="secondary"
          size="sm"
          outline
          :disabled="readonly"
          class="select-button"
          @click="openSelector"
        >
          <MessageCircleIcon :size="14" />
          {{ selectedTemplate ? '변경' : '선택' }}
        </BaseButton>
      </div>
    </div>

    <!-- 선택된 템플릿 표시 -->
    <div v-if="selectedTemplate" class="selected-display">
      <div class="selected-items">
        <div class="selected-chip">
          <div class="chip-content">
            <span class="chip-name">{{ selectedTemplate.name || selectedTemplate.text }}</span>
            <span class="chip-type">{{ getTemplateTypeText(selectedTemplate.type) }}</span>
          </div>
          <BaseButton
            v-if="!readonly"
            type="ghost"
            size="xs"
            class="chip-remove"
            @click="clearSelection"
          >
            <XIcon :size="12" />
          </BaseButton>
        </div>
      </div>
    </div>

    <!-- 빈 상태 표시 -->
    <div v-else class="empty-display">
      <span class="empty-text">{{ placeholder || '템플릿을 선택하세요' }}</span>
    </div>

    <!-- 템플릿 선택 모달 -->
    <BaseWindow
      v-model="showSelectorModal"
      title="메시지 템플릿 선택"
      width="700px"
      :min-height="'450px'"
    >
      <TemplateSelectorModal
        :selected-template="selectedTemplate"
        :filter-options="filterOptions"
        :exclude-ids="excludeIds"
        :available-coupons="availableCoupons"
        @select="handleTemplateSelect"
        @create="openCreateModal"
        @close="closeSelectorModal"
      />
    </BaseWindow>

    <!-- 템플릿 생성 모달 -->
    <BaseWindow
      v-model="showCreateModal"
      title="메시지 템플릿 생성"
      width="600px"
      :min-height="'580px'"
    >
      <TemplateForm @save="handleCreateTemplate" @cancel="closeCreateModal" />
    </BaseWindow>
  </div>
</template>

<script>
  import { ref, computed, watch } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseWindow from '@/components/common/BaseWindow.vue';
  import XIcon from '@/components/icons/XIcon.vue';
  import MessageCircleIcon from '@/components/icons/MessageCircleIcon.vue';
  import TemplateSelectorModal from './TemplateSelectorModal.vue';
  import TemplateForm from './TemplateForm.vue';

  export default {
    name: 'CompactTemplateSelector',
    components: {
      BaseButton,
      BaseWindow,
      XIcon,
      MessageCircleIcon,
      TemplateSelectorModal,
      TemplateForm,
    },
    props: {
      // 선택된 템플릿 (v-model)
      modelValue: {
        type: [Object, String],
        default: null,
      },
      // 라벨 텍스트
      label: {
        type: String,
        default: '메시지 템플릿',
      },
      // 플레이스홀더 텍스트
      placeholder: {
        type: String,
        default: '',
      },
      // 템플릿 생성 버튼 표시 여부
      allowCreate: {
        type: Boolean,
        default: true,
      },
      // 읽기 전용 모드
      readonly: {
        type: Boolean,
        default: false,
      },
      // 필터 옵션
      filterOptions: {
        type: Object,
        default: () => ({}),
      },
      // 제외할 템플릿 ID 목록
      excludeIds: {
        type: Array,
        default: () => [],
      },
      // 사용 가능한 쿠폰 목록
      availableCoupons: {
        type: Array,
        default: () => [],
      },
    },
    emits: ['update:modelValue', 'template-selected', 'template-created', 'selection-changed'],
    setup(props, { emit }) {
      // State
      const showSelectorModal = ref(false);
      const showCreateModal = ref(false);

      // Computed
      const selectedTemplate = computed({
        get: () => props.modelValue,
        set: value => {
          emit('update:modelValue', value);
          emit('selection-changed', value);
        },
      });

      // Watch for external changes
      watch(
        () => props.modelValue,
        newValue => {
          if (newValue !== selectedTemplate.value) {
            selectedTemplate.value = newValue;
          }
        },
        { immediate: true }
      );

      // Methods
      const openSelector = () => {
        if (!props.readonly) {
          showSelectorModal.value = true;
        }
      };

      const closeSelectorModal = () => {
        showSelectorModal.value = false;
      };

      const openCreateModal = () => {
        showCreateModal.value = true;
      };

      const closeCreateModal = () => {
        showCreateModal.value = false;
      };

      const handleTemplateSelect = template => {
        selectedTemplate.value = template;
        emit('template-selected', template);
        closeSelectorModal();
      };

      const handleCreateTemplate = templateData => {
        emit('template-created', templateData);
        closeCreateModal();
      };

      const clearSelection = () => {
        if (props.readonly) return;
        selectedTemplate.value = null;
      };

      const getTemplateTypeText = type => {
        const typeMap = {
          welcome: '환영',
          revisit: '재방문',
          coupon: '쿠폰',
          birthday: '생일',
          'follow-up': '사후관리',
          reminder: '리마인드',
          promotion: '프로모션',
        };
        return typeMap[type] || type;
      };

      return {
        // State
        showSelectorModal,
        showCreateModal,
        selectedTemplate,

        // Methods
        openSelector,
        closeSelectorModal,
        openCreateModal,
        closeCreateModal,
        handleTemplateSelect,
        handleCreateTemplate,
        clearSelection,
        getTemplateTypeText,
      };
    },
  };
</script>

<style scoped>
  .compact-template-selector {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  /* 라벨과 버튼 행 */
  .selector-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
  }

  .selector-label {
    font-size: 13px;
    font-weight: 600;
    color: var(--color-gray-700);
    flex-shrink: 0;
    min-width: fit-content;
  }

  .selector-buttons {
    display: flex;
    gap: 0.5rem;
    align-items: center;
    flex-shrink: 0;
  }

  .select-button {
    display: flex;
    align-items: center;
    gap: 0.375rem;
    min-width: 60px;
    justify-content: center;
  }

  .create-button {
    min-width: 32px;
    padding: 0.375rem;
    justify-content: center;
  }

  /* 선택된 템플릿 표시 영역 */
  .selected-display {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    padding: 0.75rem;
    background-color: var(--color-gray-50);
    border: 1px solid var(--color-gray-200);
    border-radius: 6px;
  }

  .selected-items {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    align-items: center;
  }

  .selected-chip {
    display: flex;
    align-items: center;
    gap: 0.375rem;
    padding: 0.375rem 0.5rem;
    background-color: white;
    border: 1px solid var(--color-primary-200);
    border-radius: 16px;
    font-size: 12px;
    color: var(--color-primary-700);
    max-width: 200px;
  }

  .chip-content {
    display: flex;
    align-items: center;
    gap: 0.25rem;
    overflow: hidden;
    flex: 1;
    min-width: 0;
  }

  .chip-name {
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .chip-type {
    font-weight: 600;
    color: var(--color-primary-600);
    flex-shrink: 0;
    font-size: 11px;
    padding: 0.125rem 0.25rem;
    background-color: var(--color-primary-50);
    border-radius: 8px;
  }

  .chip-remove {
    padding: 0.125rem;
    min-width: unset;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    color: var(--color-gray-500);
    background-color: transparent;
  }

  .chip-remove:hover {
    background-color: var(--color-error-50);
    color: var(--color-error-500);
  }

  /* 빈 상태 */
  .empty-display {
    padding: 0.5rem 0.75rem;
    border: 1px dashed var(--color-gray-300);
    border-radius: 6px;
    background-color: var(--color-gray-25);
  }

  .empty-text {
    font-size: 12px;
    color: var(--color-gray-500);
    font-style: italic;
  }

  /* 반응형 */
  @media (max-width: 768px) {
    .selector-row {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }

    .selector-buttons {
      width: 100%;
      justify-content: flex-end;
    }
  }

  /* 호버 효과 */
  .selected-chip:hover {
    border-color: var(--color-primary-300);
    background-color: var(--color-primary-25);
  }

  /* 포커스 상태 */
  .select-button:focus,
  .create-button:focus {
    outline: 2px solid var(--color-primary-200);
    outline-offset: 1px;
  }
</style>
