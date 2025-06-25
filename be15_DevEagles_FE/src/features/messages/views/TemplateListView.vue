<template>
  <div class="template-list-view">
    <!-- 상단 헤더 -->
    <div class="template-list-header">
      <h2 class="font-section-title text-dark">템플릿 보관함</h2>
      <BaseButton type="primary" size="sm" @click="openCreateModal">
        <PlusIcon class="icon" /> 템플릿 등록
      </BaseButton>
    </div>
    <BaseCard>
      <BaseTable :columns="columns" :data="paginatedTemplates">
        <template #body>
          <TemplateItem
            v-for="template in paginatedTemplates"
            :key="template.id"
            :template="template"
            :column-widths="columnWidths"
            @edit="openEditModal"
            @delete="openDeleteModal"
            @open-detail="openDetailModal"
          />
        </template>
      </BaseTable>
    </BaseCard>

    <!-- 페이지네이션 -->
    <Pagination
      v-if="totalPages > 1"
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="allTemplates.length"
      :items-per-page="itemsPerPage"
      @page-change="onPageChange"
    />

    <!-- 모달들 -->
    <TemplateCreateModal v-model="showCreateModal" @submit="handleCreate" />
    <TemplateEditModal v-model="showEditModal" :template="editTarget" @submit="handleEdit" />
    <TemplateDeleteModal v-model="showDeleteModal" @confirm="confirmDelete" />
    <TemplateDetailModal v-model="showDetailModal" :template="detailTarget" />

    <!-- 토스트 -->
    <BaseToast ref="toast" />
  </div>
</template>

<script setup>
  import { ref, computed } from 'vue';
  import { defineAsyncComponent } from 'vue';

  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  import TemplateItem from '@/features/messages/components/TemplateItem.vue';
  import { PlusIcon } from 'lucide-vue-next';
  import BaseCard from '@/components/common/BaseCard.vue';

  const TemplateCreateModal = defineAsyncComponent(
    () => import('@/features/messages/components/modal/TemplateCreateModal.vue')
  );
  const TemplateEditModal = defineAsyncComponent(
    () => import('@/features/messages/components/modal/TemplateEditModal.vue')
  );
  const TemplateDeleteModal = defineAsyncComponent(
    () => import('@/features/messages/components/modal/TemplateDeleteModal.vue')
  );
  const TemplateDetailModal = defineAsyncComponent(
    () => import('@/features/messages/components/modal/TemplateDetailModal.vue')
  );

  // 데이터
  const allTemplates = ref([
    {
      id: 1,
      name: '예약 안내',
      content: '고객님 예약이 확정되었습니다.',
      createdAt: '2024-06-10',
    },
    {
      id: 2,
      name: '시술 전 안내',
      content:
        '시술 전 주의사항을 꼭 읽어보시고 방문해 주세요. 시술 당일에는 세안과 화장품 사용을 피해주시기 바랍니다. 피부 상태에 따라 간단한 테스트 후 시술이 진행될 수 있으며, 시술 후에는 자외선 차단제를 꼭 발라주세요. 또한 2~3일간은 사우나, 음주, 무리한 운동은 삼가주시고, 시술 부위를 자극하지 않도록 주의해 주세요. 감사합니다.',
      createdAt: '2024-06-11',
    },
    {
      id: 3,
      name: '리뷰 요청',
      content: '시술이 만족스러우셨다면 리뷰를 남겨주세요.',
      createdAt: '2024-06-13',
    },
  ]);

  // 페이지네이션
  const currentPage = ref(1);
  const itemsPerPage = 10;
  const totalPages = computed(() => Math.ceil(allTemplates.value.length / itemsPerPage));
  const paginatedTemplates = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return allTemplates.value.slice(start, start + itemsPerPage);
  });
  function onPageChange(page) {
    currentPage.value = page;
  }

  // 컬럼 정의
  const columns = [
    { key: 'name', title: '템플릿명', width: '25%', headerClass: 'text-center' },
    { key: 'content', title: '내용', width: '45%', headerClass: 'text-center' },
    { key: 'createdAt', title: '등록일자', width: '20%', headerClass: 'text-center' },
    { key: 'actions', title: '관리', width: '10%', headerClass: 'text-center' },
  ];
  const columnWidths = {
    name: '25%',
    content: '45%',
    createdAt: '20%',
    actions: '10%',
  };

  // 모달 상태
  const showCreateModal = ref(false);
  const showEditModal = ref(false);
  const showDeleteModal = ref(false);
  const showDetailModal = ref(false);

  const editTarget = ref({ id: null, name: '', content: '', createdAt: '' });
  const deleteTarget = ref(null);
  const detailTarget = ref(null);

  const toast = ref(null);

  // 모달 제어 함수
  function openCreateModal() {
    showCreateModal.value = true;
  }
  function openEditModal(template) {
    editTarget.value = { ...template };
    showEditModal.value = true;
  }
  function openDeleteModal(template) {
    deleteTarget.value = template;
    showDeleteModal.value = true;
  }
  function openDetailModal(template) {
    detailTarget.value = template;
    showDetailModal.value = true;
  }

  // CRUD
  function handleCreate(newTemplate) {
    allTemplates.value.unshift(newTemplate);
    toast.value?.success('템플릿이 등록되었습니다.', { type: 'success' });
    showCreateModal.value = false;
  }
  function handleEdit(updatedTemplate) {
    const index = allTemplates.value.findIndex(t => t.id === updatedTemplate.id);
    if (index !== -1) {
      allTemplates.value[index] = { ...updatedTemplate };
      toast.value?.success('템플릿이 수정되었습니다.', { type: 'success' });
    }
    showEditModal.value = false;
  }
  function confirmDelete() {
    if (!deleteTarget.value) return;
    toast.value?.success('템플릿이 삭제되었습니다.', { type: 'success' });
    showDeleteModal.value = false;
  }
</script>

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
</style>
