<template>
  <BaseWindow
    v-model="isVisible"
    :title="`쿠폰 상세 정보 - ${couponData?.name || ''}`"
    width="750px"
  >
    <div v-if="couponData" class="coupon-detail-container">
      <!-- 기본 정보 섹션 -->
      <div class="detail-section">
        <h3 class="section-title">기본 정보</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <label class="detail-label">쿠폰명</label>
            <div class="detail-value">{{ couponData.name }}</div>
          </div>
          <div class="detail-item">
            <label class="detail-label">쿠폰 코드</label>
            <div class="detail-value">
              {{ couponData.couponCode || 'CP' + String(couponData.id).padStart(6, '0') }}
            </div>
          </div>
          <div class="detail-item">
            <label class="detail-label">상태</label>
            <div class="detail-value">
              <BaseBadge :type="couponData.isActive ? 'success' : 'secondary'">
                {{ couponData.isActive ? '활성화' : '비활성화' }}
              </BaseBadge>
            </div>
          </div>
        </div>
      </div>

      <!-- 상품 및 서비스 정보 -->
      <div class="detail-section">
        <h3 class="section-title">상품 및 서비스 정보</h3>
        <div class="detail-grid-2col">
          <div class="detail-item">
            <label class="detail-label">카테고리</label>
            <div class="detail-value">{{ couponData.category || '-' }}</div>
          </div>
          <div class="detail-item">
            <label class="detail-label">디자이너</label>
            <div class="detail-value">{{ couponData.designer || '-' }}</div>
          </div>
          <div class="detail-item">
            <label class="detail-label">1차 상품</label>
            <div class="detail-value">
              {{ couponData.product || couponData.primaryProduct || '-' }}
            </div>
          </div>
          <div class="detail-item">
            <label class="detail-label">2차 상품</label>
            <div class="detail-value">{{ couponData.secondaryProduct || '-' }}</div>
          </div>
        </div>
      </div>

      <!-- 날짜 및 할인 정보 -->
      <div class="detail-section">
        <h3 class="section-title">날짜 및 할인 정보</h3>
        <div class="detail-grid-2col">
          <div class="detail-item">
            <label class="detail-label">만료일</label>
            <div class="detail-value">{{ formatDate(couponData.expiryDate) || '-' }}</div>
          </div>
          <div class="detail-item">
            <label class="detail-label">할인율</label>
            <div class="detail-value">
              <BaseBadge type="success">{{ couponData.discount }}%</BaseBadge>
            </div>
          </div>
          <div class="detail-item">
            <label class="detail-label">생성일</label>
            <div class="detail-value">{{ formatDate(couponData.createdAt) || '-' }}</div>
          </div>
          <div class="detail-item">
            <label class="detail-label">삭제일</label>
            <div class="detail-value">{{ formatDate(couponData.deletedAt) || '-' }}</div>
          </div>
        </div>
      </div>

      <!-- 직원 정보 -->
      <div v-if="couponData.employeeId" class="detail-section">
        <h3 class="section-title">담당자 정보</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <label class="detail-label">담당자 ID</label>
            <div class="detail-value">{{ couponData.employeeId }}</div>
          </div>
        </div>
      </div>
    </div>
  </BaseWindow>
</template>

<script>
  import BaseWindow from '@/components/common/BaseWindow.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import BaseBadge from '@/components/common/BaseBadge.vue';

  export default {
    name: 'CouponDetailModal',
    components: {
      BaseWindow,
      BaseButton,
      BaseBadge,
    },
    props: {
      modelValue: {
        type: Boolean,
        default: false,
      },
      couponData: {
        type: Object,
        default: null,
      },
    },
    emits: ['update:modelValue'],
    computed: {
      isVisible: {
        get() {
          return this.modelValue;
        },
        set(value) {
          this.$emit('update:modelValue', value);
        },
      },
    },
    methods: {
      formatDate(dateString) {
        if (!dateString) return null;
        try {
          const date = new Date(dateString);
          return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
          });
        } catch (error) {
          return dateString;
        }
      },
    },
  };
</script>

<style scoped>
  .coupon-detail-container {
    padding: 12px 0;
  }

  .detail-section {
    margin-bottom: 20px;
  }

  .detail-section:last-child {
    margin-bottom: 0;
  }

  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--color-gray-900, #111827);
    margin-bottom: 10px;
    padding-bottom: 6px;
    border-bottom: 1px solid var(--color-primary-100, #c2cbd9);
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 12px;
  }

  .detail-grid-2col {
    display: grid !important;
    grid-template-columns: 1fr 1fr !important;
    gap: 16px 24px;
    width: 100%;
    min-width: 0;
  }

  .detail-grid-2col .detail-item {
    min-width: 0;
    overflow: hidden;
  }

  .detail-item {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .detail-label {
    font-size: 12px;
    font-weight: 500;
    color: var(--color-gray-600, #4b5563);
  }

  .detail-value {
    font-size: 13px;
    font-weight: 400;
    color: var(--color-gray-900, #111827);
    min-height: 18px;
    display: flex;
    align-items: center;
  }

  /* 반응형 디자인 */
  @media (max-width: 768px) {
    .detail-grid {
      grid-template-columns: 1fr 1fr;
      gap: 8px;
    }

    .detail-grid-2col {
      grid-template-columns: 1fr;
      gap: 12px;
    }

    .coupon-detail-container {
      padding: 8px 0;
    }

    .detail-section {
      margin-bottom: 16px;
    }

    .section-title {
      font-size: 14px;
      margin-bottom: 8px;
    }

    .detail-label {
      font-size: 11px;
    }

    .detail-value {
      font-size: 12px;
      min-height: 16px;
    }
  }
</style>
