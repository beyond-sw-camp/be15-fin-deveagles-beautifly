<template>
  <div class="coupon-list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">쿠폰 목록</h1>
      <BaseButton type="primary" @click="openCreateModal"> + 새 쿠폰 생성 </BaseButton>
    </div>

    <!-- Coupon Table -->
    <BaseCard>
      <BaseTable :columns="tableColumns" :data="coupons" :loading="loading" hover>
        <!-- Coupon Name Column -->
        <template #cell-name="{ item }">
          <div class="coupon-name">
            {{ item.name }}
          </div>
        </template>

        <!-- Product Column -->
        <template #cell-product="{ item }">
          <div class="coupon-product">
            {{ item.product }}
          </div>
        </template>

        <!-- Designer Column -->
        <template #cell-designer="{ item }">
          <div class="coupon-designer">
            {{ item.designer }}
          </div>
        </template>

        <!-- Discount Column -->
        <template #cell-discount="{ item }">
          <BaseBadge type="success"> {{ item.discount }}% </BaseBadge>
        </template>

        <!-- Active Status Column -->
        <template #cell-isActive="{ item }">
          <label class="toggle-switch">
            <input v-model="item.isActive" type="checkbox" @change="toggleCouponStatus(item)" />
            <span class="slider"></span>
          </label>
        </template>

        <!-- Actions Column -->
        <template #cell-actions="{ item }">
          <div class="action-buttons">
            <BaseButton
              :ref="`deleteBtn-${item.id}`"
              type="error"
              size="sm"
              @click="deleteCoupon(item, $event)"
            >
              삭제
            </BaseButton>
          </div>
        </template>

        <!-- Empty State -->
        <template #empty>
          <div class="empty-state">
            <p class="text-gray-500">등록된 쿠폰이 없습니다.</p>
            <BaseButton type="primary" @click="openCreateModal"> 첫 번째 쿠폰 생성하기 </BaseButton>
          </div>
        </template>
      </BaseTable>
    </BaseCard>

    <!-- Pagination -->
    <BasePagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="totalItems"
      :items-per-page="itemsPerPage"
      @page-change="handlePageChange"
      @items-per-page-change="handleItemsPerPageChange"
    />

    <!-- Create Modal -->
    <BaseModal v-model="showModal" title="쿠폰 생성">
      <CouponForm @save="handleSaveCoupon" @cancel="closeModal" />
    </BaseModal>

    <!-- Delete Confirm Popover -->
    <BasePopover
      v-model="showDeleteConfirm"
      title="쿠폰 삭제"
      :message="`'${selectedCouponForDelete?.name}' 쿠폰을 정말 삭제하시겠습니까?`"
      confirm-text="삭제"
      cancel-text="취소"
      confirm-type="error"
      placement="top"
      size="sm"
      :trigger-element="triggerElement"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />

    <!-- Toast Notification -->
    <BaseToast ref="toast" />
  </div>
</template>

