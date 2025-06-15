/// TemplateListView.vue
<script setup>
  import { ref, computed } from 'vue';
  import TemplateFilter from '@/features/messages/components/TemplateFilter.vue';
  import TemplateItem from '@/features/messages/components/TemplateItem.vue';

  const filter = ref({ keyword: '' });
  const showRegisterModal = ref(false);

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

  const filteredTemplates = computed(() => {
    const keyword = filter.value.keyword.trim();
    if (!keyword) return allTemplates.value;
    return allTemplates.value.filter(t => t.name.includes(keyword) || t.content.includes(keyword));
  });

  function onFilterSubmit(payload) {
    filter.value = payload;
  }
</script>

<template>
  <section class="template-list-view">
    <!-- ✅ 제목 + 등록 버튼 -->
    <div class="header-row">
      <h2 class="page-title">문자 템플릿 목록</h2>
    </div>

    <!-- ✅ 필터 + 버튼 가로 정렬 -->
    <div class="filter-input-row">
      <TemplateFilter @submit-filter="onFilterSubmit" />

      <button class="btn btn--primary btn--sm icon-btn" @click="showRegisterModal = true">
        + 템플릿 등록
      </button>
    </div>

    <!-- ✅ 테이블 영역 -->
    <div class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th class="text-center">템플릿명</th>
            <th class="text-center">내용</th>
            <th class="text-center">등록일</th>
            <th class="text-center">관리</th>
          </tr>
        </thead>
        <tbody>
          <template v-if="filteredTemplates.length > 0">
            <TemplateItem v-for="item in filteredTemplates" :key="item.id" :item="item" />
          </template>
          <tr v-else>
            <td colspan="4" class="text--gray text-center">템플릿이 없습니다</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
  .template-list-view {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    padding: 1.5rem;
    background-color: var(--color-gray-50);
  }

  .page-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--color-neutral-dark);
    margin: 0;
  }

  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
  }

  .filter-input-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
  }

  .table-wrapper {
    overflow-x: auto;
    background: white;
    border-radius: 0.5rem;
  }

  .table {
    width: 100%;
    border-collapse: collapse;
  }

  .table th,
  .table td {
    padding: 0.75rem;
    text-align: center;
    border-bottom: 1px solid var(--color-gray-200);
  }

  .table th {
    background-color: var(--color-gray-100);
    font-weight: 600;
    font-size: 0.875rem;
    color: var(--color-gray-800);
  }

  .icon-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.5rem 1rem;
    border-radius: 0.375rem;
    background-color: var(--color-primary-400);
    color: white;
    font-weight: 500;
    font-size: 0.875rem;
    white-space: nowrap;
  }

  .text-center {
    text-align: center;
  }
</style>
