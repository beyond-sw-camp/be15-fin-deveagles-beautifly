<template>
  <div class="list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">쿠폰 목록</h1>
      <BaseButton type="primary" @click="openCreateModal"> + 새 쿠폰 생성 </BaseButton>
    </div>

    <!-- Coupon Table -->
    <BaseCard>
      <BaseTable
        :columns="tableColumns"
        :data="paginatedCoupons"
        :loading="loading"
        hover
        @row-click="handleRowClick"
      >
        <!-- Coupon Name Column -->
        <template #cell-name="{ item }">
          <div class="item-name">
            {{ item.name }}
          </div>
        </template>

        <!-- Product Column -->
        <template #cell-product="{ item }">
          <div class="item-secondary">
            {{ item.product }}
          </div>
        </template>

        <!-- Designer Column -->
        <template #cell-designer="{ item }">
          <div class="item-secondary">
            {{ item.designer }}
          </div>
        </template>

        <!-- Discount Column -->
        <template #cell-discount="{ item }">
          <BaseBadge type="success"> {{ item.discount }}% </BaseBadge>
        </template>

        <!-- Active Status Column -->
        <template #cell-isActive="{ item }">
          <label class="toggle-switch enhanced" @click.stop>
            <input v-model="item.isActive" type="checkbox" @change="toggleCouponStatus(item)" />
            <span class="slider"></span>
          </label>
        </template>

        <!-- Actions Column -->
        <template #cell-actions="{ item }">
          <div class="action-buttons" @click.stop>
            <div class="tooltip-container">
              <BaseButton
                :ref="`deleteBtn-${item.id}`"
                type="ghost"
                size="sm"
                class="icon-button"
                @click="deleteCoupon(item, $event)"
              >
                <TrashIcon :size="16" color="var(--color-error-300)" />
              </BaseButton>
              <span class="tooltip tooltip-bottom tooltip-primary">삭제</span>
            </div>
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

    <!-- Create Window -->
    <BaseWindow
      v-model="showModal"
      title="쿠폰 생성"
      :min-height="'650px'"
      :before-close="checkBeforeClose"
    >
      <CouponForm
        @save="handleSaveCoupon"
        @cancel="closeModal"
        @set-before-close="setBeforeCloseCallback"
      />
    </BaseWindow>

    <!-- Detail Modal -->
    <CouponDetailModal v-model="showDetailModal" :coupon-data="selectedCouponForDetail" />

    <!-- Delete Confirm Popover -->
    <BasePopover
      v-model="showDeleteConfirm"
      title="쿠폰 삭제"
      :message="deleteConfirmMessage"
      confirm-text="삭제"
      cancel-text="취소"
      confirm-type="error"
      placement="top"
      size="sm"
      :trigger-element="triggerElement"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />

    <!-- Toast -->
    <BaseToast ref="toast" />
  </div>
</template>

