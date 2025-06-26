<template>
  <div v-if="modelValue" class="window-overlay" @click="handleOverlayClick">
    <div class="window-container" :style="windowStyle" @click.stop>
      <!-- Window Header -->
      <div class="window-header">
        <div class="window-title">{{ title }}</div>
        <div class="window-controls">
          <button class="window-close-btn" @click="closeWindow">
            <span class="close-icon">×</span>
          </button>
        </div>
      </div>

      <!-- Window Content -->
      <div class="window-content" :style="contentStyle">
        <div class="content-container">
          <slot></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'BaseWindow',
    props: {
      modelValue: {
        type: Boolean,
        default: false,
      },
      title: {
        type: String,
        default: '',
      },
      width: {
        type: String,
        default: '600px',
      },
      minHeight: {
        type: String,
        default: '300px',
      },
    },
    emits: ['update:modelValue', 'close'],
    computed: {
      windowStyle() {
        return {
          width: this.width,
          minHeight: this.minHeight,
          maxHeight: '95vh',
        };
      },
      contentStyle() {
        const minContentHeight = parseInt(this.minHeight) - 80; // 80px는 헤더 높이
        return {
          minHeight: minContentHeight > 0 ? `${minContentHeight}px` : undefined,
          maxHeight: 'calc(95vh - 80px)',
        };
      },
    },
    methods: {
      closeWindow() {
        this.$emit('update:modelValue', false);
        this.$emit('close');
      },
      handleOverlayClick() {
        this.closeWindow();
      },
    },
  };
</script>

<style scoped>
  .window-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(2px);
  }

  .window-container {
    background: var(--color-neutral-white);
    border-radius: 12px;
    box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.15),
      0 4px 16px rgba(0, 0, 0, 0.1);
    max-width: 90vw;
    overflow: hidden;
    transform: scale(1);
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    border: 1px solid var(--color-gray-200);
  }

  .window-header {
    background: var(--color-gray-100);
    padding: 16px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    user-select: none;
  }

  .window-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-gray-800);
    margin: 0;
    flex: 1;
  }

  .window-controls {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .window-close-btn {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: var(--color-secondary-main);
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    box-shadow: 0 2px 4px rgba(252, 81, 133, 0.2);
  }

  .window-close-btn:hover {
    background: var(--color-secondary-400);
    transform: scale(1.1);
    box-shadow: 0 4px 8px rgba(252, 81, 133, 0.3);
  }

  .window-close-btn:active {
    transform: scale(0.95);
  }

  .close-icon {
    color: var(--color-neutral-white);
    font-size: 14px;
    font-weight: bold;
    line-height: 1;
  }

  .window-content {
    padding: 16px;
    overflow-y: auto;
    background: var(--color-gray-100);
  }

  .content-container {
    background: var(--color-neutral-white);
    border-radius: 12px;
    padding: 20px;
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  /* 스크롤바 스타일링 */
  .window-content::-webkit-scrollbar {
    width: 6px;
  }

  .window-content::-webkit-scrollbar-track {
    background: var(--color-gray-100);
    border-radius: 3px;
  }

  .window-content::-webkit-scrollbar-thumb {
    background: var(--color-gray-300);
    border-radius: 3px;
  }

  .window-content::-webkit-scrollbar-thumb:hover {
    background: var(--color-gray-400);
  }

  /* 애니메이션 */
  .window-overlay {
    animation: fadeIn 0.3s ease-out;
  }

  .window-container {
    animation: slideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes slideIn {
    from {
      opacity: 0;
      transform: scale(0.8) translateY(-20px);
    }
    to {
      opacity: 1;
      transform: scale(1) translateY(0);
    }
  }

  /* 반응형 */
  @media (max-width: 768px) {
    .window-container {
      width: 95vw;
      margin: 20px;
    }

    .window-header {
      padding: 12px 16px;
    }

    .window-title {
      font-size: 14px;
    }

    .window-content {
      padding: 12px;
    }

    .content-container {
      padding: 16px;
      border-radius: 8px;
    }

    .window-close-btn {
      width: 18px;
      height: 18px;
    }

    .close-icon {
      font-size: 12px;
    }
  }
</style>
