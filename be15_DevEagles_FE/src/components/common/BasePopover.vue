<template>
  <div v-if="show" class="popover-backdrop" @click.self="handleBackdropClick">
    <div
      ref="popover"
      :class="['popover', `popover-${placement}`, { 'popover-small': size === 'sm' }]"
      :style="popoverStyle"
    >
      <!-- Arrow -->
      <div class="popover-arrow"></div>

      <!-- Content -->
      <div class="popover-content">
        <div v-if="title" class="popover-title">{{ title }}</div>
        <div class="popover-message">
          <slot>{{ message }}</slot>
        </div>

        <div v-if="showActions" class="popover-actions">
          <BaseButton type="secondary" size="xs" @click="handleCancel">
            {{ cancelText }}
          </BaseButton>
          <BaseButton :type="confirmType" size="xs" @click="handleConfirm">
            {{ confirmText }}
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import BaseButton from './BaseButton.vue';

  export default {
    name: 'BasePopover',
    components: {
      BaseButton,
    },
    props: {
      modelValue: {
        type: Boolean,
        default: false,
      },
      title: {
        type: String,
        default: '',
      },
      message: {
        type: String,
        default: '',
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
        default: 'error',
      },
      placement: {
        type: String,
        default: 'top',
        validator: value => ['top', 'bottom', 'left', 'right'].includes(value),
      },
      size: {
        type: String,
        default: 'sm',
        validator: value => ['sm', 'md'].includes(value),
      },
      showActions: {
        type: Boolean,
        default: true,
      },
      triggerElement: {
        type: [Element, Object],
        default: null,
      },
      maskClosable: {
        type: Boolean,
        default: true,
      },
    },
    emits: ['update:modelValue', 'confirm', 'cancel'],
    data() {
      return {
        popoverStyle: {},
      };
    },
    computed: {
      show: {
        get() {
          return this.modelValue;
        },
        set(value) {
          this.$emit('update:modelValue', value);
        },
      },
    },
    watch: {
      show(newVal) {
        if (newVal) {
          this.$nextTick(() => {
            this.updatePosition();
          });
        }
      },
    },
    methods: {
      updatePosition() {
        if (!this.triggerElement || !this.$refs.popover) return;

        const trigger = this.triggerElement;
        const popover = this.$refs.popover;
        const triggerRect = trigger.getBoundingClientRect();
        const popoverRect = popover.getBoundingClientRect();

        let top, left;

        switch (this.placement) {
          case 'top':
            top = triggerRect.top - popoverRect.height - 8;
            left = triggerRect.left + triggerRect.width / 2 - popoverRect.width / 2;
            break;
          case 'bottom':
            top = triggerRect.bottom + 8;
            left = triggerRect.left + triggerRect.width / 2 - popoverRect.width / 2;
            break;
          case 'left':
            top = triggerRect.top + triggerRect.height / 2 - popoverRect.height / 2;
            left = triggerRect.left - popoverRect.width - 8;
            break;
          case 'right':
            top = triggerRect.top + triggerRect.height / 2 - popoverRect.height / 2;
            left = triggerRect.right + 8;
            break;
        }

        // 화면 경계 체크
        const viewport = {
          width: window.innerWidth,
          height: window.innerHeight,
        };

        if (left < 8) left = 8;
        if (left + popoverRect.width > viewport.width - 8) {
          left = viewport.width - popoverRect.width - 8;
        }
        if (top < 8) top = 8;
        if (top + popoverRect.height > viewport.height - 8) {
          top = viewport.height - popoverRect.height - 8;
        }

        this.popoverStyle = {
          position: 'fixed',
          top: `${top}px`,
          left: `${left}px`,
          zIndex: 1050,
        };
      },

      handleConfirm() {
        this.$emit('confirm');
        this.show = false;
      },

      handleCancel() {
        this.$emit('cancel');
        this.show = false;
      },

      handleBackdropClick() {
        if (this.maskClosable) {
          this.handleCancel();
        }
      },
    },
  };
</script>

<style scoped>
  .popover-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1040;
    background: transparent;
  }

  .popover {
    background: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    border-radius: 0.5rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    max-width: 300px;
    animation: popoverFadeIn 0.15s ease-out;
  }

  .popover-small {
    max-width: 250px;
  }

  .popover-arrow {
    position: absolute;
    width: 8px;
    height: 8px;
    background: var(--color-neutral-white);
    border: 1px solid var(--color-gray-200);
    transform: rotate(45deg);
  }

  .popover-top .popover-arrow {
    bottom: -5px;
    left: 50%;
    margin-left: -4px;
    border-top: none;
    border-left: none;
  }

  .popover-bottom .popover-arrow {
    top: -5px;
    left: 50%;
    margin-left: -4px;
    border-bottom: none;
    border-right: none;
  }

  .popover-left .popover-arrow {
    right: -5px;
    top: 50%;
    margin-top: -4px;
    border-left: none;
    border-bottom: none;
  }

  .popover-right .popover-arrow {
    left: -5px;
    top: 50%;
    margin-top: -4px;
    border-right: none;
    border-top: none;
  }

  .popover-content {
    padding: 0.75rem;
  }

  .popover-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-neutral-dark);
    margin-bottom: 0.5rem;
  }

  .popover-message {
    font-size: 13px;
    line-height: 1.4;
    color: var(--color-gray-700);
    margin-bottom: 0.75rem;
  }

  .popover-actions {
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end;
  }

  @keyframes popoverFadeIn {
    from {
      opacity: 0;
      transform: scale(0.95);
    }
    to {
      opacity: 1;
      transform: scale(1);
    }
  }
</style>
