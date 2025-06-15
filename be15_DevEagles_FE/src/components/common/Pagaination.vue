<template>
  <div v-if="totalPages > 1" class="pagination-container">
    <div class="pagination-info">Total {{ totalItems }} items</div>
    <ul class="pagination">
      <!-- Previous Button -->
      <li class="pagination-item">
        <button
          class="pagination-link pagination-prev"
          :disabled="currentPage === 1"
          @click="goToPage(currentPage - 1)"
        >
          &lt;
        </button>
      </li>

      <!-- First Page -->
      <li v-if="showFirstPage" class="pagination-item">
        <button class="pagination-link" :class="{ active: currentPage === 1 }" @click="goToPage(1)">
          1
        </button>
      </li>

      <!-- First Ellipsis -->
      <li v-if="showFirstEllipsis" class="pagination-item">
        <span class="pagination-ellipsis">...</span>
      </li>

      <!-- Page Numbers -->
      <li v-for="page in visiblePages" :key="page" class="pagination-item">
        <button
          class="pagination-link"
          :class="{ active: currentPage === page }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
      </li>

      <!-- Last Ellipsis -->
      <li v-if="showLastEllipsis" class="pagination-item">
        <span class="pagination-ellipsis">...</span>
      </li>

      <!-- Last Page -->
      <li v-if="showLastPage" class="pagination-item">
        <button
          class="pagination-link"
          :class="{ active: currentPage === totalPages }"
          @click="goToPage(totalPages)"
        >
          {{ totalPages }}
        </button>
      </li>

      <!-- Next Button -->
      <li class="pagination-item">
        <button
          class="pagination-link pagination-next"
          :disabled="currentPage === totalPages"
          @click="goToPage(currentPage + 1)"
        >
          &gt;
        </button>
      </li>
    </ul>

    <!-- Items Per Page -->
    <div class="pagination-per-page">
      <select v-model="itemsPerPageModel" class="input" @change="changeItemsPerPage">
        <option value="10">10 / page</option>
        <option value="20">20 / page</option>
        <option value="50">50 / page</option>
        <option value="100">100 / page</option>
      </select>
    </div>

    <!-- Go to Page -->
    <div class="pagination-goto">
      <span>Go to</span>
      <input
        v-model="gotoPage"
        type="number"
        class="input"
        min="1"
        :max="totalPages"
        style="width: 60px; margin-left: 8px"
        @keyup.enter="goToSpecificPage"
      />
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
      maxVisiblePages: {
        type: Number,
        default: 5,
      },
    },
    emits: ['page-change', 'items-per-page-change'],
    data() {
      return {
        itemsPerPageModel: this.itemsPerPage,
        gotoPage: this.currentPage,
      };
    },
    computed: {
      visiblePages() {
        const pages = [];
        const start = Math.max(2, this.currentPage - Math.floor(this.maxVisiblePages / 2));
        const end = Math.min(this.totalPages - 1, start + this.maxVisiblePages - 1);

        for (let i = start; i <= end; i++) {
          if (i !== 1 && i !== this.totalPages) {
            pages.push(i);
          }
        }
        return pages;
      },
      showFirstPage() {
        return this.totalPages > 1;
      },
      showLastPage() {
        return this.totalPages > 1 && this.totalPages !== 1;
      },
      showFirstEllipsis() {
        return this.visiblePages.length > 0 && this.visiblePages[0] > 2;
      },
      showLastEllipsis() {
        return (
          this.visiblePages.length > 0 &&
          this.visiblePages[this.visiblePages.length - 1] < this.totalPages - 1
        );
      },
    },
    watch: {
      currentPage(newVal) {
        this.gotoPage = newVal;
      },
      itemsPerPage(newVal) {
        this.itemsPerPageModel = newVal;
      },
    },
    methods: {
      goToPage(page) {
        if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
          this.$emit('page-change', page);
        }
      },
      changeItemsPerPage() {
        this.$emit('items-per-page-change', parseInt(this.itemsPerPageModel));
      },
      goToSpecificPage() {
        const page = parseInt(this.gotoPage);
        if (page >= 1 && page <= this.totalPages) {
          this.goToPage(page);
        } else {
          this.gotoPage = this.currentPage;
        }
      },
    },
  };
</script>

<style scoped>
  .pagination-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 1.5rem 0;
    flex-wrap: wrap;
    gap: 1rem;
  }

  .pagination-info {
    font-size: 14px;
    color: var(--color-gray-600);
  }

  .pagination-per-page select {
    width: auto;
    min-width: 100px;
  }

  .pagination-goto {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 14px;
    color: var(--color-gray-600);
  }

  .pagination-link:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .pagination-link:disabled:hover {
    background-color: transparent;
  }

  @media (max-width: 768px) {
    .pagination-container {
      flex-direction: column;
      align-items: stretch;
    }

    .pagination {
      justify-content: center;
    }

    .pagination-info,
    .pagination-per-page,
    .pagination-goto {
      text-align: center;
    }
  }
</style>
