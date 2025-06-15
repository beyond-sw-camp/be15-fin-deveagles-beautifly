<template>
  <div v-if="show" :class="['loading-container', { overlay: overlay }]">
    <div class="loading-content">
      <!-- Spinner -->
      <div :class="['spinner', `spinner-${size}`]" :style="spinnerStyle">
        <div class="spinner-circle"></div>
      </div>

      <!-- Loading Text -->
      <div v-if="text" class="loading-text" :class="textClass">
        {{ text }}
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'BaseLoading',
    props: {
      show: {
        type: Boolean,
        default: true,
      },
      text: {
        type: String,
        default: '',
      },
      size: {
        type: String,
        default: 'md',
        validator: value => ['sm', 'md', 'lg'].includes(value),
      },
      color: {
        type: String,
        default: 'primary',
        validator: value => ['primary', 'secondary', 'white'].includes(value),
      },
      overlay: {
        type: Boolean,
        default: false,
      },
    },
    computed: {
      spinnerStyle() {
        const colorMap = {
          primary: 'var(--color-primary-main)',
          secondary: 'var(--color-secondary-main)',
          white: 'var(--color-neutral-white)',
        };
        return {
          borderTopColor: colorMap[this.color],
        };
      },
      textClass() {
        return `font-${this.size === 'sm' ? 'small' : this.size === 'lg' ? 'section-inner' : 'body'}`;
      },
    },
  };
</script>

<style scoped>
  .loading-container {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
  }

  .loading-container.overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    z-index: 9999;
  }

  .loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  .spinner {
    border-radius: 50%;
    border: 3px solid var(--color-gray-200);
    border-top-color: var(--color-primary-main);
    animation: spin 1s linear infinite;
  }

  .spinner-sm {
    width: 20px;
    height: 20px;
    border-width: 2px;
  }

  .spinner-md {
    width: 32px;
    height: 32px;
    border-width: 3px;
  }

  .spinner-lg {
    width: 48px;
    height: 48px;
    border-width: 4px;
  }

  .loading-text {
    color: var(--color-gray-600);
    text-align: center;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
</style>
