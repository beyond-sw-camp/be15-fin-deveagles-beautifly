<template>
  <div class="template-selector-modal">
    <!-- 검색 및 생성 -->
    <div class="search-section">
      <input
        v-model="searchText"
        type="text"
        placeholder="템플릿명으로 검색..."
        class="search-input"
      />
      <BaseButton type="primary" outline @click="handleCreate">
        <PlusIcon :size="16" />
        새 템플릿 생성
      </BaseButton>
    </div>

    <!-- 템플릿 목록 -->
    <div class="template-list">
      <div
        v-for="template in filteredTemplates"
        :key="template.value"
        class="template-item"
        :class="{ selected: isSelected(template) }"
        @click="selectTemplate(template)"
      >
        <div class="template-content">
          <div class="template-header">
            <div class="template-name">{{ template.text }}</div>
            <div class="template-type-badge">{{ getTypeText(template.type) }}</div>
          </div>
          <div class="template-description">
            {{ template.content || '템플릿 내용이 없습니다.' }}
          </div>
        </div>
      </div>
    </div>

    <!-- 템플릿 생성 모달 -->
    <TemplateCreateModal v-model="showCreateModal" @submit="handleTemplateCreated" />
  </div>
</template>

<script>
  import { ref, computed, onMounted, onUnmounted } from 'vue';
  import api from '@/plugins/axios';
  import { useAuthStore } from '@/store/auth.js';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PlusIcon from '@/components/icons/PlusIcon.vue';
  import TemplateCreateModal from '@/features/messages/components/modal/TemplateCreateModal.vue';

  export default {
    name: 'TemplateSelectorModal',
    components: {
      BaseButton,
      PlusIcon,
      TemplateCreateModal,
    },
    props: {
      selectedTemplate: {
        type: [Object, String],
        default: null,
      },
      availableCoupons: {
        type: Array,
        default: () => [],
      },
    },
    emits: ['select', 'create', 'close'],
    setup(props, { emit }) {
      const searchText = ref('');
      const selectedTemplateLocal = ref(props.selectedTemplate);
      const showCreateModal = ref(false);

      const templates = ref([]);

      const loadTemplates = async () => {
        try {
          const authStore = useAuthStore();
          const params = { size: 100, page: 0 }; // 큰 페이지로 전체 로드
          const res = await api.get('/message/templates', { params });
          const arr = res.data?.data?.content ?? res.data?.data ?? [];
          templates.value = arr.map(t => ({
            value: t.templateId,
            text: t.templateName,
            content: t.templateContent,
            type: (t.templateType || '').toLowerCase(),
            data: t,
          }));
        } catch (error) {
          console.error('메시지 템플릿 불러오기 실패', error);
        }
      };

      const filteredTemplates = computed(() => {
        if (!searchText.value) return templates.value;

        return templates.value.filter(template =>
          template.text.toLowerCase().includes(searchText.value.toLowerCase())
        );
      });

      const isSelected = template => {
        if (!selectedTemplateLocal.value) return false;
        return selectedTemplateLocal.value.value === template.value;
      };

      const selectTemplate = template => {
        selectedTemplateLocal.value = template;
        handleConfirm();
      };

      const getTypeText = type => {
        const typeMap = {
          advertising: '광고',
          announcement: '안내',
          etc: '기타',
        };
        return typeMap[type] || '일반';
      };

      const handleConfirm = () => {
        emit('select', selectedTemplateLocal.value);
      };

      const handleCreate = () => {
        showCreateModal.value = true;
      };

      const handleTemplateCreated = templateData => {
        // 새 템플릿을 목록에 추가
        const newTemplate = {
          value: `custom-${Date.now()}`,
          text: templateData.name,
          content: templateData.content,
          type: 'custom',
          data: templateData,
        };

        templates.value.unshift(newTemplate);

        // 생성된 템플릿을 자동으로 선택
        selectedTemplateLocal.value = newTemplate;
        showCreateModal.value = false;

        // 선택 완료
        handleConfirm();
      };

      const handleClose = () => {
        emit('close');
      };

      const handleKeyDown = event => {
        if (event.key === 'Escape') {
          handleClose();
        }
      };

      onMounted(() => {
        document.addEventListener('keydown', handleKeyDown);
        loadTemplates();
      });

      onUnmounted(() => {
        document.removeEventListener('keydown', handleKeyDown);
      });

      return {
        searchText,
        selectedTemplateLocal,
        showCreateModal,
        filteredTemplates,
        isSelected,
        selectTemplate,
        getTypeText,
        handleConfirm,
        handleCreate,
        handleTemplateCreated,
        handleClose,
      };
    },
  };
</script>

<style scoped>
  .template-selector-modal {
    display: flex;
    flex-direction: column;
    height: 100%;
    gap: 0.75rem;
  }

  .search-section {
    display: flex;
    gap: 0.75rem;
    align-items: center;
    padding-bottom: 0.75rem;
    border-bottom: 1px solid var(--color-gray-200);
  }

  .search-input {
    flex: 1;
    height: 38px;
    padding: 0.375rem 0.75rem;
    border: 1px solid var(--color-gray-300);
    border-radius: 6px;
    font-size: 14px;
    line-height: 1.5;
    background-color: white;
    box-sizing: border-box;
    transition:
      border-color 0.15s ease-in-out,
      box-shadow 0.15s ease-in-out;
  }

  .search-input:focus {
    outline: none;
    border-color: var(--color-primary-500);
    box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
  }

  .search-input::placeholder {
    color: var(--color-gray-500);
  }

  .template-list {
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    max-height: 350px;
  }

  .template-item {
    padding: 0.75rem;
    border: 1px solid var(--color-gray-200);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .template-item:hover {
    border-color: var(--color-primary-300);
    background-color: var(--color-primary-50);
  }

  .template-item.selected {
    border-color: var(--color-primary-500);
    background-color: var(--color-primary-50);
  }

  .template-content {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .template-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
  }

  .template-name {
    font-weight: 600;
    color: var(--color-gray-900);
    font-size: 15px;
    line-height: 1.3;
  }

  .template-type-badge {
    font-size: 11px;
    font-weight: 600;
    color: var(--color-primary-600);
    background-color: var(--color-primary-50);
    padding: 0.125rem 0.375rem;
    border-radius: 12px;
    flex-shrink: 0;
  }

  .template-description {
    font-size: 12px;
    color: var(--color-gray-600);
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    max-height: 2.8em;
  }
</style>
