<script setup>
  import { computed } from 'vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import BaseButton from '@/components/common/BaseButton.vue';

  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true,
    },
    templates: {
      type: Array,
      required: true,
    },
  });

  const emit = defineEmits(['update:modelValue', 'select']);

  const drawerOpen = computed({
    get: () => props.modelValue,
    set: val => emit('update:modelValue', val),
  });

  function selectTemplate(template) {
    emit('select', template);
  }
</script>

<template>
  <BaseDrawer v-model="drawerOpen" title="템플릿 선택">
    <div class="template-list">
      <div v-for="template in templates" :key="template.id" class="template-item">
        <div class="template-header">
          <span class="template-name">{{ template.name }}</span>
        </div>
        <div class="template-content">{{ template.content }}</div>
        <div class="template-action">
          <BaseButton size="sm" type="primary" @click="selectTemplate(template)">
            가져오기
          </BaseButton>
        </div>
      </div>
    </div>
  </BaseDrawer>
</template>

<style scoped>
  .template-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  /* 템플릿 카드 */
  .template-item {
    padding: 16px;
    border: 1px solid var(--color-gray-200); /* 더 자연스러운 회색 */
    border-radius: 12px;
    background-color: var(--color-neutral-white);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04); /* subtle한 그림자 */
    transition: box-shadow 0.2s ease;
  }

  .template-item:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  /* 제목 */
  .template-header {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-neutral-dark);
    margin-bottom: 8px;
  }

  /* 본문 내용 */
  .template-content {
    font-size: 14px;
    color: var(--color-gray-700);
    margin-bottom: 12px;
  }

  /* 버튼 영역 */
  .template-action {
    text-align: right;
  }
</style>
