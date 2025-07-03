import { ref, computed } from 'vue';

/**
 * 쿠폰 목록 상태 관리 composable
 * 단일 책임: 목록 상태 관리 (페이징, 선택, 모달 등)
 */
export function useCouponListState() {
  // 데이터 상태
  const coupons = ref([]);
  const currentPage = ref(0);
  const totalPages = ref(0);
  const totalItems = ref(0);
  const itemsPerPage = ref(10);

  // UI 상태
  const showCreateModal = ref(false);
  const showDetailModal = ref(false);
  const showDeleteConfirm = ref(false);

  // 선택된 항목들
  const selectedCouponForDetail = ref(null);
  const selectedCouponForDelete = ref(null);
  const triggerElement = ref(null);

  // Computed
  const hasData = computed(() => coupons.value.length > 0);
  const isEmpty = computed(() => coupons.value.length === 0 && totalItems.value === 0);
  const hasMultiplePages = computed(() => totalPages.value > 1);

  const deleteConfirmMessage = computed(() =>
    selectedCouponForDelete.value
      ? `쿠폰 '${selectedCouponForDelete.value.name}'을(를) 삭제하시겠습니까?`
      : ''
  );

  // 데이터 업데이트 메서드
  const updateCoupons = (newCoupons, pagination = {}) => {
    coupons.value = newCoupons;

    if (pagination.totalPages !== undefined) totalPages.value = pagination.totalPages;
    if (pagination.totalElements !== undefined) totalItems.value = pagination.totalElements;
    if (pagination.page !== undefined) currentPage.value = pagination.page;
    if (pagination.size !== undefined) itemsPerPage.value = pagination.size;
  };

  const updateSingleCoupon = updatedCoupon => {
    const index = coupons.value.findIndex(c => c.id === updatedCoupon.id);
    if (index !== -1) {
      coupons.value[index] = updatedCoupon;
    }
  };

  const removeCoupon = couponId => {
    coupons.value = coupons.value.filter(c => c.id !== couponId);
    totalItems.value = Math.max(0, totalItems.value - 1);
  };

  // 모달 제어 메서드
  const openCreateModal = () => {
    showCreateModal.value = true;
  };

  const closeCreateModal = () => {
    showCreateModal.value = false;
  };

  const openDetailModal = coupon => {
    selectedCouponForDetail.value = coupon;
    showDetailModal.value = true;
  };

  const closeDetailModal = () => {
    selectedCouponForDetail.value = null;
    showDetailModal.value = false;
  };

  const openDeleteConfirm = (coupon, triggerEvent) => {
    selectedCouponForDelete.value = coupon;
    triggerElement.value = triggerEvent?.currentTarget || null;
    showDeleteConfirm.value = true;
  };

  const closeDeleteConfirm = () => {
    selectedCouponForDelete.value = null;
    triggerElement.value = null;
    showDeleteConfirm.value = false;
  };

  // 페이징 메서드
  const setPage = page => {
    currentPage.value = page;
  };

  const setItemsPerPage = size => {
    itemsPerPage.value = size;
    currentPage.value = 0; // 첫 페이지로 리셋
  };

  // 초기화
  const reset = () => {
    coupons.value = [];
    currentPage.value = 0;
    totalPages.value = 0;
    totalItems.value = 0;
    itemsPerPage.value = 10;

    closeCreateModal();
    closeDetailModal();
    closeDeleteConfirm();
  };

  return {
    // 상태
    coupons,
    currentPage,
    totalPages,
    totalItems,
    itemsPerPage,
    showCreateModal,
    showDetailModal,
    showDeleteConfirm,
    selectedCouponForDetail,
    selectedCouponForDelete,
    triggerElement,

    // Computed
    hasData,
    isEmpty,
    hasMultiplePages,
    deleteConfirmMessage,

    // 데이터 관리
    updateCoupons,
    updateSingleCoupon,
    removeCoupon,

    // 모달 제어
    openCreateModal,
    closeCreateModal,
    openDetailModal,
    closeDetailModal,
    openDeleteConfirm,
    closeDeleteConfirm,

    // 페이징
    setPage,
    setItemsPerPage,

    // 유틸리티
    reset,
  };
}
