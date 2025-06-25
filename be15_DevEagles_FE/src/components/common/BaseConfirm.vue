<template>
  <BaseModal v-model="show" :title="title" @update:model-value="handleClose">
    <div class="confirm-content">
      <div v-if="icon" class="confirm-icon">
        <component :is="iconComponent" :size="48" />
      </div>
      <div class="confirm-message">
        <slot>{{ message }}</slot>
      </div>
    </div>

    <template #footer>
      <div class="confirm-actions">
        <BaseButton type="secondary" outline @click="handleCancel">
          {{ cancelText }}
        </BaseButton>
        <BaseButton :type="confirmType" @click="handleConfirm">
          {{ confirmText }}
        </BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script>
  import BaseModal from './BaseModal.vue';
  import BaseButton from './BaseButton.vue';

  export default {
    name: 'BaseConfirm',
    components: {
      BaseModal,
      BaseButton,
    },
    props: {
      modelValue: {
        type: Boolean,
        default: false,
      },
      title: {
        type: String,
        default: '확인',
      },
      message: {
        type: String,
        default: '이 작업을 계속하시겠습니까?',
      },
      confirmText: {
        type: String,
        default: '확인',
      },
      cancelText: {
        type: String,
        default: '취소',
      },
      confirmType: {
        type: String,
        default: 'primary',
        validator: value =>
          ['primary', 'secondary', 'success', 'warning', 'error', 'info'].includes(value),
      },
      icon: {
        type: Boolean,
        default: true,
      },
      iconType: {
        type: String,
        default: 'warning',
        validator: value => ['info', 'success', 'warning', 'error'].includes(value),
      },
    },
    emits: ['update:modelValue', 'confirm', 'cancel'],
    computed: {
      show: {
        get() {
          return this.modelValue;
        },
        set(value) {
          this.$emit('update:modelValue', value);
        },
      },
      iconComponent() {
        const iconMap = {
          info: 'InfoIcon',
          success: 'CheckCircleIcon',
          warning: 'AlertTriangleIcon',
          error: 'XCircleIcon',
        };
        return iconMap[this.iconType] || 'AlertTriangleIcon';
      },
    },
    methods: {
      handleConfirm() {
        this.$emit('confirm');
        this.show = false;
      },
      handleCancel() {
        this.$emit('cancel');
        this.show = false;
      },
      handleClose() {
        this.$emit('cancel');
      },
    },
  };
</script>

<style scoped>
  .confirm-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 1rem 0;
  }

  .confirm-icon {
    margin-bottom: 1rem;
    color: var(--color-warning-500);
  }

  .confirm-icon[data-type='error'] {
    color: var(--color-error-500);
  }

  .confirm-icon[data-type='success'] {
    color: var(--color-success-500);
  }

  .confirm-icon[data-type='info'] {
    color: var(--color-info-500);
  }

  .confirm-message {
    font-size: 16px;
    line-height: 1.5;
    color: var(--color-neutral-dark);
    margin-bottom: 1rem;
    white-space: pre-line; /* 줄바꿈 적용 */
  }

  .confirm-actions {
    display: flex;
    gap: 0.75rem;
    justify-content: flex-end;
  }

  @media (max-width: 768px) {
    .confirm-actions {
      flex-direction: column;
    }
  }
</style>
