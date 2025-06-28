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
    <PrimeDatePicker
      v-model="campaignDateRange"
      selection-mode="range"
      label="캠페인 기간*"
      placeholder="캠페인 기간을 선택하세요"
      :min-date="new Date()"
      :error="errors.dateRange"
      clearable
    />

    <!-- 대상 고객 등급 & 대상 고객 태그 (2열) -->
    <div class="form-row">
      <BaseForm
        id="targetGrade"
        v-model="formData.targetGrade"
        label="대상 고객 등급*"
        type="select"
        placeholder="대상 고객 등급을 선택하세요"
        :options="targetGradeOptions"
        :error="errors.targetGrade"
        required
      />
      <BaseForm
        id="targetTag"
        v-model="formData.targetTag"
        label="대상 고객 태그"
        type="select"
        placeholder="대상 고객 태그를 선택하세요"
        :options="targetTagOptions"
        :error="errors.targetTag"
      />
    </div>

    <!-- 활성화 여부 -->
    <div class="form-group">
      <label class="form-label">활성화 설정</label>
      <div class="checkbox-wrapper">
        <div class="checkbox">
          <input id="isActive" v-model="formData.isActive" type="checkbox" />
          <label for="isActive">캠페인을 즉시 활성화</label>
        </div>
      </div>
    </div>

    <!-- 버튼들 -->
    <div class="form-actions">
      <BaseButton type="secondary" outline style="flex: 1" html-type="button" @click="handleCancel">
        취소
      </BaseButton>
      <BaseButton type="primary" style="flex: 1" html-type="submit"> 저장 </BaseButton>
    </div>
  </form>
</template>

<script>
  import BaseForm from '@/components/common/BaseForm.vue';
  import BaseButton from '@/components/common/BaseButton.vue';
  import PrimeDatePicker from '@/components/common/PrimeDatePicker.vue';

  export default {
    name: 'CampaignForm',
    components: {
      BaseForm,
      BaseButton,
      PrimeDatePicker,
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
          targetGrade: '',
          targetTag: '',
          isActive: false,
        },
        errors: {},

        // 옵션 데이터
        targetGradeOptions: [
          { value: 'bronze', text: '브론즈' },
          { value: 'silver', text: '실버' },
          { value: 'gold', text: '골드' },
          { value: 'platinum', text: '플래티넘' },
          { value: 'diamond', text: '다이아몬드' },
          { value: 'vip', text: 'VIP' },
        ],
        targetTagOptions: [
          { value: 'new_customer', text: '신규 고객' },
          { value: 'loyal_customer', text: '단골 고객' },
          { value: 'inactive_customer', text: '비활성 고객' },
          { value: 'high_spender', text: '고액 결제 고객' },
          { value: 'frequent_visitor', text: '방문 빈도 높은 고객' },
          { value: 'birthday_month', text: '생일월 고객' },
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
            // 각 날짜가 null이 아닌지 확인
            if (value[0] && value[1]) {
              this.formData.startDate = this.formatDateToLocal(value[0]);
              this.formData.endDate = this.formatDateToLocal(value[1]);
            } else if (value[0]) {
              // 첫 번째 날짜만 선택된 경우
              this.formData.startDate = this.formatDateToLocal(value[0]);
              this.formData.endDate = '';
            }
          } else {
            this.formData.startDate = '';
            this.formData.endDate = '';
          }
        },
      },
    },

    methods: {
      formatDateToLocal(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      },

      validateForm() {
        this.errors = {};

        if (!this.formData.name.trim()) {
          this.errors.name = '캠페인명은 필수입니다.';
        }

        if (!this.formData.startDate || !this.formData.endDate) {
          this.errors.dateRange = '캠페인 기간 선택은 필수입니다.';
        }

        if (!this.formData.targetGrade) {
          this.errors.targetGrade = '대상 고객 등급 선택은 필수입니다.';
        }

        // 날짜 유효성 검사
        if (this.formData.startDate && this.formData.endDate) {
          const startDate = new Date(this.formData.startDate);
          const endDate = new Date(this.formData.endDate);

          if (endDate <= startDate) {
            this.errors.dateRange = '종료일은 시작일보다 늦어야 합니다.';
          }

          // 시작일이 과거인지 확인
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          startDate.setHours(0, 0, 0, 0);

          if (startDate < today) {
            this.errors.dateRange = '시작일은 오늘 이후여야 합니다.';
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
          targetGrade: '',
          targetTag: '',
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
    gap: 1rem;
  }

  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
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
    font-size: 14px;
    color: var(--color-gray-700);
  }

  .checkbox input[type='checkbox'] {
    width: auto;
    margin: 0;
    accent-color: var(--color-primary-main);
    cursor: pointer;
  }

  .checkbox label {
    cursor: pointer;
    margin: 0;
    font-size: 14px;
    color: var(--color-gray-700);
  }

  .form-actions {
    display: flex;
    gap: 1rem;
    margin-top: 1rem;
  }

  @media (max-width: 768px) {
    .form-row {
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }

    .form-actions {
      flex-direction: column;
    }
  }
</style>
