<script setup>
  import { ref, computed } from 'vue';
  import MessageFilter from '../components/MessageFilter.vue';
  import MessageItem from '../components/MessageItem.vue';
  import { MessageCircleIcon } from '@/components/icons/index.js';

  // 원본 메시지 (불변)
  const allMessages = ref([
    { id: 1, type: 'SMS', content: '예약이 완료되었습니다.', status: 'sent', date: '2024-06-10' },
    {
      id: 2,
      type: '알림톡',
      content: '시술 전 확인 사항을 꼭 읽어주세요.',
      status: 'reserved',
      date: '2024-06-12',
    },
    {
      id: 3,
      type: 'SMS',
      content: '고객님, 소중한 리뷰를 남겨주세요.',
      status: 'sent',
      date: '2024-06-09',
    },
    {
      id: 4,
      type: '알림톡',
      content: '금일 이벤트 안내드립니다.',
      status: 'reserved',
      date: '2024-06-11',
    },
    {
      id: 5,
      type: 'SMS',
      content: '시술 완료되었습니다. 감사합니다!',
      status: 'sent',
      date: '2024-06-10',
    },
  ]);

  // 필터 조건 (성능 위해 ref 사용)
  const filter = ref({ status: 'all' });

  // 페이지 상태
  const page = ref(1);
  const pageSize = 20;

  // 필터링된 메시지 목록 (슬라이싱 X)
  const filtered = computed(() => {
    if (filter.value.status === 'all') return allMessages.value;
    return allMessages.value.filter(msg => msg.status === filter.value.status);
  });

  // 실제 렌더링 대상 메시지
  const pagedMessages = computed(() => {
    const start = (page.value - 1) * pageSize;
    return filtered.value.slice(start, start + pageSize);
  });

  const totalPages = computed(() => Math.ceil(filtered.value.length / pageSize));

  function onFilter(newFilter) {
    // 내부 프로퍼티만 갱신해야 반응성 유지됨
    filter.value.status = newFilter.status;
    page.value = 1;
  }

  function prevPage() {
    if (page.value > 1) page.value--;
  }

  function nextPage() {
    if (page.value < totalPages.value) page.value++;
  }
</script>

<template>
  <section class="message-list-view">
    <!-- ✅ 페이지 제목 -->
    <h2 class="page-title">문자 내역</h2>

    <!-- 필터 영역 -->
    <div class="filter-row-wrapper">
      <MessageFilter @update:filter="onFilter" />
      <button class="btn btn-primary btn-sm icon-btn" title="문자 보내기">
        <MessageCircleIcon :size="16" />
      </button>
    </div>

    <!-- 메시지 목록 -->
    <div class="message-list">
      <MessageItem v-for="message in pagedMessages" :key="message.id" :message="message" />
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination-area">
      <button class="pagination-btn" :disabled="page === 1" @click="prevPage">이전</button>
      <span class="page-indicator">{{ page }} / {{ totalPages }}</span>
      <button class="pagination-btn" :disabled="page === totalPages" @click="nextPage">다음</button>
    </div>
  </section>
</template>

<style scoped>
  .filter-row-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    margin-bottom: 1rem;
  }

  .message-list-view {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    padding: 1.5rem;
    background-color: var(--color-gray-50);
  }

  /* ✅ 페이지 제목 스타일 */
  .page-title {
    font-size: 18px;
    font-weight: 700;
    line-height: 23.4px;
    color: var(--color-neutral-dark);
    margin-bottom: 1rem;
  }

  .message-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .pagination-area {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-top: 1rem;
  }

  .pagination-btn {
    padding: 0.5rem 1rem;
    background-color: var(--color-primary-400);
    color: white;
    border-radius: 0.5rem;
    font-weight: 500;
    font-size: 0.875rem;
  }

  .page-indicator {
    font-size: 0.875rem;
    color: var(--color-gray-700);
  }
</style>
