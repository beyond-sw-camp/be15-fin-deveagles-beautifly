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
        :data="processedPagedData"
        :loading="loading"
        hover
        @row-click="onTableRowClick"
      >
        <!-- Coupon Title Column -->
        <template #cell-couponTitle="{ item }">
          <div class="item-name">
            {{ item.couponTitle }}
          </div>
        </template>

        <!-- 디자이너 Column -->
        <template #cell-designer="{ item }">
          <div class="item-secondary">
            {{
              item.designerInfo && item.designerInfo.staffName
                ? item.designerInfo.staffName
                : '전체'
            }}
          </div>
        </template>
        <!-- 만료일 Column -->
        <template #cell-expirationDate="{ item }">
          <div class="item-secondary">
            {{ formatDate(item.expirationDate) }}
          </div>
        </template>

        <!-- Product/Service Column -->
        <template #cell-productService="{ item }">
          <div class="item-secondary">
            {{ item.primaryItemInfo?.name }}
            <span v-if="item.primaryItemInfo?.name && item.secondaryItemInfo?.name">, </span>
            {{
              item.secondaryItemInfo && item.secondaryItemInfo.name
                ? item.secondaryItemInfo.name
                : '전체'
            }}
          </div>
        </template>

        <!-- Discount Rate Column -->
        <template #cell-discountRate="{ item }">
          <BaseBadge type="success"> {{ item.discountRate }}% </BaseBadge>
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
      :current-page="page"
      :total-pages="totalPages"
      :total-items="total"
      :items-per-page="pageSize"
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
  import { getLogger, getErrorLogger, getPerformanceLogger } from '@/plugins/LoggerManager.js';
  import couponsAPI from '../api/coupons.js';
  import ApiErrorHandler from '../utils/ApiErrorHandler.js';
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

  const logger = getLogger('CouponList');
  const errorLogger = getErrorLogger();
  const perfLogger = getPerformanceLogger();

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

      const executeWithErrorHandling = async (apiCall, context, successMessage = null) => {
        const startTime = performance.now();

        try {
          loading.value = true;
          logger.info(`${context} 시작`);

          const result = await apiCall();

          const duration = performance.now() - startTime;
          perfLogger.measure(context, Math.round(duration));
          logger.info(`${context} 완료`);

          if (successMessage) {
            showToast(successMessage, 'success');
          }

          return result;
        } catch (error) {
          const handledError = ApiErrorHandler.handleCouponError(error, context.toLowerCase());

          errorLogger.apiError(context, error, {
            duration: Math.round(performance.now() - startTime),
          });

          showToast(handledError.message, 'error');
          throw handledError;
        } finally {
          loading.value = false;
        }
      };

      // Local state
      const coupons = ref([]);
      const page = ref(1);
      const pageSize = ref(10);
      const total = ref(0);
      const totalPages = ref(0);
      const loading = ref(false);
      const showModal = ref(false);
      const showDetailModal = ref(false);
      const selectedCouponForDetail = ref(null);
      const showDeleteConfirm = ref(false);
      const selectedCouponForDelete = ref(null);
      const triggerElement = ref(null);
      const beforeCloseCallback = ref(null);

      // Table columns
      const tableColumns = [
        { key: 'couponTitle', title: '쿠폰명' },
        { key: 'designer', title: '디자이너' },
        { key: 'productService', title: '상품/서비스' },
        { key: 'discountRate', title: '할인율' },
        { key: 'expirationDate', title: '만료일' },
        { key: 'isActive', title: '활성화' },
        { key: 'actions', title: 'Actions', width: '80px' },
      ];

      // Computed
      const deleteConfirmMessage = computed(() =>
        selectedCouponForDelete.value
          ? `쿠폰 '${selectedCouponForDelete.value.couponTitle}'을(를) 삭제하시겠습니까?`
          : ''
      );

      const processedPagedData = computed(() => {
        return coupons.value;
      });

      // 날짜 포맷 함수
      function formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
        });
      }

      // API Methods
      const loadCoupons = async () => {
        const response = await executeWithErrorHandling(
          () => couponsAPI.getCoupons({ page: page.value - 1, size: pageSize.value }),
          '쿠폰 목록 조회'
        );

        logger.debug('loadCoupons - API Response:', response);

        if (!response || !Array.isArray(response.content)) {
          throw new Error('유효하지 않은 응답 데이터입니다.');
        }

        coupons.value = response.content;
        total.value = response.totalElements;
        totalPages.value = response.totalPages;
        page.value = response.page + 1; // Convert 0-indexed API page to 1-indexed for local ref

        logger.info('쿠폰 목록 로드 완료', {
          totalItems: total.value,
          totalPages: totalPages.value,
          currentPage: page.value,
          itemsPerPage: pageSize.value,
          loadedItems: coupons.value.length,
        });
      };

      const createCoupon = async couponData => {
        await executeWithErrorHandling(
          () => couponsAPI.createCoupon(couponData),
          '쿠폰 생성',
          '쿠폰이 성공적으로 생성되었습니다.'
        );
        await loadCoupons(); // 목록 새로고침
      };

      const deleteCouponById = async id => {
        await executeWithErrorHandling(
          () => couponsAPI.deleteCoupon(id),
          '쿠폰 삭제',
          '쿠폰이 삭제되었습니다.'
        );
        await loadCoupons(); // 목록 새로고침
      };

      const toggleCouponStatusById = async coupon => {
        const updatedCoupon = await executeWithErrorHandling(
          () => couponsAPI.toggleCouponStatus(coupon.id),
          '쿠폰 상태 토글'
        );

        // 로컬 상태 업데이트
        const index = coupons.value.findIndex(c => c.id === coupon.id);
        if (index !== -1) {
          coupons.value[index] = updatedCoupon;
        }
        logger.info('쿠폰 상태 토글 완료', { id: coupon.id, newStatus: updatedCoupon.isActive });

        // 상태별 메시지
        const statusMessage = updatedCoupon.isActive ? '활성화' : '비활성화';
        showToast(`쿠폰이 ${statusMessage}되었습니다.`, 'success');
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
          console.log(error);
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

      const handlePageChange = async newPage => {
        page.value = newPage;
        await loadCoupons();
      };

      const handleItemsPerPageChange = async size => {
        pageSize.value = size;
        page.value = 1; // Reset to first page when items per page changes
        await loadCoupons();
      };

      // Detail modal methods
      const openDetailModal = coupon => {
        selectedCouponForDetail.value = coupon;
        showDetailModal.value = true;
      };

      // 헤더 클릭시 상세로 연결되는 버그 방지용 row 클릭 핸들러
      function onTableRowClick(item, event) {
        // 헤더 셀 클릭시(TH) 무시
        if (event?.target?.tagName === 'TH') return;
        openDetailModal(item);
      }

      // 초기 데이터 로드
      onMounted(() => {
        loadCoupons();
      });

      return {
        // State
        loading,
        showModal,
        showDetailModal,
        selectedCouponForDetail,
        showDeleteConfirm,
        selectedCouponForDelete,
        triggerElement,
        page,
        pageSize,
        total,
        totalPages,
        deleteConfirmMessage,
        processedPagedData,

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
        handleRowClick: onTableRowClick, // Keep this for compatibility if other parts of the app use it
        formatDate,
        onTableRowClick,
        loadCoupons,
      };
    },
  };
</script>

<style scoped>
  @import '@/assets/css/list-components.css';
</style>
