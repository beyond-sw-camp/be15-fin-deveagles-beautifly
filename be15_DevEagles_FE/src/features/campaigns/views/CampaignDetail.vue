<template>
  <div class="campaign-detail-container">
    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <BaseButton type="secondary" size="xs" class="back-button" @click="goBack">
        <ArrowLeftIcon :size="14" />
        목록으로
      </BaseButton>
    </div>

    <!-- Header -->
    <div class="page-header">
      <h1 class="font-screen-title">{{ campaign?.name || '캠페인 상세' }}</h1>
    </div>

    <!-- Campaign Info Card -->
    <BaseCard class="campaign-info-card">
      <div class="campaign-info">
        <div class="campaign-status">
          <BaseBadge :type="getStatusBadgeType(campaign?.status)">
            {{ getStatusText(campaign?.status) }}
          </BaseBadge>
        </div>
        <div class="campaign-basic-info">
          <div class="info-item">
            <span class="label">기간</span>
            <span class="value">{{ formatPeriod(campaign?.startDate, campaign?.endDate) }}</span>
          </div>
          <div class="info-item">
            <span class="label">생성일</span>
            <span class="value">{{ formatDate(campaign?.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="label">설명</span>
            <span class="value">{{ campaign?.description || '설명이 없습니다.' }}</span>
          </div>
        </div>
      </div>
    </BaseCard>

    <!-- Tabs -->
    <div class="tabs-container">
      <BaseTab v-model="activeTab" :tabs="tabs" />
    </div>

    <!-- Tab Content -->
    <div class="tab-content">
      <!-- 캠페인 상세 보기 탭 -->
      <div v-if="activeTab === '캠페인 상세'" class="tab-panel">
        <BaseCard>
          <h3 class="section-title">캠페인 상세 정보</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">캠페인명</span>
              <span class="value">{{ campaign?.name }}</span>
            </div>
            <div class="detail-item">
              <span class="label">시작일</span>
              <span class="value">{{ formatDate(campaign?.startDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">종료일</span>
              <span class="value">{{ formatDate(campaign?.endDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">상태</span>
              <span class="value">{{ getStatusText(campaign?.status) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">쿠폰</span>
              <span class="value">
                <button
                  class="coupon-link"
                  :disabled="!campaign?.couponId"
                  @click="showCouponDetail"
                >
                  {{ getCouponName(campaign?.couponId) }}
                </button>
              </span>
            </div>
            <div class="detail-item full-width">
              <span class="label">설명</span>
              <span class="value">{{ campaign?.description || '설명이 없습니다.' }}</span>
            </div>
          </div>
        </BaseCard>
      </div>

      <!-- 성과 분석 탭 -->
      <div v-if="activeTab === '성과 분석'" class="tab-panel">
        <BaseCard>
          <div class="empty-state">
            <div class="empty-icon">📊</div>
            <h3>성과 분석</h3>
            <p>캠페인 성과 분석 데이터를 준비 중입니다.</p>
          </div>
        </BaseCard>
      </div>
    </div>

    <!-- Toast -->
    <BaseToast ref="toast" />

    <!-- Coupon Detail Modal -->
    <CouponDetailModal v-model="showCouponModal" :coupon-data="selectedCoupon" />
  </div>
</template>

<script>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { MOCK_CAMPAIGNS, MOCK_COUPONS } from '@/constants/mockData';
  import { formatPeriod, formatDate, getStatusText, getStatusBadgeType } from '@/utils/formatters';

  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseCard from '@/components/common/BaseCard.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';
  import BaseTab from '@/components/common/BaseTab.vue';
  import BaseToast from '@/components/common/BaseToast.vue';
  import ArrowLeftIcon from '@/components/icons/ArrowLeftIcon.vue';
  import CouponDetailModal from '@/features/coupons/components/CouponDetailModal.vue';

  export default {
    name: 'CampaignDetail',
    components: {
      BaseButton,
      BaseCard,
      BaseBadge,
      BaseTab,
      BaseToast,
      ArrowLeftIcon,
      CouponDetailModal,
    },
    setup() {
      const route = useRoute();
      const router = useRouter();
      const toast = ref(null);

      // State
      const campaign = ref(null);
      const activeTab = ref('캠페인 상세');
      const tabs = ['캠페인 상세', '성과 분석'];
      const showCouponModal = ref(false);
      const selectedCoupon = ref(null);

      // Methods
      const loadCampaign = () => {
        const campaignId = parseInt(route.params.id);
        const foundCampaign = MOCK_CAMPAIGNS.find(c => c.id === campaignId);

        if (foundCampaign) {
          campaign.value = { ...foundCampaign };
        } else {
          toast.value?.show('캠페인을 찾을 수 없습니다.', 'error');
          router.push('/campaigns');
        }
      };

      const goBack = () => {
        router.push('/campaigns');
      };

      const getCouponName = couponId => {
        const coupon = MOCK_COUPONS.find(c => c.id === couponId);
        return coupon ? coupon.name : '쿠폰 정보 없음';
      };

      const showCouponDetail = () => {
        if (!campaign.value?.couponId) {
          toast.value?.show('쿠폰 정보가 없습니다.', 'error');
          return;
        }

        const coupon = MOCK_COUPONS.find(c => c.id === campaign.value.couponId);
        if (coupon) {
          selectedCoupon.value = coupon;
          showCouponModal.value = true;
        } else {
          toast.value?.show('쿠폰을 찾을 수 없습니다.', 'error');
        }
      };

      // Lifecycle
      onMounted(() => {
        loadCampaign();
      });

      return {
        // Data
        campaign,
        activeTab,
        tabs,
        toast,
        showCouponModal,
        selectedCoupon,

        // Methods
        goBack,
        getCouponName,
        showCouponDetail,
        formatPeriod,
        formatDate,
        getStatusText,
        getStatusBadgeType,
      };
    },
  };
</script>

<style scoped>
  .campaign-detail-container {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }

  .breadcrumb {
    margin-bottom: 16px;
  }

  .back-button {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .coupon-link {
    background: none;
    border: none;
    color: var(--color-primary-main);
    text-decoration: underline;
    cursor: pointer;
    font-size: inherit;
    font-weight: 500;
    transition: color 0.2s ease;
  }

  .coupon-link:hover:not(:disabled) {
    color: var(--color-primary-dark);
  }

  .coupon-link:disabled {
    color: var(--color-text-secondary);
    text-decoration: none;
    cursor: not-allowed;
  }

  .campaign-info-card {
    margin-bottom: 24px;
  }

  .campaign-info {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .campaign-status {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .campaign-basic-info {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }

  .info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .info-item .label {
    font-size: 12px;
    color: var(--color-text-secondary);
    font-weight: 500;
  }

  .info-item .value {
    font-size: 14px;
    color: var(--color-text-primary);
  }

  .tabs-container {
    margin-bottom: 24px;
  }

  .tab-content {
    min-height: 400px;
  }

  .tab-panel {
    animation: fadeIn 0.3s ease-in-out;
  }

  .section-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 20px;
    color: var(--color-text-primary);
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }

  .detail-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 16px;
    background-color: var(--color-bg-secondary);
    border-radius: 8px;
  }

  .detail-item.full-width {
    grid-column: 1 / -1;
  }

  .detail-item .label {
    font-size: 12px;
    color: var(--color-text-secondary);
    font-weight: 500;
    text-transform: uppercase;
  }

  .detail-item .value {
    font-size: 14px;
    color: var(--color-text-primary);
    font-weight: 500;
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;
    color: var(--color-text-secondary);
  }

  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }

  .empty-state h3 {
    font-size: 20px;
    margin-bottom: 8px;
    color: var(--color-text-primary);
  }

  .empty-state p {
    font-size: 14px;
    color: var(--color-text-secondary);
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @media (max-width: 768px) {
    .campaign-detail-container {
      padding: 16px;
    }

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
    }

    .campaign-basic-info {
      grid-template-columns: 1fr;
    }

    .detail-grid {
      grid-template-columns: 1fr;
    }
  }
</style>
