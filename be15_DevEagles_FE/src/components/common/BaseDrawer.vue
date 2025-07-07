<template>
  <Teleport to="body">
    <transition name="drawer-fade">
      <div
        v-if="modelValue"
        class="drawer-backdrop"
        :style="{ zIndex: zIndex }"
        @click.self="closeDrawer"
      >
        <transition name="drawer-slide" @after-leave="$emit('afterLeave')">
          <div v-if="modelValue" :class="['drawer', `drawer-${position}`, `drawer-${size}`]">
            <!-- Drawer Header -->
            <div v-if="$slots.header || title" class="drawer-header">
              <slot name="header">
                <h3 class="drawer-title">{{ title }}</h3>
              </slot>
              <button v-if="closable" class="drawer-close" aria-label="닫기" @click="closeDrawer">
                &times;
              </button>
            </div>

            <!-- Drawer Body -->
            <div class="drawer-body" :class="{ 'no-padding': noPadding }">
              <slot></slot>
            </div>

            <!-- Drawer Footer -->
            <div v-if="$slots.footer" class="drawer-footer">
              <slot name="footer"></slot>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </Teleport>
</template>

<script>
  export default {
    name: 'BaseDrawer',
    props: {
      modelValue: {
        type: Boolean,
        required: true,
      },
      title: {
        type: String,
        default: '',
      },
      position: {
        type: String,
        default: 'right',
        validator: value => ['left', 'right', 'top', 'bottom'].includes(value),
      },
      size: {
        type: String,
        default: 'md',
        validator: value => ['sm', 'md', 'lg', 'xl', 'full'].includes(value),
      },
      closable: {
        type: Boolean,
        default: true,
      },
      maskClosable: {
        type: Boolean,
        default: true,
      },
      noPadding: {
        type: Boolean,
        default: false,
      },
      // [기능 추가] z-index를 외부에서 제어하기 위한 prop
      zIndex: {
        type: Number,
        default: 1000,
      },
    },
    emits: ['update:modelValue', 'close', 'afterLeave'],
    mounted() {
      // ESC 키로 닫기
      this.handleEscape = e => {
        if (e.key === 'Escape' && this.modelValue && this.closable) {
          this.closeDrawer();
        }
      };
      document.addEventListener('keydown', this.handleEscape);
    },
    beforeUnmount() {
      if (this.handleEscape) {
        document.removeEventListener('keydown', this.handleEscape);
      }
    },
    methods: {
      closeDrawer() {
        if (this.maskClosable) {
          this.$emit('update:modelValue', false);
          this.$emit('close');
        }
      },
    },
  };
</script>

<style scoped>
  .drawer-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    /* z-index는 이제 style 바인딩으로 제어됩니다. */
    display: flex;
  }

  .drawer {
    background: var(--color-neutral-white);
    box-shadow: 0 8px 40px -10px rgba(0, 0, 0, 0.15);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative; /* z-index가 backdrop 위에 적용되도록 */
  }

  /* Position Styles */
  .drawer-right {
    margin-left: auto;
    height: 100vh;
  }

  .drawer-left {
    margin-right: auto;
    height: 100vh;
  }

  .drawer-top {
    margin-bottom: auto;
    width: 100vw;
  }

  .drawer-bottom {
    margin-top: auto;
    width: 100vw;
  }

  /* Size Styles */
  .drawer-right.drawer-sm,
  .drawer-left.drawer-sm {
    width: 320px;
  }

  .drawer-right.drawer-md,
  .drawer-left.drawer-md {
    width: 480px;
  }

  .drawer-right.drawer-lg,
  .drawer-left.drawer-lg {
    width: 640px;
  }

  .drawer-right.drawer-xl,
  .drawer-left.drawer-xl {
    width: 800px;
  }

  .drawer-right.drawer-full,
  .drawer-left.drawer-full {
    width: 100vw;
  }

  .drawer-top.drawer-sm,
  .drawer-bottom.drawer-sm {
    height: 240px;
  }

  .drawer-top.drawer-md,
  .drawer-bottom.drawer-md {
    height: 360px;
  }

  .drawer-top.drawer-lg,
  .drawer-bottom.drawer-lg {
    height: 480px;
  }

  .drawer-top.drawer-xl,
  .drawer-bottom.drawer-xl {
    height: 600px;
  }

  .drawer-top.drawer-full,
  .drawer-bottom.drawer-full {
    height: 100vh;
  }

  /* Header */
  .drawer-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1.5rem;
    border-bottom: 1px solid var(--color-gray-200);
    flex-shrink: 0;
  }

  .drawer-title {
    font-size: 18px;
    font-weight: 700;
    line-height: 23.4px;
    margin: 0;
    color: var(--color-neutral-dark);
  }

  .drawer-close {
    background: none;
    border: none;
    font-size: 1.5rem;
    line-height: 1;
    padding: 0;
    cursor: pointer;
    color: var(--color-gray-500);
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 0.25rem;
    transition: all 0.2s ease;
  }

  .drawer-close:hover {
    color: var(--color-gray-800);
    background-color: var(--color-gray-100);
  }

  /* Body */
  .drawer-body {
    flex: 1;
    padding: 1.5rem;
    overflow-y: auto;
  }

  .drawer-body.no-padding {
    padding: 0;
  }

  /* Footer */
  .drawer-footer {
    border-top: 1px solid var(--color-gray-200);
    padding: 1rem;
    display: flex;
    justify-content: center;
    gap: 0.75rem;
  }

  /* Transitions */
  .drawer-fade-enter-active,
  .drawer-fade-leave-active {
    transition: opacity 0.3s ease;
  }

  .drawer-fade-enter-from,
  .drawer-fade-leave-to {
    opacity: 0;
  }

  .drawer-slide-enter-active,
  .drawer-slide-leave-active {
    transition: transform 0.3s ease;
  }

  /* Right Drawer Slide */
  .drawer-right.drawer-slide-enter-from,
  .drawer-right.drawer-slide-leave-to {
    transform: translateX(100%);
  }

  /* Left Drawer Slide */
  .drawer-left.drawer-slide-enter-from,
  .drawer-left.drawer-slide-leave-to {
    transform: translateX(-100%);
  }

  /* Top Drawer Slide */
  .drawer-top.drawer-slide-enter-from,
  .drawer-top.drawer-slide-leave-to {
    transform: translateY(-100%);
  }

  /* Bottom Drawer Slide */
  .drawer-bottom.drawer-slide-enter-from,
  .drawer-bottom.drawer-slide-leave-to {
    transform: translateY(100%);
  }

  /* Responsive */
  @media (max-width: 768px) {
    .drawer-right,
    .drawer-left {
      width: 100vw !important;
    }

    .drawer-top,
    .drawer-bottom {
      height: 100vh !important;
    }

    .drawer-header,
    .drawer-body,
    .drawer-footer {
      padding: 1rem;
    }
  }

  /* Scrollbar Styling */
  .drawer-body::-webkit-scrollbar {
    width: 6px;
  }

  .drawer-body::-webkit-scrollbar-track {
    background: var(--color-gray-100);
  }

  .drawer-body::-webkit-scrollbar-thumb {
    background: var(--color-gray-400);
    border-radius: 3px;
  }

  .drawer-body::-webkit-scrollbar-thumb:hover {
    background: var(--color-gray-500);
  }
</style>
