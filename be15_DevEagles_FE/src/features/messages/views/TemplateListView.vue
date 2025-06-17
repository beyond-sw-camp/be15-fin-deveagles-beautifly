<script setup>
  import { ref, computed } from 'vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BasePagination from '@/components/common/Pagaination.vue';
  import TemplateItem from '@/features/messages/components/TemplateItem.vue';
  import { PlusIcon } from 'lucide-vue-next';

  const allTemplates = ref([
    { id: 1, name: '예약 안내', content: '고객님 예약이 확정되었습니다.', createdAt: '2024-06-10' },
    {
      id: 2,
      name: '시술 전 안내',
      content: '시술 전 주의사항을 확인해주세요.',
      createdAt: '2024-06-11',
    },
    {
      id: 3,
      name: '리뷰 요청',
      content: '시술이 만족스러우셨다면 리뷰를 남겨주세요.',
      createdAt: '2024-06-13',
    },
  ]);

  const currentPage = ref(1);
  const itemsPerPage = 10;
  const showCreateModal = ref(false);

  const totalPages = computed(() => Math.ceil(allTemplates.value.length / itemsPerPage));

  const paginatedTemplates = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return allTemplates.value.slice(start, start + itemsPerPage);
  });

  const columns = [
    { key: 'name', title: '템플릿명', width: '20%', headerClass: 'text-center' },
    { key: 'content', title: '내용', width: '50%', headerClass: 'text-center' },
    { key: 'createdAt', title: '등록일자', width: '20%', headerClass: 'text-center' },
    { key: 'actions', title: '관리', width: '10%', headerClass: 'text-center' },
  ];

  function onPageChange(page) {
    currentPage.value = page;
  }

  function handleCreate(newTemplate) {
    allTemplates.value.unshift(newTemplate);
    showCreateModal.value = false;
  }
</script>

<template>
  <div class="template-list-view">
    <div class="template-list-header">
      <h2 class="font-section-title text-dark">문자 템플릿 목록</h2>
      <BaseButton type="primary" size="sm" @click="showCreateModal = true">
        <PlusIcon class="icon" /> 템플릿 등록
      </BaseButton>
    </div>

    <BaseTable :columns="columns" :data="paginatedTemplates">
      <template #body>
        <TemplateItem
          v-for="template in paginatedTemplates"
          :key="template.id"
          :template="template"
        />
      </template>
    </BaseTable>

    <BasePagination
      v-if="totalPages > 1"
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="allTemplates.length"
      :items-per-page="itemsPerPage"
      @page-change="onPageChange"
    />
  </div>
</template>

<style scoped>
  .template-list-view {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }
  .template-list-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 1.5rem;
  }
  th {
    text-align: center !important;
  }
</style>

<style>
  .text-center {
    text-align: center !important;
  }
</style>
