<template>
  <form class="campaign-form" @submit.prevent="handleSubmit">
    <!-- 캠페인명 -->
    <BaseForm
      id="campaignName"
      v-model="formData.name"
      label="캠페인명*"
      type="text"
      placeholder="캠페인명을 입력하세요"
      :error="errors.name"
      required
    />

    <!-- 캠페인 설명 -->
    <BaseForm
      id="description"
      v-model="formData.description"
      label="캠페인 설명"
      type="textarea"
      placeholder="캠페인에 대한 설명을 입력하세요"
      :error="errors.description"
    />

    <!-- 캠페인 기간 -->
    <VCalendarWrapper
      v-model="campaignDateRange"
      mode="range"
      label="캠페인 기간*"
      start-placeholder="시작일"
      end-placeholder="종료일"
      :error="errors.dateRange"
      :min-date="new Date()"
      clearable
    />

    <!-- 대상 고객 -->
    <BaseForm
      id="targetAudience"
      v-model="formData.targetAudience"
      label="대상 고객*"
      type="select"
      placeholder="대상 고객을 선택하세요"
      :options="targetAudienceOptions"
      :error="errors.targetAudience"
      required
    />

    <!-- 캠페인 상태 -->
    <BaseForm
      id="status"
      v-model="formData.status"
      label="캠페인 상태*"
      type="select"
      placeholder="캠페인 상태를 선택하세요"
      :options="statusOptions"
      :error="errors.status"
      required
    />

    <!-- 활성화 여부 -->
    <div class="form-group">
      <label class="form-label">활성화 설정</label>
      <div class="checkbox-wrapper">
        <label class="checkbox">
          <input v-model="formData.isActive" type="checkbox" />
          <span>캠페인을 즉시 활성화</span>
        </label>
      </div>
    </div>

    <!-- 버튼들 -->
    <div class="form-actions">
      <BaseButton type="secondary" outline style="flex: 1" @click="handleCancel"> 취소 </BaseButton>
      <BaseButton type="primary" style="flex: 1" @click="handleSubmit"> 저장 </BaseButton>
    </div>
  </form>
</template>

<script>
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import VCalendarWrapper from '@/components/common/VCalendarWrapper.vue';

  export default {
    name: 'CampaignForm',
    components: {
      BaseForm,
      BaseButton,
      VCalendarWrapper,
    },
    props: {},
    emits: ['save', 'cancel'],
    data() {
      return {
        formData: {
          name: '',
          description: '',
          startDate: '',
          endDate: '',
          targetAudience: '',
          status: 'draft',
          isActive: false,
        },
        errors: {},

        // 옵션 데이터
        targetAudienceOptions: [
          { value: 'all', text: '전체 고객' },
          { value: 'new', text: '신규 고객' },
          { value: 'existing', text: '기존 고객' },
          { value: 'vip', text: 'VIP 고객' },
          { value: 'inactive', text: '비활성 고객' },
        ],
        statusOptions: [
          { value: 'draft', text: '임시저장' },
          { value: 'scheduled', text: '예정' },
          { value: 'active', text: '진행중' },
          { value: 'completed', text: '완료' },
          { value: 'cancelled', text: '취소' },
        ],
      };
    },

    computed: {
      campaignDateRange: {
        get() {
          if (this.formData.startDate && this.formData.endDate) {
            return [new Date(this.formData.startDate), new Date(this.formData.endDate)];
          }
          return null;
        },
        set(value) {
          if (value && Array.isArray(value) && value.length === 2) {
            this.formData.startDate = value[0].toISOString().split('T')[0];
            this.formData.endDate = value[1].toISOString().split('T')[0];
          } else {
            this.formData.startDate = '';
            this.formData.endDate = '';
          }
        },
      },
    },

    methods: {
      validateForm() {
        this.errors = {};

        if (!this.formData.name.trim()) {
          this.errors.name = '캠페인명은 필수입니다.';
        }

        if (!this.formData.startDate || !this.formData.endDate) {
          this.errors.dateRange = '캠페인 기간 선택은 필수입니다.';
        }

        if (!this.formData.targetAudience) {
          this.errors.targetAudience = '대상 고객 선택은 필수입니다.';
        }

        if (!this.formData.status) {
          this.errors.status = '캠페인 상태 선택은 필수입니다.';
        }

        // 날짜 유효성 검사
        if (this.formData.startDate && this.formData.endDate) {
          const startDate = new Date(this.formData.startDate);
          const endDate = new Date(this.formData.endDate);

          if (endDate <= startDate) {
            this.errors.dateRange = '종료일은 시작일보다 늦어야 합니다.';
          }

          // 시작일이 과거인지 확인 (draft 상태가 아닌 경우)
          if (this.formData.status !== 'draft') {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            startDate.setHours(0, 0, 0, 0);

            if (startDate < today) {
              this.errors.dateRange = '시작일은 오늘 이후여야 합니다.';
            }
          }
        }

        return Object.keys(this.errors).length === 0;
      },

      handleSubmit() {
        if (this.validateForm()) {
          const campaignData = {
            ...this.formData,
          };

          this.$emit('save', campaignData);
        }
      },

      handleCancel() {
        this.$emit('cancel');
      },

      resetForm() {
        this.formData = {
          name: '',
          description: '',
          startDate: '',
          endDate: '',
          targetAudience: '',
          status: 'draft',
          isActive: false,
        };
        this.errors = {};
      },
    },
  };
</script>

<style scoped>
  .campaign-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-label {
    font-size: 14px;
    font-weight: 700;
    color: var(--color-gray-700);
  }

  .checkbox-wrapper {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .checkbox {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    font-size: 14px;
    color: var(--color-gray-700);
  }

  .checkbox input[type='checkbox'] {
    width: auto;
    margin: 0;
    accent-color: var(--color-primary-main);
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    margin-top: 1rem;
  }

  @media (max-width: 768px) {
    .form-actions {
      flex-direction: column;
    }
  }
</style>
