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
            {{ item.name }}
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

        <!-- Active Status Column -->
        <template #cell-isActive="{ item }">
          <label class="toggle-switch">
            <input v-model="item.isActive" type="checkbox" @change="toggleCampaignStatus(item)" />
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
  import { MOCK_CAMPAIGNS } from '@/constants/mockData';
  import { formatPeriod, getStatusText, getStatusBadgeType } from '@/utils/formatters';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseModal from '@/components/common/BaseModal.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import BasePagination from '@/components/common/Pagaination.vue';
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
      BasePagination,
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
        items: campaigns,
        currentPage,
        loading,
        showDeleteConfirm,
        selectedItem: selectedCampaignForDelete,
        triggerElement,
        totalItems,
        totalPages,
        itemsPerPage,
        paginatedItems: paginatedCampaigns,
        toggleItemStatus,
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
        { key: 'isActive', title: '활성화' },
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
      const toggleCampaignStatus = campaign => toggleItemStatus(campaign);

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
        toggleCampaignStatus,
        handlePageChange,
        handleItemsPerPageChange,
        formatPeriod,
        getStatusText,
        getStatusBadgeType,
      };
    },
  };
</script>

<style scoped>
  @import '@/styles/list-components.css';
</style>
