<template>
  <div class="coupon-list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">쿠폰 목록</h1>
      <div class="header-actions">
        <BaseButton type="secondary" outline @click="openDrawer"> 필터 & 정렬 </BaseButton>
        <BaseButton type="primary" @click="openCreateModal"> + 새 쿠폰 생성 </BaseButton>
      </div>
    </div>

    <!-- Coupon Table -->
    <BaseCard>
      <BaseTable :columns="tableColumns" :data="coupons" :loading="loading" hover>
        <!-- Checkbox Header -->
        <template #header-checkbox>
          <input v-model="selectAll" type="checkbox" @change="toggleSelectAll" />
        </template>

        <!-- Checkbox Column -->
        <template #cell-checkbox="{ item }">
          <input v-model="selectedCoupons" type="checkbox" :value="item.id" />
        </template>

        <!-- Coupon Name Column -->
        <template #cell-name="{ item }">
          <div class="coupon-name">
            {{ item.name }}
          </div>
        </template>

        <!-- Discount Rate Column -->
        <template #cell-discountRate="{ item }">
          <BaseBadge type="primary">{{ item.discountRate }}%</BaseBadge>
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
            <BaseButton type="info" size="sm" @click="editCoupon(item)"> 수정 </BaseButton>
            <BaseButton type="error" size="sm" @click="deleteCoupon(item)"> 삭제 </BaseButton>
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

    <!-- Create/Edit Modal -->
    <BaseModal v-model="showModal" :title="isEditing ? '쿠폰 수정' : '쿠폰 생성'">
      <CouponForm
        :coupon="selectedCoupon"
        :is-editing="isEditing"
        @save="handleSaveCoupon"
        @cancel="closeModal"
      />
    </BaseModal>

    <!-- Filter Drawer -->
    <BaseDrawer v-model="showDrawer" title="필터 & 정렬" position="right" size="md">
      <div class="filter-content">
        <!-- 상태 필터 -->
        <div class="filter-section">
          <h4 class="filter-title">상태</h4>
          <BaseForm v-model="filters.status" type="radio" :options="statusOptions" name="status" />
        </div>

        <!-- 디자이너 필터 -->
        <div class="filter-section">
          <h4 class="filter-title">디자이너</h4>
          <BaseForm v-model="filters.designers" type="checkbox" :options="designerFilterOptions" />
        </div>

        <!-- 할인율 범위 -->
        <div class="filter-section">
          <h4 class="filter-title">할인율 범위</h4>
          <div class="discount-range">
            <BaseForm
              v-model="filters.minDiscount"
              type="number"
              placeholder="최소 %"
              style="width: 100px"
            />
            <span>~</span>
            <BaseForm
              v-model="filters.maxDiscount"
              type="number"
              placeholder="최대 %"
              style="width: 100px"
            />
          </div>
        </div>

        <!-- 정렬 옵션 -->
        <div class="filter-section">
          <h4 class="filter-title">정렬</h4>
          <BaseForm
            v-model="filters.sortBy"
            type="select"
            :options="sortOptions"
            placeholder="정렬 기준 선택"
          />
        </div>
      </div>

      <template #footer>
        <div class="drawer-actions">
          <BaseButton type="secondary" outline @click="resetFilters"> 초기화 </BaseButton>
          <BaseButton type="primary" @click="applyFilters"> 적용 </BaseButton>
        </div>
      </template>
    </BaseDrawer>

    <!-- Toast Notification -->
    <BaseToast ref="toast" />
  </div>
</template>

