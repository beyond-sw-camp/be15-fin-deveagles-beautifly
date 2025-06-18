<template>
  <div v-if="totalPages > 1" class="pagination-wrapper">
    <div class="pagination-container">
      <!-- First Page Button -->
      <button class="pagination-btn" :disabled="currentPage === 1" @click="goToPage(1)">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path
            d="M11 17L6 12L11 7M18 17L13 12L18 7"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>

      <!-- Previous Button -->
      <button
        class="pagination-btn"
        :disabled="currentPage === 1"
        @click="goToPage(currentPage - 1)"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path
            d="M15 18L9 12L15 6"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>

      <!-- Page Numbers -->
      <div class="page-numbers">
        <button
          v-for="page in displayPages"
          :key="page"
          class="page-btn"
          :class="{ active: currentPage === page }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
      </div>

      <!-- Next Button -->
      <button
        class="pagination-btn"
        :disabled="currentPage === totalPages"
        @click="goToPage(currentPage + 1)"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path
            d="M9 18L15 12L9 6"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>

      <!-- Last Page Button -->
      <button
        class="pagination-btn"
        :disabled="currentPage === totalPages"
        @click="goToPage(totalPages)"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path
            d="M13 17L18 12L13 7M6 17L11 12L6 7"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </div>

    <!-- Page Info -->
    <div class="pagination-info">
      <span class="font-small text-gray-500">
        {{ startItem }}-{{ endItem }} of {{ totalItems }} items
      </span>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'BasePagination',
    props: {
      currentPage: {
        type: Number,
        default: 1,
      },
      totalPages: {
        type: Number,
        required: true,
      },
      totalItems: {
        type: Number,
        required: true,
      },
      itemsPerPage: {
        type: Number,
        default: 10,
      },
    },
    emits: ['page-change'],
    computed: {
      startItem() {
        return (this.currentPage - 1) * this.itemsPerPage + 1;
      },
      endItem() {
        return Math.min(this.currentPage * this.itemsPerPage, this.totalItems);
      },
      displayPages() {
        const pages = [];
        const maxVisible = 5;

        if (this.totalPages <= maxVisible) {
          // 전체 페이지가 적으면 모두 표시
          for (let i = 1; i <= this.totalPages; i++) {
            pages.push(i);
          }
        } else {
          // 현재 페이지 중심으로 페이지 계산
          const half = Math.floor(maxVisible / 2);
          let start = Math.max(this.currentPage - half, 1);
          let end = Math.min(start + maxVisible - 1, this.totalPages);

          // 끝에서 시작점 조정
          if (end === this.totalPages) {
            start = Math.max(end - maxVisible + 1, 1);
          }

          for (let i = start; i <= end; i++) {
            pages.push(i);
          }
        }

        return pages;
      },
    },
    methods: {
      goToPage(page) {
        if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
          this.$emit('page-change', page);
        }
      },
    },
  };
</script>

<style scoped>
  .pagination-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    margin: 24px 0;
  }

  .pagination-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }

  .pagination {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .pagination-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    border: 1px solid var(--color-gray-200);
    background: var(--color-neutral-white);
    color: var(--color-gray-700);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 14px;
    font-weight: 500;
  }

  .pagination-btn:hover:not(:disabled) {
    background: var(--color-gray-50);
    border-color: var(--color-gray-300);
  }

  .pagination-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    background: var(--color-gray-100);
  }

  .pagination-btn svg {
    flex-shrink: 0;
  }

  .page-numbers {
    display: flex;
    align-items: center;
    gap: 4px;
    margin: 0 8px;
  }

  .page-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: 1px solid var(--color-gray-200);
    background: var(--color-neutral-white);
    color: var(--color-gray-700);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 14px;
    font-weight: 500;
  }

  .page-btn:hover {
    background: var(--color-gray-50);
    border-color: var(--color-gray-300);
  }

  .page-btn.active {
    background: var(--color-primary-main);
    border-color: var(--color-primary-main);
    color: var(--color-neutral-white);
  }

  .page-btn.active:hover {
    background: var(--color-primary-600);
    border-color: var(--color-primary-600);
  }

  .pagination-info {
    text-align: center;
  }

  @media (max-width: 768px) {
    .pagination-container {
      gap: 8px;
    }

    .pagination-btn {
      padding: 6px 8px;
      font-size: 13px;
    }

    .page-btn {
      width: 32px;
      height: 32px;
      font-size: 13px;
    }

    .page-numbers {
      margin: 0 4px;
    }

    .pagination-info {
      font-size: 12px;
    }
  }
</style>
