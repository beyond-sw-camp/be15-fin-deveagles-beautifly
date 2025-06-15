<template>
  <div class="campaign-list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">캠페인 목록</h1>
      <BaseButton type="primary" @click="openCreateModal"> + 새 캠페인 생성 </BaseButton>
    </div>

    <!-- Campaign Table -->
    <BaseCard>
      <BaseTable :columns="tableColumns" :data="campaigns" :loading="loading" hover>
        <!-- Campaign Name Column -->
        <template #cell-name="{ item }">
          <div class="campaign-name">
            {{ item.name }}
          </div>
        </template>

        <!-- Period Column -->
        <template #cell-period="{ item }">
          <div class="campaign-period">
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
            <BaseButton
              :ref="`deleteBtn-${item.id}`"
              type="error"
              size="sm"
              @click="deleteCampaign(item, $event)"
            >
              삭제
            </BaseButton>
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
      :message="`'${selectedCampaignForDelete?.name}' 캠페인을 정말 삭제하시겠습니까?`"
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
      CampaignForm,
    },
    data() {
      return {
        // 캠페인 데이터 (목업 데이터)
        campaigns: [
          {
            id: 1,
            name: '여름 시즌 프로모션',
            startDate: '2025-06-01',
            endDate: '2025-08-31',
            status: 'scheduled',
            isActive: true,
            description: '여름 시즌 특별 할인 캠페인',
            targetAudience: 'all',
          },
          {
            id: 2,
            name: '신규 고객 환영 캠페인',
            startDate: '2025-01-01',
            endDate: '2025-12-31',
            status: 'active',
            isActive: true,
            description: '신규 가입 고객 대상 웰컴 캠페인',
            targetAudience: 'new',
          },
          {
            id: 3,
            name: '연말 감사 이벤트',
            startDate: '2024-12-01',
            endDate: '2024-12-31',
            status: 'completed',
            isActive: false,
            description: '연말 고객 감사 이벤트',
            targetAudience: 'vip',
          },
          {
            id: 4,
            name: 'VIP 고객 전용 캠페인',
            startDate: '2025-03-01',
            endDate: '2025-05-31',
            status: 'scheduled',
            isActive: true,
            description: 'VIP 등급 고객 대상 특별 혜택',
            targetAudience: 'vip',
          },
          {
            id: 5,
            name: '봄맞이 리뉴얼 캠페인',
            startDate: '2025-03-15',
            endDate: '2025-04-30',
            status: 'draft',
            isActive: false,
            description: '봄 시즌 메뉴 리뉴얼 홍보',
            targetAudience: 'all',
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
        selectedCampaignForDelete: null,
        triggerElement: null,

        // 로딩 상태
        loading: false,

        // 테이블 컬럼 정의
        tableColumns: [
          {
            key: 'name',
            title: '캠페인명',
          },
          {
            key: 'period',
            title: '기간',
          },
          {
            key: 'status',
            title: '상태',
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

      // 캠페인 관리
      handleSaveCampaign(campaignData) {
        // 새로 생성
        const newCampaign = {
          ...campaignData,
          id: Date.now(), // 실제로는 서버에서 받아올 ID
        };
        this.campaigns.unshift(newCampaign);
        this.showToastMessage('캠페인이 생성되었습니다.', 'success');
        this.closeModal();
      },

      deleteCampaign(campaign, event) {
        this.selectedCampaignForDelete = campaign;
        this.triggerElement = event.target.closest('button');
        this.showDeleteConfirm = true;
      },

      confirmDelete() {
        if (this.selectedCampaignForDelete) {
          const index = this.campaigns.findIndex(c => c.id === this.selectedCampaignForDelete.id);
          if (index !== -1) {
            this.campaigns.splice(index, 1);
            this.showToastMessage('캠페인이 삭제되었습니다.', 'success');
          }
        }
        this.cancelDelete();
      },

      cancelDelete() {
        this.selectedCampaignForDelete = null;
        this.triggerElement = null;
        this.showDeleteConfirm = false;
      },

      toggleCampaignStatus(campaign) {
        const status = campaign.isActive ? '활성화' : '비활성화';
        this.showToastMessage(`캠페인이 ${status}되었습니다.`, 'success');
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

      // 유틸리티 메서드
      formatPeriod(startDate, endDate) {
        const start = new Date(startDate).toLocaleDateString('ko-KR');
        const end = new Date(endDate).toLocaleDateString('ko-KR');
        return `${start} ~ ${end}`;
      },

      getStatusText(status) {
        const statusMap = {
          draft: '임시저장',
          scheduled: '예정',
          active: '진행중',
          completed: '완료',
          cancelled: '취소',
        };
        return statusMap[status] || status;
      },

      getStatusBadgeType(status) {
        const typeMap = {
          draft: 'neutral',
          scheduled: 'warning',
          active: 'success',
          completed: 'info',
          cancelled: 'error',
        };
        return typeMap[status] || 'neutral';
      },

      // 토스트 알림
      showToastMessage(message, type = 'success') {
        this.$refs.toast[type](message);
      },
    },
  };
</script>

<style scoped>
  .campaign-list-container {
    padding: 2rem;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
  }

  .campaign-name {
    font-weight: 600;
    color: var(--color-neutral-dark);
  }

  .campaign-period {
    font-size: 13px;
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

  @media (max-width: 768px) {
    .campaign-list-container {
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