<script>
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BasePagination from '@/components/common/Pagaination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import BaseDrawer from '@/components/common/BaseDrawer.vue';
  import CouponForm from './CouponForm.vue';

  export default {
    name: 'CouponList',
    components: {
      BaseButton,
      BaseModal,
      BasePagination,
      BaseCard,
      BaseTable,
      BaseBadge,
      BaseToast,
      BaseDrawer,
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
            discountRate: 20,
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
            discountRate: 15,
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
            discountRate: 25,
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
            discountRate: 30,
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
            discountRate: 10,
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

        // 선택 관련
        selectedCoupons: [],
        selectAll: false,

        // 모달 관련
        showModal: false,
        isEditing: false,
        selectedCoupon: null,

        // 드로어 관련
        showDrawer: false,

        // 필터 관련
        filters: {
          status: 'all',
          designers: [],
          minDiscount: '',
          maxDiscount: '',
          sortBy: '',
        },

        // 로딩 상태
        loading: false,

        // 테이블 컬럼 정의
        tableColumns: [
          {
            key: 'checkbox',
            title: '',
            width: '50px',
          },
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
            key: 'discountRate',
            title: '할인율',
          },
          {
            key: 'isActive',
            title: '활성화',
          },
          {
            key: 'actions',
            title: 'Actions',
            width: '120px',
          },
        ],

        // 필터 옵션들
        statusOptions: [
          { value: 'all', text: '전체' },
          { value: 'active', text: '활성화' },
          { value: 'inactive', text: '비활성화' },
        ],
        designerFilterOptions: [
          { value: '이순신', text: '이순신' },
          { value: '김철수', text: '김철수' },
          { value: '박영희', text: '박영희' },
          { value: '최민수', text: '최민수' },
        ],
        sortOptions: [
          { value: 'name_asc', text: '이름 오름차순' },
          { value: 'name_desc', text: '이름 내림차순' },
          { value: 'discount_asc', text: '할인율 낮은순' },
          { value: 'discount_desc', text: '할인율 높은순' },
          { value: 'created_desc', text: '최신순' },
          { value: 'created_asc', text: '오래된순' },
        ],
      };
    },

    watch: {
      selectedCoupons() {
        this.selectAll = this.selectedCoupons.length === this.coupons.length;
      },
    },
    methods: {
      // 모달 관련
      openCreateModal() {
        this.isEditing = false;
        this.selectedCoupon = null;
        this.showModal = true;
      },

      editCoupon(coupon) {
        this.isEditing = true;
        this.selectedCoupon = { ...coupon };
        this.showModal = true;
      },

      closeModal() {
        this.showModal = false;
        this.selectedCoupon = null;
        this.isEditing = false;
      },

      // 쿠폰 관리
      handleSaveCoupon(couponData) {
        if (this.isEditing) {
          // 수정
          const index = this.coupons.findIndex(c => c.id === couponData.id);
          if (index !== -1) {
            this.coupons[index] = { ...couponData };
            this.showToastMessage('쿠폰이 수정되었습니다.', 'success');
          }
        } else {
          // 새로 생성
          const newCoupon = {
            ...couponData,
            id: Date.now(), // 실제로는 서버에서 받아올 ID
          };
          this.coupons.unshift(newCoupon);
          this.showToastMessage('쿠폰이 생성되었습니다.', 'success');
        }
        this.closeModal();
      },

      deleteCoupon(coupon) {
        if (confirm('정말 이 쿠폰을 삭제하시겠습니까?')) {
          const index = this.coupons.findIndex(c => c.id === coupon.id);
          if (index !== -1) {
            this.coupons.splice(index, 1);
            this.showToastMessage('쿠폰이 삭제되었습니다.', 'success');
          }
        }
      },

      toggleCouponStatus(coupon) {
        const status = coupon.isActive ? '활성화' : '비활성화';
        this.showToastMessage(`쿠폰이 ${status}되었습니다.`, 'success');
      },

      // 선택 관련
      toggleSelectAll() {
        if (this.selectAll) {
          this.selectedCoupons = this.coupons.map(c => c.id);
        } else {
          this.selectedCoupons = [];
        }
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

      // 드로어 관련
      openDrawer() {
        this.showDrawer = true;
      },

      resetFilters() {
        this.filters = {
          status: 'all',
          designers: [],
          minDiscount: '',
          maxDiscount: '',
          sortBy: '',
        };
      },

      applyFilters() {
        // 실제로는 API 호출하여 필터링된 데이터 가져오기
        console.log('Applying filters:', this.filters);
        this.showToastMessage('필터가 적용되었습니다.', 'success');
        this.showDrawer = false;
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

  .header-actions {
    display: flex;
    gap: 1rem;
    align-items: center;
  }

  .coupon-name {
    font-weight: 600;
    color: var(--color-neutral-dark);
  }

  .action-buttons {
    display: flex;
    gap: 0.5rem;
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

    .header-actions {
      justify-content: center;
    }

    .action-buttons {
      flex-direction: column;
    }

    .table {
      font-size: 12px;
    }
  }

  /* Drawer Content Styles */
  .filter-content {
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  .filter-section {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .filter-title {
    font-size: 16px;
    font-weight: 700;
    color: var(--color-neutral-dark);
    margin: 0;
  }

  .discount-range {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .drawer-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
  }

  @media (max-width: 768px) {
    .drawer-actions {
      flex-direction: column;
    }
  }
</style>
