<template>
  <div class="list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">캠페인 목록</h1>
      <BaseButton type="primary" @click="openCreateModal"> + 새 캠페인 생성 </BaseButton>
    </div>

    <!-- Campaign Table -->
    <BaseCard>
      <BaseTable :columns="tableColumns" :data="paginatedCampaigns" :loading="loading" hover>
        <!-- Campaign Name Column -->
        <template #cell-name="{ item }">
          <div class="item-name">
            <router-link :to="`/campaigns/${item.id}`" class="campaign-link">
              {{ item.name }}
            </router-link>
          </div>
        </template>

        <!-- Period Column -->
        <template #cell-period="{ item }">
          <div class="item-secondary small">
            {{ formatPeriod(item.startDate, item.endDate) }}
          </div>
        </template>

        <!-- Status Column -->
        <template #cell-status="{ item }">
          <BaseBadge :type="getStatusBadgeType(item.status)">
            {{ getStatusText(item.status) }}
          </BaseBadge>
        </template>

        <!-- Coupon Column -->
        <template #cell-coupon="{ item }">
          <div class="item-secondary small">
            {{ getCouponName(item.couponId) }}
          </div>
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
                @click="deleteCampaign(item, $event)"
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
            <p class="text-gray-500">등록된 캠페인이 없습니다.</p>
            <BaseButton type="primary" @click="openCreateModal">
              첫 번째 캠페인 생성하기
            </BaseButton>
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
    <BaseModal v-model="showModal" title="캠페인 생성">
      <CampaignForm @save="handleSaveCampaign" @cancel="closeModal" />
    </BaseModal>

    <!-- Delete Confirm Popover -->
    <BasePopover
      v-model="showDeleteConfirm"
      title="캠페인 삭제"
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
  import { MOCK_CAMPAIGNS, MOCK_COUPONS } from '@/constants/mockData';
  import { formatPeriod, getStatusText, getStatusBadgeType } from '@/utils/formatters';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import CampaignForm from '../components/CampaignForm.vue';

  export default {
    name: 'CampaignList',
    components: {
      BaseButton,
      BaseModal,
      BasePopover,
      BasePagination: Pagination,
      BaseCard,
      BaseTable,
      BaseBadge,
      BaseToast,

      TrashIcon,
      CampaignForm,
    },
    setup() {
      // List management composable
      const {
        currentPage,
        loading,
        showDeleteConfirm,
        selectedItem: selectedCampaignForDelete,
        triggerElement,
        totalItems,
        totalPages,
        itemsPerPage,
        paginatedItems: paginatedCampaigns,
        deleteItem,
        confirmDelete,
        cancelDelete,
        handlePageChange,
        handleItemsPerPageChange,
        addItem,
      } = useListManagement({
        itemName: MESSAGES.CAMPAIGN.ITEM_NAME,
        initialItems: MOCK_CAMPAIGNS,
        itemsPerPage: 10,
      });

      // Local state
      const showModal = ref(false);

      // Table columns
      const tableColumns = [
        { key: 'name', title: '캠페인명' },
        { key: 'period', title: '기간' },
        { key: 'status', title: '상태' },
        { key: 'coupon', title: '쿠폰' },
        { key: 'actions', title: 'Actions', width: '80px' },
      ];

      // Computed
      const deleteConfirmMessage = computed(() =>
        selectedCampaignForDelete.value
          ? MESSAGES.CAMPAIGN.DELETE_CONFIRM(selectedCampaignForDelete.value.name)
          : ''
      );

      // Methods
      const openCreateModal = () => {
        showModal.value = true;
      };

      const closeModal = () => {
        showModal.value = false;
      };

      const handleSaveCampaign = campaignData => {
        addItem(campaignData);
        closeModal();
      };

      const deleteCampaign = (campaign, event) => deleteItem(campaign, event);

      const getCouponName = couponId => {
        const coupon = MOCK_COUPONS.find(c => c.id === couponId);
        return coupon ? coupon.name : '쿠폰 정보 없음';
      };

      return {
        // State
        paginatedCampaigns,
        currentPage,
        loading,
        showModal,
        showDeleteConfirm,
        selectedCampaignForDelete,
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
        handleSaveCampaign,
        deleteCampaign,
        confirmDelete,
        cancelDelete,
        handlePageChange,
        handleItemsPerPageChange,
        getCouponName,
        formatPeriod,
        getStatusText,
        getStatusBadgeType,
      };
    },
  };
</script>

<style scoped>
  @import '@/assets/css/list-components.css';

  .campaign-link {
    color: var(--color-primary-main);
    text-decoration: none;
    font-weight: 500;
    transition: all 0.2s ease;
  }

  .campaign-link:hover {
    color: var(--color-primary-dark);
    text-decoration: underline;
  }
</style>
