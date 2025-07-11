<template>
  <div class="list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">캠페인 목록</h1>
      <BaseButton type="primary" @click="openCreateModal"> + 새 캠페인 생성 </BaseButton>
    </div>

    <!-- Campaign Table -->
    <BaseCard>
      <BaseTable
        :columns="tableColumns"
        :data="paginatedCampaigns"
        :loading="loading"
        hover
        @row-click="handleRowClick"
      >
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

        <!-- Coupon Column -->
        <template #cell-coupon="{ item }">
          <div class="item-secondary small">
            {{ getCouponName(item.couponId) }}
          </div>
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

    <!-- Create Window -->
    <BaseWindow v-model="showModal" title="캠페인 생성" :min-height="'500px'">
      <CampaignForm @save="handleSaveCampaign" @cancel="closeModal" />
    </BaseWindow>

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
  import { ref, computed, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { useListManagement } from '@/composables/useListManagement';
  import { MESSAGES } from '@/constants/messages';
  import campaignsAPI from '../api/campaigns.js';
  import couponsAPI from '@/features/coupons/api/coupons.js';
  import { useAuthStore } from '@/store/auth.js';
  import { formatPeriod, getStatusText, getStatusBadgeType } from '@/utils/formatters';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseWindow from '@/components/common/BaseWindow.vue';
  import BasePopover from '@/components/common/BasePopover.vue';
  import Pagination from '@/components/common/Pagination.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseTable from '@/components/common/BaseTable.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseToast from '@/components/common/BaseToast.vue';

  import TrashIcon from '@/components/icons/TrashIcon.vue';
  import CampaignForm from '../components/CampaignForm.vue';
  import { useToast } from '@/composables/useToast';

  export default {
    name: 'CampaignList',
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
      CampaignForm,
    },
    setup() {
      const router = useRouter();
      const authStore = useAuthStore();
      const coupons = ref([]);
      const couponCache = ref({});

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
        items,
        deleteItem,
        confirmDelete,
        cancelDelete,
        handlePageChange,
        handleItemsPerPageChange,
      } = useListManagement({
        itemName: MESSAGES.CAMPAIGN.ITEM_NAME,
        initialItems: [],
        itemsPerPage: 10,
      });

      const { showToast } = useToast();

      // Campaigns 데이터 로드
      const loadCampaigns = async () => {
        try {
          loading.value = true;
          const result = await campaignsAPI.getCampaigns({ shopId: 1, page: 0, size: 100 });
          items.value = result.content.map(item => ({
            ...item,
            templateId: item.templateId ?? item.template_id,
            customerGradeId: item.customerGradeId ?? item.customer_grade_id,
            tagId: item.tagId ?? item.tag_id,
          }));
        } catch (e) {
          console.error(e);
        } finally {
          loading.value = false;
        }
      };

      onMounted(() => {
        loadCampaigns();
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

      const handleSaveCampaign = async campaignData => {
        try {
          await campaignsAPI.createCampaign(campaignData);
          await loadCampaigns();
          showToast('캠페인이 생성되었습니다.', 'success');
        } catch (e) {
          console.error(e);
        } finally {
          closeModal();
        }
      };

      const deleteCampaign = (campaign, event) => deleteItem(campaign, event);

      const confirmDeleteWrapper = async () => {
        if (selectedCampaignForDelete.value) {
          try {
            await campaignsAPI.deleteCampaign(selectedCampaignForDelete.value.id);
            await loadCampaigns();
            showToast('캠페인이 삭제되었습니다.', 'success');
          } catch (e) {
            console.error(e);
          }
        }
        confirmDelete();
      };

      const getCouponName = couponId => {
        if (!couponId) return '쿠폰 정보 없음';
        const coupon = (Array.isArray(coupons.value) ? coupons.value : []).find(
          c => String(c.id) === String(couponId)
        );
        if (coupon) return coupon.name;
        if (couponCache.value[couponId]) return couponCache.value[couponId];
        couponsAPI
          .getCouponById(couponId)
          .then(c => {
            couponCache.value[couponId] = c.name;
          })
          .catch(() => {
            couponCache.value[couponId] = '쿠폰 정보 없음';
          });
        return '쿠폰 정보 없음';
      };

      // Row click handler
      const handleRowClick = (item, event) => {
        router.push(`/campaigns/${item.id}`);
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
        confirmDelete: confirmDeleteWrapper,
        cancelDelete,
        handlePageChange,
        handleItemsPerPageChange,
        getCouponName,
        handleRowClick,
        formatPeriod,
        getStatusText,
        getStatusBadgeType,
      };
    },
  };
</script>

<style scoped>
  @import '@/assets/css/list-components.css';
</style>
