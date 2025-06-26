<script setup>
  import { computed, ref } from 'vue';
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

  const openedIds = ref(new Set());

  function toggleDetail(id) {
    if (openedIds.value.has(id)) {
      openedIds.value.delete(id);
    } else {
      openedIds.value.add(id);
    }
  }

  function isOpened(id) {
    return openedIds.value.has(id);
  }

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
          <button class="toggle-button" @click="toggleDetail(template.id)">
            {{ isOpened(template.id) ? '닫기' : '자세히 보기' }}
          </button>
        </div>

        <div class="template-content">
          {{ template.content }}
        </div>

        <div v-if="isOpened(template.id)" class="template-detail">
          <div
            v-if="
              template.link || template.couponId || template.grades?.length || template.tags?.length
            "
          >
            <div v-if="template.link" class="detail-item">링크: {{ template.link }}</div>
            <div v-if="template.couponId" class="detail-item">쿠폰 ID: {{ template.couponId }}</div>
            <div v-if="template.grades?.length" class="detail-item">
              등급: {{ template.grades.join(', ') }}
            </div>
            <div v-if="template.tags?.length" class="detail-item">
              태그: {{ template.tags.join(', ') }}
            </div>
          </div>
          <div v-else class="detail-empty">등록된 상세 정보가 없습니다.</div>
        </div>

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

  .template-item {
    padding: 16px;
    border: 1px solid var(--color-gray-200);
    border-radius: 12px;
    background-color: var(--color-neutral-white);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
    transition: box-shadow 0.2s ease;
  }

  .template-item:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .template-header {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-neutral-dark);
    margin-bottom: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .template-name {
    flex: 1;
  }

  .toggle-button {
    font-size: 13px;
    color: var(--color-primary);
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px 8px;
  }

  .template-content {
    font-size: 14px;
    color: var(--color-gray-700);
    margin-bottom: 12px;
  }

  .template-detail {
    background-color: var(--color-gray-50);
    border-radius: 8px;
    padding: 12px;
    margin-top: 0;
    margin-bottom: 12px;
    font-size: 13px;
    color: var(--color-gray-700);
    line-height: 1.6;
  }

  .detail-item + .detail-item {
    margin-top: 6px;
  }

  .detail-empty {
    font-size: 13px;
    color: var(--color-gray-500);
  }

  .template-action {
    text-align: right;
  }
</style>
