<template>
  <div class="list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">쿠폰 목록</h1>
      <BaseButton type="primary" @click="openCreateModal"> + 새 쿠폰 생성 </BaseButton>
    </div>

    <!-- Coupon Table -->
    <BaseCard>
      <BaseTable :columns="tableColumns" :data="paginatedCoupons" :loading="loading" hover>
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
          <label class="toggle-switch">
            <input v-model="item.isActive" type="checkbox" @change="toggleCouponStatus(item)" />
            <span class="slider"></span>
          </label>
        </template>

        <!-- Actions Column -->
        <template #cell-actions="{ item }">
          <div class="action-buttons">
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

    <!-- Create Modal -->
    <BaseModal v-model="showModal" title="쿠폰 생성">
      <CouponForm @save="handleSaveCoupon" @cancel="closeModal" />
    </BaseModal>

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
  import { ref, computed } from 'vue';
  import { useListManagement } from '@/composables/useListManagement';
  import { MESSAGES } from '@/constants/messages';
  import { MOCK_COUPONS } from '@/constants/mockData';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import BasePagination from '@/components/common/Pagaination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  import TrashIcon from '@/components/icons/TrashIcon.vue';
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

      TrashIcon,
      CouponForm,
    },
    setup() {
      // List management composable
      const {
        items: coupons,
        currentPage,
        loading,
        showDeleteConfirm,
        selectedItem: selectedCouponForDelete,
        triggerElement,
        totalItems,
        totalPages,
        itemsPerPage,
        paginatedItems: paginatedCoupons,
        toggleItemStatus,
        deleteItem,
        confirmDelete,
        cancelDelete,
        handlePageChange,
        handleItemsPerPageChange,
        addItem,
      } = useListManagement({
        itemName: MESSAGES.COUPON.ITEM_NAME,
        initialItems: MOCK_COUPONS,
        itemsPerPage: 10,
      });

      // Local state
      const showModal = ref(false);

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
          ? MESSAGES.COUPON.DELETE_CONFIRM(selectedCouponForDelete.value.name)
          : ''
      );

      // Methods
      const openCreateModal = () => {
        showModal.value = true;
      };

      const closeModal = () => {
        showModal.value = false;
      };

      const handleSaveCoupon = couponData => {
        addItem(couponData);
        closeModal();
      };

      const deleteCoupon = (coupon, event) => deleteItem(coupon, event);
      const toggleCouponStatus = coupon => toggleItemStatus(coupon);

      return {
        // State
        paginatedCoupons,
        currentPage,
        loading,
        showModal,
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
        handleSaveCoupon,
        deleteCoupon,
        confirmDelete,
        cancelDelete,
        toggleCouponStatus,
        handlePageChange,
        handleItemsPerPageChange,
      };
    },
  };
</script>

<style scoped>
  @import '@/styles/list-components.css';
</style>