<script>
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import BasePagination from '@/components/common/Pagaination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import CouponForm from '../components/CouponForm.vue';

  export default {
    name: 'CouponList',
    components: {
      BaseButton,
      BaseModal,
      BasePopover,
      BasePagination,
      BaseCard,
      BaseTable,
      BaseBadge,
      BaseToast,
      CouponForm,
    },
    data() {
      return {
        // 쿠폰 데이터 (목업 데이터)
        coupons: [
          {
            id: 1,
            name: '열린 시즌 할인 쿠폰',
            product: '열린',
            designer: '이순신',
            discount: 20,
            isActive: true,
            category: '1차',
            secondaryProduct: '',
            expiryDate: '2024-12-31',
          },
          {
            id: 2,
            name: '신규 고객 환영 쿠폰',
            product: '닫힌',
            designer: '김철수',
            discount: 15,
            isActive: true,
            category: '2차',
            secondaryProduct: '옵션1',
            expiryDate: '2024-11-30',
          },
          {
            id: 3,
            name: '여름 시즌 특가 쿠폰',
            product: '반열린',
            designer: '박영희',
            discount: 25,
            isActive: false,
            category: '1차',
            secondaryProduct: '옵션2',
            expiryDate: '2024-08-31',
          },
          {
            id: 4,
            name: 'VIP 고객 전용 쿠폰',
            product: '열린',
            designer: '최민수',
            discount: 30,
            isActive: true,
            category: '3차',
            secondaryProduct: '옵션3',
            expiryDate: '2024-12-15',
          },
          {
            id: 5,
            name: '연말 감사 쿠폰',
            product: '닫힌',
            designer: '이순신',
            discount: 10,
            isActive: true,
            category: '2차',
            secondaryProduct: '',
            expiryDate: '2024-12-31',
          },
        ],

        // 페이지네이션
        currentPage: 1,
        itemsPerPage: 10,
        totalItems: 85,
        totalPages: 9,

        // 모달 관련
        showModal: false,
        showDeleteConfirm: false,
        selectedCouponForDelete: null,
        triggerElement: null,

        // 로딩 상태
        loading: false,

        // 테이블 컬럼 정의
        tableColumns: [
          {
            key: 'name',
            title: '쿠폰명',
          },
          {
            key: 'product',
            title: '상품',
          },
          {
            key: 'designer',
            title: '디자이너',
          },
          {
            key: 'discount',
            title: '할인율',
          },
          {
            key: 'isActive',
            title: '활성화',
          },
          {
            key: 'actions',
            title: 'Actions',
            width: '80px',
          },
        ],
      };
    },

    methods: {
      // 모달 관련
      openCreateModal() {
        this.showModal = true;
      },

      closeModal() {
        this.showModal = false;
      },

      // 쿠폰 관리
      handleSaveCoupon(couponData) {
        // 새로 생성
        const newCoupon = {
          ...couponData,
          id: Date.now(), // 실제로는 서버에서 받아올 ID
        };
        this.coupons.unshift(newCoupon);
        this.showToastMessage('쿠폰이 생성되었습니다.', 'success');
        this.closeModal();
      },

      deleteCoupon(coupon, event) {
        this.selectedCouponForDelete = coupon;
        this.triggerElement = event.target.closest('button');
        this.showDeleteConfirm = true;
      },

      confirmDelete() {
        if (this.selectedCouponForDelete) {
          const index = this.coupons.findIndex(c => c.id === this.selectedCouponForDelete.id);
          if (index !== -1) {
            this.coupons.splice(index, 1);
            this.showToastMessage('쿠폰이 삭제되었습니다.', 'success');
          }
        }
        this.cancelDelete();
      },

      cancelDelete() {
        this.selectedCouponForDelete = null;
        this.triggerElement = null;
        this.showDeleteConfirm = false;
      },

      toggleCouponStatus(coupon) {
        const status = coupon.isActive ? '활성화' : '비활성화';
        this.showToastMessage(`쿠폰이 ${status}되었습니다.`, 'success');
      },

      // 페이지네이션
      handlePageChange(page) {
        this.currentPage = page;
        // 실제로는 API 호출
      },

      handleItemsPerPageChange(itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        this.currentPage = 1;
        // 실제로는 API 호출
      },

      // 토스트 알림
      showToastMessage(message, type = 'success') {
        this.$refs.toast[type](message);
      },
    },
  };
</script>

<style scoped>
  .coupon-list-container {
    padding: 2rem;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
  }

  .coupon-name {
    font-weight: 600;
    color: var(--color-neutral-dark);
  }

  .coupon-product,
  .coupon-designer {
    font-size: 14px;
    color: var(--color-gray-600);
  }

  .action-buttons {
    display: flex;
    gap: 8px;
  }

  .empty-state {
    text-align: center;
    padding: 3rem;
  }

  .empty-state p {
    margin-bottom: 1rem;
  }

  /* Toggle Switch */
  .toggle-switch {
    position: relative;
    display: inline-block;
    width: 44px;
    height: 24px;
  }

  .toggle-switch input {
    opacity: 0;
    width: 0;
    height: 0;
  }

  .slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--color-gray-300);
    transition: 0.3s;
    border-radius: 24px;
  }

  .slider:before {
    position: absolute;
    content: '';
    height: 18px;
    width: 18px;
    left: 3px;
    bottom: 3px;
    background-color: white;
    transition: 0.3s;
    border-radius: 50%;
  }

  input:checked + .slider {
    background-color: var(--color-success-500);
  }

  input:checked + .slider:before {
    transform: translateX(20px);
  }

  /* Toast Notification */
  .toast-notification {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    padding: 1rem 1.5rem;
    border-radius: 0.5rem;
    color: white;
    font-weight: 600;
    z-index: 1000;
    animation: slideIn 0.3s ease;
  }

  .toast-notification.success {
    background-color: var(--color-success-500);
  }

  .toast-notification.error {
    background-color: var(--color-error-300);
  }

  @keyframes slideIn {
    from {
      transform: translateX(100%);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }

  @media (max-width: 768px) {
    .coupon-list-container {
      padding: 1rem;
    }

    .page-header {
      flex-direction: column;
      gap: 1rem;
      align-items: stretch;
    }

    .action-buttons {
      flex-direction: column;
    }

    .table {
      font-size: 12px;
    }
  }
</style>