<script>
  import { ref, computed, onMounted } from 'vue';
  import { useToast } from '@/composables/useToast';
  import { createLogger } from '@/plugins/logger.js';
  import couponsAPI from '../api/coupons.js';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseWindow from '@/components/common/BaseWindow.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import CouponForm from '../components/CouponForm.vue';
  import CouponDetailModal from '../components/CouponDetailModal.vue';

  const logger = createLogger('CouponList');

  export default {
    name: 'CouponList',
    components: {
      BaseButton,
      BaseWindow,
      BasePopover,
      BasePagination: Pagination,
      BaseCard,
      BaseTable,
      BaseBadge,
      BaseToast,

      TrashIcon,
      CouponForm,
      CouponDetailModal,
    },
    setup() {
      // Toast composable
      const { showToast } = useToast();

      // Local state
      const coupons = ref([]);
      const paginatedCoupons = ref([]);
      const currentPage = ref(0);
      const totalPages = ref(0);
      const totalItems = ref(0);
      const itemsPerPage = ref(10);
      const loading = ref(false);
      const showModal = ref(false);
      const showDetailModal = ref(false);
      const selectedCouponForDetail = ref(null);
      const showDeleteConfirm = ref(false);
      const selectedCouponForDelete = ref(null);
      const triggerElement = ref(null);
      const beforeCloseCallback = ref(null);

      // 모달 변경사항 체크는 BaseWindow에서 직접 처리

      // Table columns
      const tableColumns = [
        { key: 'name', title: '쿠폰명' },
        { key: 'product', title: '상품' },
        { key: 'designer', title: '디자이너' },
        { key: 'discount', title: '할인율' },
        { key: 'isActive', title: '활성화' },
        { key: 'actions', title: 'Actions', width: '80px' },
      ];

      // Computed
      const deleteConfirmMessage = computed(() =>
        selectedCouponForDelete.value
          ? `쿠폰 '${selectedCouponForDelete.value.name}'을(를) 삭제하시겠습니까?`
          : ''
      );

      // API Methods
      const loadCoupons = async (searchParams = {}) => {
        try {
          loading.value = true;
          logger.info('쿠폰 목록 로드 시작', searchParams);

          const response = await couponsAPI.getCoupons({
            page: currentPage.value,
            size: itemsPerPage.value,
            ...searchParams,
          });

          // API 응답에서 data 추출
          const data = response.data || response;

          coupons.value = data.content || [];
          paginatedCoupons.value = data.content || [];
          totalPages.value = data.totalPages || 0;
          totalItems.value = data.totalElements || 0;

          logger.info('쿠폰 목록 로드 완료', {
            totalItems: totalItems.value,
            totalPages: totalPages.value,
          });
        } catch (error) {
          logger.error('쿠폰 목록 로드 실패', error);
          showToast(error.message || '쿠폰 목록을 불러오는 데 실패했습니다.', 'error');
          coupons.value = [];
          paginatedCoupons.value = [];
        } finally {
          loading.value = false;
        }
      };

      const createCoupon = async couponData => {
        try {
          logger.info('쿠폰 생성 시작', couponData);

          const newCoupon = await couponsAPI.createCoupon(couponData);

          logger.info('쿠폰 생성 완료', newCoupon);
          showToast('쿠폰이 성공적으로 생성되었습니다.', 'success');

          await loadCoupons(); // 목록 새로고침
        } catch (error) {
          logger.error('쿠폰 생성 실패', error);
          showToast(error.message || '쿠폰 생성에 실패했습니다.', 'error');
          throw error;
        }
      };

      const deleteCouponById = async id => {
        try {
          logger.info('쿠폰 삭제 시작', { id });

          await couponsAPI.deleteCoupon(id);

          logger.info('쿠폰 삭제 완료', { id });
          showToast('쿠폰이 삭제되었습니다.', 'success');

          await loadCoupons(); // 목록 새로고침
        } catch (error) {
          logger.error('쿠폰 삭제 실패', error);
          showToast(error.message || '쿠폰 삭제에 실패했습니다.', 'error');
        }
      };

      const toggleCouponStatusById = async coupon => {
        try {
          logger.info('쿠폰 상태 토글 시작', { id: coupon.id, currentStatus: coupon.isActive });

          const updatedCoupon = await couponsAPI.toggleCouponStatus(coupon.id);

          // 로컬 상태 업데이트
          const index = paginatedCoupons.value.findIndex(c => c.id === coupon.id);
          if (index !== -1) {
            paginatedCoupons.value[index] = updatedCoupon;
          }

          logger.info('쿠폰 상태 토글 완료', { id: coupon.id, newStatus: updatedCoupon.isActive });
          showToast(
            `쿠폰이 ${updatedCoupon.isActive ? '활성화' : '비활성화'}되었습니다.`,
            'success'
          );
        } catch (error) {
          logger.error('쿠폰 상태 토글 실패', error);
          showToast(error.message || '쿠폰 상태 변경에 실패했습니다.', 'error');

          // 실패 시 원래 상태로 되돌리기
          coupon.isActive = !coupon.isActive;
        }
      };

      // Event Handlers
      const openCreateModal = () => {
        beforeCloseCallback.value = null; // 콜백 초기화
        showModal.value = true;
      };

      const closeModal = () => {
        beforeCloseCallback.value = null; // 콜백 초기화
        showModal.value = false;
      };

      const setBeforeCloseCallback = callback => {
        beforeCloseCallback.value = callback;
      };

      const checkBeforeClose = () => {
        if (beforeCloseCallback.value) {
          return beforeCloseCallback.value();
        }
        return true; // 콜백이 없으면 항상 닫기 허용
      };

      const handleSaveCoupon = async couponData => {
        try {
          await createCoupon(couponData);
          closeModal();
        } catch (error) {
          // 에러는 createCoupon에서 처리됨
        }
      };

      const deleteCoupon = (coupon, event) => {
        selectedCouponForDelete.value = coupon;
        triggerElement.value = event.currentTarget;
        showDeleteConfirm.value = true;
      };

      const confirmDelete = async () => {
        if (selectedCouponForDelete.value) {
          await deleteCouponById(selectedCouponForDelete.value.id);
        }
        showDeleteConfirm.value = false;
        selectedCouponForDelete.value = null;
        triggerElement.value = null;
      };

      const cancelDelete = () => {
        showDeleteConfirm.value = false;
        selectedCouponForDelete.value = null;
        triggerElement.value = null;
      };

      const toggleCouponStatus = coupon => {
        toggleCouponStatusById(coupon);
      };

      const handlePageChange = page => {
        currentPage.value = page;
        loadCoupons();
      };

      const handleItemsPerPageChange = size => {
        itemsPerPage.value = size;
        currentPage.value = 0; // 첫 페이지로 리셋
        loadCoupons();
      };

      // Detail modal methods
      const openDetailModal = coupon => {
        selectedCouponForDetail.value = coupon;
        showDetailModal.value = true;
      };

      const handleRowClick = item => {
        openDetailModal(item);
      };

      // 초기 데이터 로드
      onMounted(() => {
        loadCoupons();
      });

      return {
        // State
        paginatedCoupons,
        currentPage,
        loading,
        showModal,
        showDetailModal,
        selectedCouponForDetail,
        showDeleteConfirm,
        selectedCouponForDelete,
        triggerElement,
        itemsPerPage,

        // Computed
        totalItems,
        totalPages,
        deleteConfirmMessage,

        // Data
        tableColumns,

        // Methods
        openCreateModal,
        closeModal,
        setBeforeCloseCallback,
        checkBeforeClose,
        handleSaveCoupon,
        deleteCoupon,
        confirmDelete,
        cancelDelete,
        toggleCouponStatus,
        handlePageChange,
        handleItemsPerPageChange,
        openDetailModal,
        handleRowClick,
        loadCoupons,
      };
    },
  };
</script>

<style scoped>
  @import '@/assets/css/list-components.css';
</style>
